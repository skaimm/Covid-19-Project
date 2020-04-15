package com.covid.covidapp.fragments2;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.covid.covidapp.LoadingDialog;
import com.covid.covidapp.R;
import com.covid.covidapp.models.CityRisk;
import com.covid.covidapp.models.CountryCase;
import com.covid.covidapp.models.CountryRisk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatFragment newInstance(String param1, String param2) {
        StatFragment fragment = new StatFragment();
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


    private TextView s1,d1,s2,d2,s3,d3,s4,d4,s5,d5,s6,d6,title;
    private Button btn_risk,btn_vaka;
    private PowerSpinnerView spinner_country,spinner_cities;
    private ArrayList<CountryCase> countryCases;
    private ArrayList<CountryRisk> countryRisks;

    private ArrayList<String> nameCities;
    Map<String, String> ISO3vCountry;

    private ArrayList<String> countries;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private LoadingDialog loadingDialog;

    private String userID,selectedCountry,selectedCity,currentlyLoc="TUR";

    @SuppressLint({"CutPasteId", "ResourceAsColor"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stat, container, false);
        loadingDialog= new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        selectedCountry= getResources().getString(R.string.ISOGLOBAL);
        selectedCity = getResources().getString(R.string.GENERAL);
        initAndLoad(view);
        userID = getArguments().getString(ARG_PARAM1, getResources().getString(R.string.db_bilinmiyor));

        setupFirebaseAuth();

        return view;
    }

    private void getCountriesName(){
        String[] isoCountries = Locale.getISOCountries();
        for (String country : isoCountries) {

            Locale name = new Locale.Builder().setRegion(country/*Japan*/).build();
            ISO3vCountry.put(name.getDisplayCountry(Locale.forLanguageTag(Locale.getDefault().toLanguageTag())),name.getISO3Country());
        }
        countries = new ArrayList<String>(ISO3vCountry.keySet());
        Collections.sort(countries);
        Collections.reverse(countries);
        countries.add(getResources().getString(R.string.GLOBAL));
        Collections.reverse(countries);
    }

    private int getCountrynameFromISO(String iso){
        String[] isoCountries = Locale.getISOCountries();
        for (String country : isoCountries) {

            Locale name = new Locale.Builder().setRegion(country/*Japan*/).build();
            if(name.getISO3Country().equals(iso)){
                String c1 = name.getDisplayCountry(Locale.forLanguageTag(Locale.getDefault().toLanguageTag()));
                int index = countries.indexOf(c1);
                return index;
            }
        }
        return 0;
    }

    private void initAndLoad(View v){
        spinner_country = v.findViewById(R.id.spinner_countries);
        spinner_cities = v.findViewById(R.id.spinner_cities);
        title = v.findViewById(R.id.tv_title);
        s1 = v.findViewById(R.id.tv_1s);
        s2 = v.findViewById(R.id.tv_2s);
        s3 = v.findViewById(R.id.tv_3s);
        s4 = v.findViewById(R.id.tv_4s);
        s5 = v.findViewById(R.id.tv_5s);
        s6 = v.findViewById(R.id.tv_6s);
        d1 = v.findViewById(R.id.tv_1d);
        d2 = v.findViewById(R.id.tv_2d);
        d3 = v.findViewById(R.id.tv_3d);
        d4 = v.findViewById(R.id.tv_4d);
        d5 = v.findViewById(R.id.tv_5d);
        d6 = v.findViewById(R.id.tv_6d);
        btn_risk = v.findViewById(R.id.btn_risk);
        btn_vaka = v.findViewById(R.id.btn_vaka);

        ISO3vCountry = new HashMap<String, String>();
        nameCities = new ArrayList<>();
        countryCases = new ArrayList<>();
        countryRisks = new ArrayList<>();

        getCountriesName();
        spinner_country.setItems(countries);
    }


    private void getGlobalCases(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://corona.lmao.ninja/all";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //progressbar
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String cases = jsonObject.getString("cases");
                    String deaths = jsonObject.getString("deaths");
                    String todayCase = jsonObject.getString("todayCases");
                    String recover = jsonObject.getString("recovered");
                    String active = jsonObject.getString("active");
                    String todayDeath = jsonObject.getString("todayDeaths");

                    title.setText(getResources().getString(R.string.coronavirus) + getResources().getString(R.string.GLOBAL));
                    s1.setText("Toplam Vaka");
                    d1.setText(cases);
                    s1.setTextColor(getResources().getColor(R.color.toplamvaka));
                    d1.setTextColor(getResources().getColor(R.color.toplamvaka));

                    s6.setText("Toplam Ölüm");
                    d6.setText(deaths);
                    s6.setTextColor(getResources().getColor(R.color.toplamolum));
                    d6.setTextColor(getResources().getColor(R.color.toplamolum));

                    s3.setText("Yeni Ölüm");
                    d3.setText(todayDeath);
                    s3.setTextColor(getResources().getColor(R.color.yeniolum));
                    d3.setTextColor(getResources().getColor(R.color.yeniolum));

                    s5.setText("İyileşen");
                    d5.setText(recover);
                    s5.setTextColor(getResources().getColor(R.color.aktifvaka));
                    d5.setTextColor(getResources().getColor(R.color.aktifvaka));

                    s4.setText("Aktif Vaka");
                    d4.setText(active);
                    s4.setTextColor(getResources().getColor(R.color.ortayuksekrisk));
                    d4.setTextColor(getResources().getColor(R.color.ortayuksekrisk));

                    s2.setText("Bugün Vaka");
                    d2.setText(todayCase);
                    s2.setTextColor(getResources().getColor(R.color.yenivaka));
                    d2.setTextColor(getResources().getColor(R.color.yenivaka));

                   // setupPieChart(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Yükleme Hatası",error.toString());
            }
        });

        queue.add(stringRequest);
    }


    private void getDataFromServer(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://corona.lmao.ninja/countries";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {
                //progressbar
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i=0; i<jsonArray.length();i++){
                        JSONObject data = jsonArray.getJSONObject(i);
                        countryCases.add(new CountryCase(data.getJSONObject("countryInfo").getString("iso3"),data.getString("cases"),data.getString("todayCases"),
                                data.getString("deaths"),data.getString("todayDeaths"),data.getString("active"),data.getString("recovered")));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CountryCase a = countryCases.get(0);
                String coede = a.getCountry();

                loadingDialog.dissmissDialog();
                spinner_country.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
                    @Override public void onItemSelected(final int position, final String item) {
                        if(getResources().getString(R.string.GLOBAL).equals(item)){
                            selectedCountry = getResources().getString(R.string.ISOGLOBAL);
                        }else{
                            selectedCountry = ISO3vCountry.get(item);
                        }
                        nameCities.clear();

                        spinner_cities.setVisibility(View.INVISIBLE);
                        btn_vaka.setBackground(getResources().getDrawable(R.drawable.background_text));
                        btn_risk.setBackground(getResources().getDrawable(R.drawable.background_button));

                        pressedCase(position);

                        nameCities.add(getResources().getString(R.string.GENERAL));

                        for(int i =0 ;i<countryRisks.size();i++){
                            if(countryRisks.get(i).getCountry().equals(selectedCountry)){
                                for(int j=0;j<countryRisks.get(i).getCities().size();j++){
                                    nameCities.add(countryRisks.get(i).getCities().get(j).getCity());
                                }
                                break;
                            }
                        }

                        spinner_cities.setItems(nameCities);

                        spinner_cities.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
                            @Override public void onItemSelected(int position2, String item) {
                                selectedCity = item;
                                pressedRisk(position);

                            }
                        });

                        btn_risk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                spinner_cities.selectItemByIndex(0);
                                if(nameCities.size()>0)
                                    spinner_cities.setVisibility(View.VISIBLE);
                                btn_risk.setBackground(getResources().getDrawable(R.drawable.background_text));
                                btn_vaka.setBackground(getResources().getDrawable(R.drawable.background_button));
                                pressedRisk(position);

                            }
                        });
                       //spinner_cities.selectItemByIndex(Arrays.asList(cityname).indexOf("istanbul"));
                        btn_vaka.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                spinner_cities.setVisibility(View.INVISIBLE);
                                btn_vaka.setBackground(getResources().getDrawable(R.drawable.background_text));
                                btn_risk.setBackground(getResources().getDrawable(R.drawable.background_button));
                                pressedCase(position);
                            }
                        });



                    }
                });

                spinner_country.selectItemByIndex(getCountrynameFromISO(currentlyLoc));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Yükleme Hatası",error.toString());
            }
        });

        queue.add(stringRequest);
    }

    private void pressedCase(int in){
        if(getResources().getString(R.string.ISOGLOBAL).equals(selectedCountry)){
            getGlobalCases();

        }else{
            boolean noCountry = true;

            title.setText(getResources().getString(R.string.coronavirus)+ countries.get(in));

            s1.setText(getResources().getString(R.string.cases));
            s2.setText(getResources().getString(R.string.activecases));
            s3.setText(getResources().getString(R.string.recoveredcases));
            s4.setText(getResources().getString(R.string.todaycases));
            s5.setText(getResources().getString(R.string.todaydeaths));
            s6.setText(getResources().getString(R.string.deaths));

            s1.setTextColor(getResources().getColor(R.color.toplamvaka));
            s2.setTextColor(getResources().getColor(R.color.ortayuksekrisk));
            s3.setTextColor(getResources().getColor(R.color.aktifvaka));
            s4.setTextColor(getResources().getColor(R.color.yenivaka));
            s5.setTextColor(getResources().getColor(R.color.yeniolum));
            s6.setTextColor(getResources().getColor(R.color.toplamolum));

            d1.setTextColor(getResources().getColor(R.color.toplamvaka));
            d2.setTextColor(getResources().getColor(R.color.ortayuksekrisk));
            d3.setTextColor(getResources().getColor(R.color.aktifvaka));
            d4.setTextColor(getResources().getColor(R.color.yenivaka));
            d5.setTextColor(getResources().getColor(R.color.yeniolum));
            d6.setTextColor(getResources().getColor(R.color.toplamolum));

            for(int i=0;i<countryCases.size();i++){
                if(countryCases.get(i).getCountry().equals(selectedCountry)){
                    noCountry = false;

                    d1.setText(countryCases.get(i).getCases());
                    d2.setText(countryCases.get(i).getActive());
                    d3.setText(countryCases.get(i).getRecovered());
                    d4.setText(countryCases.get(i).getTodayCases());
                    d5.setText(countryCases.get(i).getTodayDeaths());
                    d6.setText(countryCases.get(i).getDeaths());
                    break;
                }
            }
            if(noCountry){
                d1.setText("0");
                d2.setText("0");
                d3.setText("0");
                d4.setText("0");
                d5.setText("0");
                d6.setText("0");
            }
        }
    }

    private void pressedRisk(int in){

        s6.setText(getResources().getString(R.string.dusuk_riskli));
        s2.setText(getResources().getString(R.string.yuksek_riskli));
        s3.setText(getResources().getString(R.string.orta_yuksek_riskli));
        s4.setText(getResources().getString(R.string.orta_riskli));
        s5.setText(getResources().getString(R.string.orta_dusuk_riskli));
        s1.setText(getResources().getString(R.string.bilinmiyor));

        s6.setTextColor(getResources().getColor(R.color.dusukrisk));
        s2.setTextColor(getResources().getColor(R.color.yuksekrisk));
        s3.setTextColor(getResources().getColor(R.color.ortayuksekrisk));
        s4.setTextColor(getResources().getColor(R.color.ortarisk));
        s5.setTextColor(getResources().getColor(R.color.ortadusukrisk));
        s1.setTextColor(getResources().getColor(R.color.toplamvaka));

        d6.setTextColor(getResources().getColor(R.color.dusukrisk));
        d2.setTextColor(getResources().getColor(R.color.yuksekrisk));
        d3.setTextColor(getResources().getColor(R.color.ortayuksekrisk));
        d4.setTextColor(getResources().getColor(R.color.ortarisk));
        d5.setTextColor(getResources().getColor(R.color.ortadusukrisk));
        d1.setTextColor(getResources().getColor(R.color.toplamvaka));

        boolean noCity = true;

        if(getResources().getString(R.string.ISOGLOBAL).equals(selectedCountry)){
            int dusuk=0,odusuk=0,orta=0,oyuksek=0,yuksek=0,bilinmiyor=0;
            noCity = false;
            for(int i=0;i<countryRisks.size();i++){
                dusuk += countryRisks.get(i).getDusuk();
                odusuk += countryRisks.get(i).getOdusuk();
                orta += countryRisks.get(i).getOrta();
                oyuksek += countryRisks.get(i).getOyuksek();
                yuksek += countryRisks.get(i).getYuksek();
                bilinmiyor += countryRisks.get(i).getBilinmiyor();
            }
            title.setText(getResources().getString(R.string.coronavirus) + getResources().getString(R.string.GLOBAL));
            d6.setText(String.valueOf(dusuk));
            d2.setText(String.valueOf(yuksek));
            d3.setText(String.valueOf(oyuksek));
            d4.setText(String.valueOf(orta));
            d5.setText(String.valueOf(odusuk));
            d1.setText(String.valueOf(bilinmiyor));

        }else{
            for(int i=0;i<countryRisks.size();i++){
                if(countryRisks.get(i).getCountry().equals(selectedCountry)){
                    if(selectedCity.equals(getResources().getString(R.string.GENERAL))){
                        noCity = false;
                        title.setText(getResources().getString(R.string.coronavirus) + countries.get(in));
                        d6.setText(String.valueOf(countryRisks.get(i).getDusuk()));
                        d2.setText(String.valueOf(countryRisks.get(i).getYuksek()));
                        d3.setText(String.valueOf(countryRisks.get(i).getOyuksek()));
                        d4.setText(String.valueOf(countryRisks.get(i).getOrta()));
                        d5.setText(String.valueOf(countryRisks.get(i).getOdusuk()));
                        d1.setText(String.valueOf(countryRisks.get(i).getBilinmiyor()));
                    }else{
                        for(int j=0;j<countryRisks.get(i).getCities().size();j++){
                            if(selectedCity==countryRisks.get(i).getCities().get(j).getCity()){
                                noCity = false;
                                title.setText(getResources().getString(R.string.coronavirus) + countryRisks.get(i).getCities().get(j).getCity() +" / " +countries.get(in));
                                d6.setText(String.valueOf(countryRisks.get(i).getCities().get(j).getDusuk()));
                                d2.setText(String.valueOf(countryRisks.get(i).getCities().get(j).getYuksek()));
                                d3.setText(String.valueOf(countryRisks.get(i).getCities().get(j).getOyuksek()));
                                d4.setText(String.valueOf(countryRisks.get(i).getCities().get(j).getOrta()));
                                d5.setText(String.valueOf(countryRisks.get(i).getCities().get(j).getOdusuk()));
                                d1.setText(String.valueOf(countryRisks.get(i).getCities().get(j).getBilinmiyor()));
                                break;
                            }
                        }
                    }

                    break;
                }
            }
        }

        if(noCity){
            title.setText(getResources().getString(R.string.coronavirus) + countries.get(in));
            d2.setText("0");
            d3.setText("0");
            d4.setText("0");
            d5.setText("0");
            d1.setText("0");
            d6.setText("0");
        }
    }


     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
                // ...
            }
        };

        myRef.child(getResources().getString(R.string.db_location)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                for(DataSnapshot users : dataSnapshot.getChildren()){
                    boolean girdi = true;
                    String city = users.child(getResources().getString(R.string.field_city)).getValue(String.class);
                    String country = users.child(getResources().getString(R.string.field_country)).getValue(String.class);
                    String risk = users.child(getResources().getString(R.string.field_risk)).getValue(String.class);

                    if(userID.equals(users.getKey())){
                        currentlyLoc = country;
                    }
                    for(int i=0;i<countryRisks.size();i++){
                        if(countryRisks.get(i).getCountry().equals(country)){
                            for(int j =0; j< countryRisks.get(i).getCities().size();j++){
                                if(countryRisks.get(i).getCities().get(j).getCity().equals(city)){
                                    girdi = false;
                                    if(getResources().getString(R.string.db_yuksek_riskli).equals(risk)){
                                        countryRisks.get(i).getCities().get(j).setYuksek();
                                        countryRisks.get(i).setYuksek(countryRisks.get(i).getCities().get(j).getYuksek());
                                    }
                                    else if(getResources().getString(R.string.db_orta_yuksek_riskli).equals(risk)){
                                        countryRisks.get(i).getCities().get(j).setOyuksek();
                                        countryRisks.get(i).setOyuksek(countryRisks.get(i).getCities().get(j).getOyuksek());
                                    }
                                    else if(getResources().getString(R.string.db_orta_riskli).equals(risk)){
                                        countryRisks.get(i).getCities().get(j).setOrta();
                                        countryRisks.get(i).setOrta(countryRisks.get(i).getCities().get(j).getOrta());
                                    }
                                    else if(getResources().getString(R.string.db_orta_dusuk_riskli).equals(risk)){
                                        countryRisks.get(i).getCities().get(j).setOdusuk();
                                        countryRisks.get(i).setOdusuk(countryRisks.get(i).getCities().get(j).getOdusuk());
                                    }
                                    else if(getResources().getString(R.string.db_dusuk_riskli).equals(risk)){
                                        countryRisks.get(i).getCities().get(j).setDusuk();
                                        countryRisks.get(i).setDusuk(countryRisks.get(i).getCities().get(j).getDusuk());
                                    }else{
                                        countryRisks.get(i).getCities().get(j).setBilinmiyor();
                                        countryRisks.get(i).setBilinmiyor(countryRisks.get(i).getCities().get(j).getBilinmiyor());
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }

                    if(girdi){
                        CityRisk cityRisk = new CityRisk(country,city,0,0,0,0,0,0);
                        boolean cougirdi= true;
                        if(getResources().getString(R.string.db_yuksek_riskli).equals(risk)){
                            cityRisk.setYuksek();
                        }
                        else if(getResources().getString(R.string.db_orta_yuksek_riskli).equals(risk)){
                            cityRisk.setOyuksek();
                        }
                        else if(getResources().getString(R.string.db_orta_riskli).equals(risk)){
                            cityRisk.setOrta();
                        }
                        else if(getResources().getString(R.string.db_orta_dusuk_riskli).equals(risk)){
                            cityRisk.setOdusuk();
                        }
                        else if(getResources().getString(R.string.db_dusuk_riskli).equals(risk)){
                            cityRisk.setDusuk();
                        }else{
                            cityRisk.setBilinmiyor();
                        }
                        for(int i=0;i<countryRisks.size();i++){
                            if(countryRisks.get(i).getCountry().equals(cityRisk.getCountry())){
                                cougirdi = false;
                                countryRisks.get(i).setCities(cityRisk);
                                if(getResources().getString(R.string.db_yuksek_riskli).equals(risk)){
                                    countryRisks.get(i).setYuksek(cityRisk.getYuksek());
                                }
                                else if(getResources().getString(R.string.db_orta_yuksek_riskli).equals(risk)){
                                    countryRisks.get(i).setOyuksek(cityRisk.getOyuksek());
                                }
                                else if(getResources().getString(R.string.db_orta_riskli).equals(risk)){
                                    countryRisks.get(i).setOrta(cityRisk.getOrta());
                                }
                                else if(getResources().getString(R.string.db_orta_dusuk_riskli).equals(risk)){
                                    countryRisks.get(i).setOdusuk(cityRisk.getOdusuk());
                                }
                                else if(getResources().getString(R.string.db_dusuk_riskli).equals(risk)){
                                    countryRisks.get(i).setDusuk(cityRisk.getDusuk());
                                }else{
                                    countryRisks.get(i).setBilinmiyor(cityRisk.getBilinmiyor());
                                }
                                break;
                            }
                        }
                        if(cougirdi){
                            countryRisks.add(new CountryRisk(cityRisk.getCountry(),cityRisk.getDusuk(),cityRisk.getOdusuk(),cityRisk.getOrta(),cityRisk.getOyuksek(),cityRisk.getYuksek(),cityRisk.getBilinmiyor(),cityRisk));
                        }
                    }

                }

                //countryRisks.add(new CountryRisk(countries.getKey(),dusuk,odusuk,orta,oyuksek,yuksek,bilinmiyor,cityRisks));
                getDataFromServer();

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
