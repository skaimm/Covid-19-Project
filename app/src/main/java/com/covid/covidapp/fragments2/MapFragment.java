package com.covid.covidapp.fragments2;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.covid.covidapp.LoadingDialog;
import com.covid.covidapp.R;
import com.covid.covidapp.models.User;
import com.covid.covidapp.models.UserLoc;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private GoogleMap mMap;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private String userID;
    private double uLat,uLng;
    private ArrayList<UserLoc> mUserLoc;
    private ArrayList<User> mUser;
    private SearchView searchView;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        loadingDialog= new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        userID = getArguments().getString(ARG_PARAM1, "bilinmiyor");
        mUserLoc = new ArrayList<>();
        searchView = v.findViewById(R.id.searchlocation);
        setupFirebaseAuth();
        return v;
    }


     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */

    private void loadMap(){
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;
                LatLng camera = new LatLng(uLat,uLng);
                for(int i =0; i<mUserLoc.size();i++){

                    // Add a marker in Sydney and move the camera
                    LatLng sydney = new LatLng(mUserLoc.get(i).getLat(), mUserLoc.get(i).getLng());
                    if(mUserLoc.get(i).getRisk().equals(getResources().getString(R.string.db_yuksek_riskli)))
                        mMap.addMarker(new MarkerOptions().position(sydney).icon(customDescriptor(getContext(),R.drawable.ic_yuksek_person)));
                    else if(mUserLoc.get(i).getRisk().equals(getResources().getString(R.string.db_orta_yuksek_riskli)))
                        mMap.addMarker(new MarkerOptions().position(sydney).icon(customDescriptor(getContext(),R.drawable.ic_ortayuksek_person)));
                    else if (mUserLoc.get(i).getRisk().equals(getResources().getString(R.string.db_orta_riskli)))
                        mMap.addMarker(new MarkerOptions().position(sydney).icon(customDescriptor(getContext(),R.drawable.ic_orta_person)));
                    else if(mUserLoc.get(i).getRisk().equals(getResources().getString(R.string.db_orta_dusuk_riskli)))
                        mMap.addMarker(new MarkerOptions().position(sydney).icon(customDescriptor(getContext(),R.drawable.ic_ortadusuk_person)));
                    else if(mUserLoc.get(i).getRisk().equals(getResources().getString(R.string.db_dusuk_riskli)))
                        mMap.addMarker(new MarkerOptions().position(sydney).icon(customDescriptor(getContext(),R.drawable.ic_dusuk_person)));
                    else
                        mMap.addMarker(new MarkerOptions().position(sydney).icon(customDescriptor(getContext(),R.drawable.ic_bilinmiyor_person)));

                }

               // mMap.moveCamera(CameraUpdateFactory.newLatLng(camera));
                loadingDialog.dissmissDialog();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera,15));


                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        String loca = searchView.getQuery().toString();
                        List<Address> addressList = null;

                        if(loca !=null || !loca.equals("")){
                            Geocoder geocoder = new Geocoder(getContext());
                            try {
                                addressList = geocoder.getFromLocationName(loca,1);

                            }catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(!addressList.isEmpty()){
                                Address address = addressList.get(0);
                                address = addressList.get(0);
                                LatLng ll = new LatLng(address.getLatitude(),address.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll,15));
                            }else {
                                final Dialog customDialog2 = new Dialog(getContext());
                                customDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                Objects.requireNonNull(customDialog2.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                customDialog2.setContentView(R.layout.dialog_congrats);
                                customDialog2.setCanceledOnTouchOutside(false);
                                TextView title = (TextView) customDialog2.findViewById(R.id.tv_title);
                                TextView message = (TextView) customDialog2.findViewById(R.id.tv_message);
                                Button yes = (Button) customDialog2.findViewById(R.id.btn_yes);

                                title.setText(getResources().getString(R.string.warning));
                                message.setText(getResources().getString(R.string.warmap));
                                yes.setText(getResources().getString(R.string.okay));

                                yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        customDialog2.cancel();
                                    }
                                });
                                customDialog2.show();
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
            }
        });
    }

    private BitmapDescriptor customDescriptor(Context context,int vectorResId){
        Drawable vector = ContextCompat.getDrawable(context,vectorResId);
        vector.setBounds(0,0,vector.getIntrinsicWidth(),vector.getIntrinsicHeight());
        Bitmap bitmap=Bitmap.createBitmap(vector.getIntrinsicWidth(),vector.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        vector.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                } else {
                }

            }
        };


        myRef.child(getResources().getString(R.string.db_location)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot locations : dataSnapshot.getChildren()){
                    mUserLoc.add(locations.getValue(UserLoc.class));
                    if(locations.getKey().equals(userID)){
                        uLat = mUserLoc.get(mUserLoc.size()-1).getLat();
                        uLng = mUserLoc.get(mUserLoc.size()-1).getLng();
                    }
                }
                loadMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
