package com.nic.KVVTSurvey.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.KVVTSurvey.R;
import com.nic.KVVTSurvey.adapter.CommonAdapter;
import com.nic.KVVTSurvey.adapter.ViewServerDataListAdapter;
import com.nic.KVVTSurvey.api.Api;
import com.nic.KVVTSurvey.api.ApiService;
import com.nic.KVVTSurvey.api.ServerResponse;
import com.nic.KVVTSurvey.constant.AppConstant;
import com.nic.KVVTSurvey.dataBase.DBHelper;
import com.nic.KVVTSurvey.dataBase.dbData;
import com.nic.KVVTSurvey.databinding.HomeScreenBinding;
import com.nic.KVVTSurvey.dialog.MyDialog;
import com.nic.KVVTSurvey.model.KVVTSurvey;
import com.nic.KVVTSurvey.session.PrefManager;
import com.nic.KVVTSurvey.support.ProgressHUD;
import com.nic.KVVTSurvey.utils.UrlGenerator;
import com.nic.KVVTSurvey.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HomePage extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private HomeScreenBinding homeScreenBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String isHome;
    Handler myHandler = new Handler();
    private List<KVVTSurvey> Village = new ArrayList<>();
    private List<KVVTSurvey> VillageOrdered = new ArrayList<>();
    private List<KVVTSurvey> HabitationOrdered  = new ArrayList<>();
    private List<KVVTSurvey> Habitation = new ArrayList<>();
    private List<KVVTSurvey> Street = new ArrayList<>();
    private List<KVVTSurvey> StreetOrdered = new ArrayList<>();
    private List<KVVTSurvey> Scheme = new ArrayList<>();
    private List<KVVTSurvey> Community = new ArrayList<>();
    String lastInsertedID;
    String isEligible = "";
    String isExistingUser = "";
    String isFaHus = "";
    private ProgressHUD progressHUD;
    Boolean flag=false;
    Boolean Etflag=false;


    String pref_Village;
    String pref_Scheme;
    String photoRequired="";
    String auto_rejection="";
    String exclusion_criteria_ta="";

    String patta_avilable_status="";
    String is_awaas_plus_list="";
    String is_document_available="";
    String is_natham_land_available="";
    int current_year=0;
    int previous_year=0;
    static String getdata="yes";

    ArrayList<KVVTSurvey> kavvtIdList;
    String go_true="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        homeScreenBinding = DataBindingUtil.setContentView(this, R.layout.home_screen);
        homeScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        dbData.open();
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }


        villageFilterSpinner(prefManager.getBlockCode());
        schemeFilterSpinner(prefManager.getBlockCode());
        communityFilterSpinner();

        getCurrentYearPreviousYear();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            isHome = bundle.getString("Home");
        }
        if(Utils.isOnline()){
                if(isHome.equals("Login")) {
                        getAllKVVTList();
                        //go_true="no";
                }

        }
        else {
            Utils.showAlert(HomePage.this, "Your Internet seems to be Offline.Data can be viewed only in Online mode.");
        }
        homeScreenBinding.selectScheTv.setVisibility(View.GONE);
        homeScreenBinding.scheLayout.setVisibility(View.GONE);

        homeScreenBinding.seecIdLayout.setVisibility(View.GONE);
        homeScreenBinding.nameLayout.setVisibility(View.GONE);
        homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
        homeScreenBinding.strLayout.setVisibility(View.GONE);
        homeScreenBinding.selectStrTv.setVisibility(View.GONE);
        homeScreenBinding.communityLayout.setVisibility(View.GONE);
        homeScreenBinding.selectCommunityTv.setVisibility(View.GONE);
        homeScreenBinding.doorNoLayout.setVisibility(View.GONE);
        homeScreenBinding.benfMigTv.setVisibility(View.GONE);
        homeScreenBinding.beneficiaryMigratedLayout.setVisibility(View.GONE);
        homeScreenBinding.pattaAvailableTv.setVisibility(View.GONE);
        homeScreenBinding.pattaAvailableLayout.setVisibility(View.GONE);
        homeScreenBinding.awaasTv.setVisibility(View.GONE);
        homeScreenBinding.awaasLayout.setVisibility(View.GONE);
        homeScreenBinding.documentAvailableLayout.setVisibility(View.GONE);
        homeScreenBinding.documentAvailableTv.setVisibility(View.GONE);
        homeScreenBinding.nathamLandAvailableTv.setVisibility(View.GONE);
        homeScreenBinding.nathamLandLayout.setVisibility(View.GONE);

        homeScreenBinding.villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    pref_Village = Village.get(position).getPvName();
                    prefManager.setVillageListPvName(pref_Village);
                    prefManager.setPvCode(Village.get(position).getPvCode());
                    habitationFilterSpinner(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode());
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.tick.setVisibility(View.GONE);
                    homeScreenBinding.streetSpinner.setSelection(0);
                    homeScreenBinding.streetSpinner.setAdapter(null);
                    prefManager.setHabCode("");
                    prefManager.setStreetCode("");
                    prefManager.setStreetName("");
                    homeScreenBinding.nameLayout.setVisibility(View.GONE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
                    homeScreenBinding.name.setText("");
                    homeScreenBinding.fatherName.setText("");
                }else {
                    Etflag=false;
                    prefManager.setVillageListPvName("");
                    prefManager.setPvCode("");
                    prefManager.setHabCode("");
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.tick.setVisibility(View.GONE);
                    homeScreenBinding.habitationSpinner.setAdapter(null);
                    homeScreenBinding.nameLayout.setVisibility(View.GONE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
                    homeScreenBinding.name.setText("");
                    homeScreenBinding.fatherName.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        homeScreenBinding.schemeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    is_awaas_plus_list="";
                    patta_avilable_status="";

                    is_document_available="";
                    is_natham_land_available="";

                    homeScreenBinding.awassNo.setChecked(false);
                    homeScreenBinding.awaasYes.setChecked(false);
                    homeScreenBinding.pattaYes.setChecked(false);
                    homeScreenBinding.pattaNo.setChecked(false);

                    homeScreenBinding.documentYes.setChecked(false);
                    homeScreenBinding.documentNo.setChecked(false);
                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(false);

                    homeScreenBinding.nathamLandLayout.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.documentAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.documentAvailableLayout.setVisibility(View.GONE);

                    pref_Scheme = Scheme.get(position).getExclusion_criteria();
                    photoRequired = Scheme.get(position).getPhoto_required();
                    auto_rejection = Scheme.get(position).getEleigible_auto_rejection();
                    exclusion_criteria_ta = Scheme.get(position).getExclusion_criteria_ta();
                    prefManager.setKeySchemeListName(pref_Scheme);
                    prefManager.setKeySchemeCode(Scheme.get(position).getExclusion_criteria_id());
                    if(photoRequired.equals("Y")){
                        //homeScreenBinding.saveData.setText("Take Photo");
                        homeScreenBinding.saveData.setText(getResources().getString(R.string.take_photo));
                    }else {
                        //homeScreenBinding.saveData.setText("Save details");
                        homeScreenBinding.saveData.setText(getResources().getString(R.string.save_details));
                    }
                    if(auto_rejection.equals("Y")){
                        homeScreenBinding.pattaAvailableTv.setVisibility(View.GONE);
                        homeScreenBinding.pattaAvailableLayout.setVisibility(View.GONE);
                        homeScreenBinding.awaasTv.setVisibility(View.GONE);
                        homeScreenBinding.awaasLayout.setVisibility(View.GONE);
                    }
                    else {
                        homeScreenBinding.pattaAvailableTv.setVisibility(View.VISIBLE);
                        homeScreenBinding.pattaAvailableLayout.setVisibility(View.VISIBLE);
                        homeScreenBinding.awaasTv.setVisibility(View.VISIBLE);
                        homeScreenBinding.awaasLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeScreenBinding.habitationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setHabCode(Habitation.get(position).getHabCode());
                    prefManager.setStreetCode("");
                    prefManager.setStreetName("");
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.tick.setVisibility(View.GONE);
                    getStreetList();
                    new fetchParticularKvvtIdtask().execute();
                    homeScreenBinding.streetSpinner.setSelection(0);
                    homeScreenBinding.nameLayout.setVisibility(View.GONE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
                    homeScreenBinding.name.setText("");
                    homeScreenBinding.fatherName.setText("");
                }else {
                    prefManager.setHabCode("");
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.tick.setVisibility(View.GONE);
                    homeScreenBinding.nameLayout.setVisibility(View.GONE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
                    homeScreenBinding.name.setText("");
                    homeScreenBinding.fatherName.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        homeScreenBinding.streetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setStreetCode(Street.get(position).getStreetCode());
                    prefManager.setStreetName(Street.get(position).getStreetName());
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.tick.setVisibility(View.GONE);
                }else {
                    prefManager.setStreetCode("");
                    prefManager.setStreetName("");
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.tick.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        homeScreenBinding.communitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setCommunityCode(Community.get(position).getCommunity_id());
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.tick.setVisibility(View.GONE);
                }else {
                    prefManager.setCommunityCode("");
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.tick.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        homeScreenBinding.takePicLayout.setAlpha(0);
        final Runnable pmgsy = new Runnable() {
            @Override
            public void run() {
                homeScreenBinding.takePicLayout.setAlpha(1);
                homeScreenBinding.takePicLayout.startAnimation(AnimationUtils.loadAnimation(HomePage.this, R.anim.text_view_move_right));

            }
        };


        homeScreenBinding.migYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isEligible = "Y";
                    homeScreenBinding.migNo.setChecked(false);
                    validateYesNo();
                }else {
                    homeScreenBinding.schemeSpinner.setSelection(0);
                    prefManager.setKeySchemeCode("");
                    //homeScreenBinding.selectScheTv.setVisibility(View.GONE);
                    //homeScreenBinding.scheLayout.setVisibility(View.GONE);
                }
            }
        });

        homeScreenBinding.migNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isEligible = "N";
                    homeScreenBinding.migYes.setChecked(false);
                    validateYesNo();
                }else {

                }
            }
        });

        homeScreenBinding.radioFather.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    isFaHus="F";
                    homeScreenBinding.radioFH.clearCheck();
                    homeScreenBinding.radioFather.setEnabled(true);
                    homeScreenBinding.radioFather.setChecked(true);
                    homeScreenBinding.radioHusband.setChecked(false);
                    Log.d("RisFaHus",isFaHus);
                }
            }
        });
        homeScreenBinding.radioHusband.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    isFaHus="H";
                    homeScreenBinding.radioFH.clearCheck();
                    homeScreenBinding.radioHusband.setEnabled(true);
                    homeScreenBinding.radioHusband.setChecked(true);
                    homeScreenBinding.radioFather.setChecked(false);
                    Log.d("RHisFaHus",isFaHus);
                }
            }
        });

        homeScreenBinding.pattaYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    homeScreenBinding.pattaYes.setChecked(true);
                    homeScreenBinding.pattaNo.setChecked(false);
                    patta_avilable_status="Y";

                    homeScreenBinding.documentAvailableLayout.setVisibility(View.GONE);
                    homeScreenBinding.documentAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandLayout.setVisibility(View.GONE);

                    homeScreenBinding.documentYes.setChecked(false);
                    homeScreenBinding.documentNo.setChecked(false);
                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(false);

                    is_document_available="";
                    is_natham_land_available="";

                }
            }
        });
        homeScreenBinding.pattaNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    homeScreenBinding.pattaYes.setChecked(false);
                    homeScreenBinding.pattaNo.setChecked(true);
                    patta_avilable_status="N";

                    homeScreenBinding.documentAvailableLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.documentAvailableTv.setVisibility(View.VISIBLE);
                    homeScreenBinding.nathamLandAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandLayout.setVisibility(View.GONE);

                    homeScreenBinding.documentYes.setChecked(false);
                    homeScreenBinding.documentNo.setChecked(false);
                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(false);

                    is_document_available="";
                    is_natham_land_available="";


                }
                else {
                    homeScreenBinding.pattaYes.setChecked(false);
                    homeScreenBinding.pattaNo.setChecked(false);
                    patta_avilable_status="";

                    homeScreenBinding.documentAvailableLayout.setVisibility(View.GONE);
                    homeScreenBinding.documentAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandLayout.setVisibility(View.GONE);

                    homeScreenBinding.documentYes.setChecked(false);
                    homeScreenBinding.documentNo.setChecked(false);
                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(false);

                    is_document_available="";
                    is_natham_land_available="";

                }
            }
        });

        homeScreenBinding.documentYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    homeScreenBinding.documentYes.setChecked(true);
                    homeScreenBinding.documentNo.setChecked(false);
                    is_document_available="Y";
                    is_natham_land_available="";

                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(false);

                    homeScreenBinding.nathamLandAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandLayout.setVisibility(View.GONE);


                }
            }
        });
        homeScreenBinding.documentNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    homeScreenBinding.documentYes.setChecked(false);
                    homeScreenBinding.documentNo.setChecked(true);
                    is_document_available="N";
                    is_natham_land_available="";

                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(false);

                    homeScreenBinding.nathamLandAvailableTv.setVisibility(View.VISIBLE);
                    homeScreenBinding.nathamLandLayout.setVisibility(View.VISIBLE);


                }
                else {
                    homeScreenBinding.documentYes.setChecked(false);
                    homeScreenBinding.documentNo.setChecked(false);
                    is_document_available="";
                    is_natham_land_available="";

                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(false);

                    homeScreenBinding.nathamLandAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandLayout.setVisibility(View.GONE);

                }
            }
        });

        homeScreenBinding.nathamLandYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    homeScreenBinding.nathamLandYes.setChecked(true);
                    homeScreenBinding.nathamLandNo.setChecked(false);
                    is_natham_land_available="Y";
                }
            }
        });
        homeScreenBinding.nathamLandNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(true);
                    is_natham_land_available="N";
                }
            }
        });

        homeScreenBinding.awaasYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    homeScreenBinding.awaasYes.setChecked(true);
                    homeScreenBinding.awassNo.setChecked(false);
                    is_awaas_plus_list="Y";
                }
            }
        });
        homeScreenBinding.awassNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    homeScreenBinding.awaasYes.setChecked(false);
                    homeScreenBinding.awassNo.setChecked(true);
                    is_awaas_plus_list="N";
                }
            }
        });

        homeScreenBinding.existingUserYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isExistingUser="Y";
                    homeScreenBinding.existingUserNo.setChecked(false);
                    homeScreenBinding.seecIdLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.nameLayout.setVisibility(View.GONE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
                    homeScreenBinding.migYesLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.migNoLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.benfMigTv.setVisibility(View.GONE);
                    homeScreenBinding.eligibleTv.setVisibility(View.GONE);
                    homeScreenBinding.strLayout.setVisibility(View.GONE);
                    homeScreenBinding.selectStrTv.setVisibility(View.GONE);
                    homeScreenBinding.selectCommunityTv.setVisibility(View.GONE);
                    homeScreenBinding.communityLayout.setVisibility(View.GONE);
                    homeScreenBinding.doorNoLayout.setVisibility(View.GONE);
                    homeScreenBinding.migNo.setChecked(false);
                    homeScreenBinding.migYes.setChecked(false);
                    homeScreenBinding.migNo.setEnabled(true);
                    homeScreenBinding.name.setEnabled(false);
                    homeScreenBinding.fatherName.setEnabled(false);
                    homeScreenBinding.FHRadioLayout.setVisibility(View.GONE);
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.name.setText("");
                    homeScreenBinding.fatherName.setText("");
                    homeScreenBinding.doorNo.setText("");
                    homeScreenBinding.streetSpinner.setSelection(0);
                    homeScreenBinding.communitySpinner.setSelection(0);
                    homeScreenBinding.radioFather.setChecked(false);
                    homeScreenBinding.radioHusband.setChecked(false);
                    homeScreenBinding.radioFather.setEnabled(true);
                    homeScreenBinding.radioHusband.setEnabled(true);

                    auto_rejection="";
                    photoRequired="";
                    is_awaas_plus_list="";
                    is_natham_land_available="";
                    is_document_available="";
                    patta_avilable_status="";
                    homeScreenBinding.selectScheTv.setVisibility(View.VISIBLE);
                    homeScreenBinding.scheLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.pattaAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.pattaAvailableLayout.setVisibility(View.GONE);
                    homeScreenBinding.awaasTv.setVisibility(View.GONE);
                    homeScreenBinding.awaasLayout.setVisibility(View.GONE);
                    homeScreenBinding.documentAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.documentAvailableLayout.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandLayout.setVisibility(View.GONE);
                    homeScreenBinding.documentYes.setChecked(false);
                    homeScreenBinding.documentNo.setChecked(false);
                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(false);
                    homeScreenBinding.pattaNo.setChecked(false);
                    homeScreenBinding.pattaYes.setChecked(false);
                    homeScreenBinding.awaasYes.setChecked(false);
                    homeScreenBinding.awassNo.setChecked(false);
                    homeScreenBinding.schemeSpinner.setSelection(0);


                }
                else {
                    homeScreenBinding.strLayout.setVisibility(View.GONE);
                    homeScreenBinding.selectStrTv.setVisibility(View.GONE);
                    homeScreenBinding.selectCommunityTv.setVisibility(View.GONE);
                    homeScreenBinding.communityLayout.setVisibility(View.GONE);
                    homeScreenBinding.doorNoLayout.setVisibility(View.GONE);
                    homeScreenBinding.seecIdLayout.setVisibility(View.GONE);
                    homeScreenBinding.nameLayout.setVisibility(View.GONE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
                    homeScreenBinding.migYesLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.migNoLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.eligibleTv.setVisibility(View.GONE);
                    homeScreenBinding.benfMigTv.setVisibility(View.GONE);
                    homeScreenBinding.migNo.setChecked(false);
                    homeScreenBinding.migYes.setChecked(false);
                    homeScreenBinding.migNo.setEnabled(true);
                    homeScreenBinding.name.setEnabled(false);
                    homeScreenBinding.fatherName.setEnabled(false);
                    homeScreenBinding.FHRadioLayout.setVisibility(View.GONE);
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.name.setText("");
                    homeScreenBinding.fatherName.setText("");
                    homeScreenBinding.doorNo.setText("");
                    homeScreenBinding.streetSpinner.setSelection(0);
                    homeScreenBinding.communitySpinner.setSelection(0);
                    homeScreenBinding.radioFather.setChecked(false);
                    homeScreenBinding.radioHusband.setChecked(false);
                    homeScreenBinding.radioFather.setEnabled(true);
                    homeScreenBinding.radioHusband.setEnabled(true);

                    isExistingUser="";

                    auto_rejection="";
                    photoRequired="";
                    is_awaas_plus_list="";
                    is_natham_land_available="";
                    is_document_available="";
                    patta_avilable_status="";
                    homeScreenBinding.selectScheTv.setVisibility(View.GONE);
                    homeScreenBinding.scheLayout.setVisibility(View.GONE);
                    homeScreenBinding.pattaAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.pattaAvailableLayout.setVisibility(View.GONE);
                    homeScreenBinding.awaasTv.setVisibility(View.GONE);
                    homeScreenBinding.awaasLayout.setVisibility(View.GONE);
                    homeScreenBinding.documentAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.documentAvailableLayout.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandLayout.setVisibility(View.GONE);
                    homeScreenBinding.documentYes.setChecked(false);
                    homeScreenBinding.documentNo.setChecked(false);
                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(false);
                    homeScreenBinding.pattaNo.setChecked(false);
                    homeScreenBinding.pattaYes.setChecked(false);
                    homeScreenBinding.awaasYes.setChecked(false);
                    homeScreenBinding.awassNo.setChecked(false);
                    homeScreenBinding.eligibleTv.setVisibility(View.GONE);
                    homeScreenBinding.schemeSpinner.setSelection(0);

                }
            }
        });
        homeScreenBinding.existingUserNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isExistingUser="N";
                    homeScreenBinding.existingUserYes.setChecked(false);
                    homeScreenBinding.seecIdLayout.setVisibility(View.GONE);
                    homeScreenBinding.nameLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.strLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.selectStrTv.setVisibility(View.VISIBLE);
                    homeScreenBinding.selectCommunityTv.setVisibility(View.VISIBLE);
                    homeScreenBinding.communityLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.doorNoLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.migYesLayout.setVisibility(View.GONE);
                    homeScreenBinding.migNoLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.eligibleTv.setVisibility(View.GONE);
                    homeScreenBinding.benfMigTv.setVisibility(View.GONE);
                    homeScreenBinding.migNo.setChecked(true);
                    homeScreenBinding.migYes.setChecked(false);
                    homeScreenBinding.migNo.setEnabled(false);
                    homeScreenBinding.name.setEnabled(true);
                    homeScreenBinding.fatherName.setEnabled(true);
                    homeScreenBinding.FHRadioLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.name.setText("");
                    homeScreenBinding.fatherName.setText("");
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.doorNo.setText("");
                    homeScreenBinding.streetSpinner.setSelection(0);
                    homeScreenBinding.communitySpinner.setSelection(0);

                    homeScreenBinding.radioFather.setChecked(false);
                    homeScreenBinding.radioHusband.setChecked(false);
                    homeScreenBinding.radioFather.setEnabled(true);
                    homeScreenBinding.radioHusband.setEnabled(true);


                    auto_rejection="N";
                    photoRequired="Y";
                    is_awaas_plus_list="";
                    is_natham_land_available="";
                    is_document_available="";
                    patta_avilable_status="";
                    homeScreenBinding.selectScheTv.setVisibility(View.GONE);
                    homeScreenBinding.scheLayout.setVisibility(View.GONE);
                    homeScreenBinding.pattaAvailableTv.setVisibility(View.VISIBLE);
                    homeScreenBinding.pattaAvailableLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.awaasTv.setVisibility(View.VISIBLE);
                    homeScreenBinding.awaasLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.documentAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.documentAvailableLayout.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandLayout.setVisibility(View.GONE);
                    homeScreenBinding.documentYes.setChecked(false);
                    homeScreenBinding.documentNo.setChecked(false);
                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(false);
                    homeScreenBinding.pattaNo.setChecked(false);
                    homeScreenBinding.pattaYes.setChecked(false);
                    homeScreenBinding.awaasYes.setChecked(false);
                    homeScreenBinding.awassNo.setChecked(false);
                    homeScreenBinding.eligibleTv.setVisibility(View.GONE);
                    homeScreenBinding.schemeSpinner.setSelection(0);



                }
                else {
                    homeScreenBinding.strLayout.setVisibility(View.GONE);
                    homeScreenBinding.selectStrTv.setVisibility(View.GONE);
                    homeScreenBinding.selectCommunityTv.setVisibility(View.GONE);
                    homeScreenBinding.communityLayout.setVisibility(View.GONE);
                    homeScreenBinding.doorNoLayout.setVisibility(View.GONE);
                    homeScreenBinding.seecIdLayout.setVisibility(View.GONE);
                    homeScreenBinding.nameLayout.setVisibility(View.GONE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
                    homeScreenBinding.migYesLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.migNoLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.eligibleTv.setVisibility(View.GONE);
                    homeScreenBinding.benfMigTv.setVisibility(View.GONE);
                    homeScreenBinding.migNo.setChecked(false);
                    homeScreenBinding.migYes.setChecked(false);
                    homeScreenBinding.migNo.setEnabled(true);
                    homeScreenBinding.name.setEnabled(false);
                    homeScreenBinding.fatherName.setEnabled(false);
                    homeScreenBinding.FHRadioLayout.setVisibility(View.GONE);
                    homeScreenBinding.benificiaryId.setText("");
                    homeScreenBinding.name.setText("");
                    homeScreenBinding.fatherName.setText("");
                    homeScreenBinding.doorNo.setText("");
                    homeScreenBinding.streetSpinner.setSelection(0);
                    homeScreenBinding.communitySpinner.setSelection(0);
                    homeScreenBinding.radioFather.setChecked(false);
                    homeScreenBinding.radioHusband.setChecked(false);
                    homeScreenBinding.radioFather.setEnabled(true);
                    homeScreenBinding.radioHusband.setEnabled(true);

                    isExistingUser="";

                    auto_rejection="";
                    photoRequired="";
                    is_awaas_plus_list="";
                    is_natham_land_available="";
                    is_document_available="";
                    patta_avilable_status="";
                    homeScreenBinding.selectScheTv.setVisibility(View.GONE);
                    homeScreenBinding.scheLayout.setVisibility(View.GONE);
                    homeScreenBinding.pattaAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.pattaAvailableLayout.setVisibility(View.GONE);
                    homeScreenBinding.awaasTv.setVisibility(View.GONE);
                    homeScreenBinding.awaasLayout.setVisibility(View.GONE);
                    homeScreenBinding.documentAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.documentAvailableLayout.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandAvailableTv.setVisibility(View.GONE);
                    homeScreenBinding.nathamLandLayout.setVisibility(View.GONE);
                    homeScreenBinding.documentYes.setChecked(false);
                    homeScreenBinding.documentNo.setChecked(false);
                    homeScreenBinding.nathamLandYes.setChecked(false);
                    homeScreenBinding.nathamLandNo.setChecked(false);
                    homeScreenBinding.pattaNo.setChecked(false);
                    homeScreenBinding.pattaYes.setChecked(false);
                    homeScreenBinding.awaasYes.setChecked(false);
                    homeScreenBinding.awassNo.setChecked(false);
                    homeScreenBinding.eligibleTv.setVisibility(View.GONE);
                    homeScreenBinding.schemeSpinner.setSelection(0);

                }
            }
        });

        homeScreenBinding.benificiaryId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!prefManager.getPvCode().equals("") && !prefManager.getHabCode().equals("")){
                    Etflag=true;
                }
                else {
                    Etflag=false;
                    Utils.showAlert(HomePage.this,"First select Village and Habitation!");
                }
                return false;
            }
        });
        homeScreenBinding.benificiaryId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               /* if(!prefManager.getPvCode().equals("") && !prefManager.getHabCode().equals("")){
                    Etflag=true;
                }else {
                    Etflag=false;
                }*/

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                flag=false;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                flag=false;
                if(!homeScreenBinding.benificiaryId.getText().toString().isEmpty()){
                    homeScreenBinding.tick.setVisibility(View.VISIBLE);
                }else{
                    homeScreenBinding.tick.setVisibility(View.GONE);
                }

            }
        });
        homeScreenBinding.tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Etflag){
                    if(!homeScreenBinding.benificiaryId.getText().toString().equals("")){
                        validateBenificiaryId(homeScreenBinding.benificiaryId.getText().toString());}
                    else {
                        Utils.showAlert(HomePage.this,"Enter Benificiary Id!");
                    }
                }
                else {
                    Utils.showAlert(HomePage.this,"First select Village and Habitation!");
                }
            }
        });
       /* myHandler.postDelayed(pmgsy, 1500);
        homeScreenBinding.viewServerData.setAlpha(0);
        final Runnable block = new Runnable() {
            @Override
            public void run() {
                homeScreenBinding.viewServerData.setAlpha(1);
                homeScreenBinding.viewServerData.startAnimation(AnimationUtils.loadAnimation(HomePage.this, R.anim.text_view_move));

            }
        };
        myHandler.postDelayed(block, 2000);*/

        syncButtonVisibility();
        //viewServerData();

    }
    public void getStreetList() {
        try {
            new ApiService(this).makeJSONObjectRequest("StreetList", Api.Method.POST, UrlGenerator.getServicesListUrl(), streetListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject streetListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.StreetListJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("StreetList", "" + authKey);
        return dataSet;
    }

    private void validateBenificiaryId(String toString) {
        boolean flag_new = false;
        String beneficiary_name="";
        String beneficiary_father_name="";
        try {
            if(Utils.isOnline()) {
                new ApiService(this).makeJSONObjectRequest("ValidateBenificiaryId", Api.Method.POST, UrlGenerator.getKVVTListUrl(), validateBenificiaryIdJsonParams(), "not cache", this);
            }
            else {
                Utils.hideSoftKeyboard(this);
                for(int i =0;i<kavvtIdList.size();i++){
                    if(toString.equals(kavvtIdList.get(i).getBeneficiaryId())){
                        flag =true;
                        beneficiary_name = kavvtIdList.get(i).getBeneficiaryName();
                        beneficiary_father_name = kavvtIdList.get(i).getBeneficiaryFatherName();
                        break;
                    }
                    else {
                        flag = false;
                    }
                }

                if(flag){
                    homeScreenBinding.nameLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.name.setText(beneficiary_name);
                    homeScreenBinding.fatherName.setText(beneficiary_father_name);
                }
                else {
                    Utils.showAlert(this,"No Record");
                    homeScreenBinding.nameLayout.setVisibility(View.GONE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
                    homeScreenBinding.name.setText("");
                    homeScreenBinding.fatherName.setText("");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void validateYesNo() {
        if ( isEligible.equalsIgnoreCase("Y")) {
            homeScreenBinding.takePicLayout.setVisibility(View.GONE);
            //homeScreenBinding.selectScheTv.setVisibility(View.VISIBLE);
            //homeScreenBinding.scheLayout.setVisibility(View.VISIBLE);
            //homeScreenBinding.saveData.setText("Take Photo");
            homeScreenBinding.saveData.setText(getResources().getString(R.string.take_photo));
        } else if (isEligible.equalsIgnoreCase("N")) {
            homeScreenBinding.schemeSpinner.setSelection(0);
            prefManager.setKeySchemeCode("");
            homeScreenBinding.takePicLayout.setVisibility(View.GONE);
            //homeScreenBinding.selectScheTv.setVisibility(View.GONE);
            //homeScreenBinding.scheLayout.setVisibility(View.GONE);
            //homeScreenBinding.saveData.setText("Take Photo");
            homeScreenBinding.saveData.setText(getResources().getString(R.string.take_photo));
        }
    }

//    public boolean validateCheck() {
//        if (isAlive.equalsIgnoreCase("") || isMigrated.equalsIgnoreCase("Y")) {
//            homeScreenBinding.saveData.setText("Save details");
    //homeScreenBinding.saveData.setText(getResources().getString(R.string.save_details));
//        } else if (isAlive.equalsIgnoreCase("Y") && isMigrated.equalsIgnoreCase("N")) {
//            homeScreenBinding.saveData.setText("Take Photo");
    //homeScreenBinding.saveData.setText(getResources().getString(R.string.take_photo));
//        } else if (isAlive.equalsIgnoreCase("N") && isLegal.equalsIgnoreCase("N")) {
//            homeScreenBinding.saveData.setText("Save details");
    //homeScreenBinding.saveData.setText(getResources().getString(R.string.save_details));
//        } else if (isAlive.equalsIgnoreCase("N") && isLegal.equalsIgnoreCase("Y") && isMigrated.equalsIgnoreCase("Y")) {
//            homeScreenBinding.saveData.setText("Save details");
    //homeScreenBinding.saveData.setText(getResources().getString(R.string.save_details));
//        } else if (isAlive.equalsIgnoreCase("N") && isLegal.equalsIgnoreCase("Y") && isMigrated.equalsIgnoreCase("N")) {
//            homeScreenBinding.saveData.setText("Take Photo");
    //homeScreenBinding.saveData.setText(getResources().getString(R.string.take_photo));
//        }
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }



    public void villageFilterSpinner(String filterVillage) {
        Cursor VillageList = null;
        VillageList = db.rawQuery("SELECT * FROM " + DBHelper.VILLAGE_TABLE_NAME + " where dcode = "+prefManager.getDistrictCode()+ " and bcode = '" + filterVillage + "'", null);

        Village.clear();
        VillageOrdered.clear();
        if (VillageList.getCount() > 0) {
            if (VillageList.moveToFirst()) {
                do {
                    KVVTSurvey villageList = new KVVTSurvey();
                    String districtCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String pvname = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_NAME));
                    String pvname_ta = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_NAME_TA));

                    villageList.setDistictCode(districtCode);
                    villageList.setBlockCode(blockCode);
                    villageList.setPvCode(pvCode);
                    villageList.setPvName(pvname);
                    villageList.setPvNameTa(pvname_ta);

                    VillageOrdered.add(villageList);
                } while (VillageList.moveToNext());
            }
            Log.d("Villagespinnersize", "" + VillageOrdered.size());

        }
        Collections.sort(VillageOrdered, (lhs, rhs) -> lhs.getPvNameTa().compareTo(rhs.getPvNameTa()));
        KVVTSurvey villageListValue = new KVVTSurvey();
        villageListValue.setPvName(getResources().getString(R.string.select_village));
        villageListValue.setPvNameTa(getResources().getString(R.string.select_village));
        Village.add(villageListValue);
        for (int i = 0; i < VillageOrdered.size(); i++) {
            KVVTSurvey villageList = new KVVTSurvey();
            String districtCode = VillageOrdered.get(i).getDistictCode();
            String blockCode = VillageOrdered.get(i).getBlockCode();
            String pvCode =  VillageOrdered.get(i).getPvCode();
            String pvname =  VillageOrdered.get(i).getPvName();
            String pvname_ta =  VillageOrdered.get(i).getPvNameTa();

            villageList.setDistictCode(districtCode);
            villageList.setBlockCode(blockCode);
            villageList.setPvCode(pvCode);
            villageList.setPvName(pvname);
            villageList.setPvNameTa(pvname_ta);

            Village.add(villageList);
        }
        homeScreenBinding.villageSpinner.setAdapter(new CommonAdapter(this, Village, "VillageList"));
    }
    public void schemeFilterSpinner(String filterVillage) {
        Cursor VillageList = null;
        VillageList = db.rawQuery("SELECT * FROM " + DBHelper.SCHEME_TABLE_NAME , null);

        Scheme.clear();
        KVVTSurvey villageListValue = new KVVTSurvey();
        villageListValue.setExclusion_criteria(getResources().getString(R.string.select_automatic_exclusion_creteria));
        villageListValue.setExclusion_criteria_ta(getResources().getString(R.string.select_automatic_exclusion_creteria));
        Scheme.add(villageListValue);
        if (VillageList.getCount() > 0) {
            if (VillageList.moveToFirst()) {
                do {
                    KVVTSurvey villageList = new KVVTSurvey();
                    String EXCLUSION_CRITERIA_ID = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.EXCLUSION_CRITERIA_ID));
                    String EXCLUSION_CRITERIA = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.EXCLUSION_CRITERIA));
                    String PHOTO_REQUIRED = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PHOTO_REQUIRED));
                    String AUTO_REJECTED = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.AUTO_REJECT));
                    String EXCLUSION_CRITERIA_TA = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.EXCLUSION_CRITERIA_TA));

                    villageList.setExclusion_criteria_id(EXCLUSION_CRITERIA_ID);
                    villageList.setExclusion_criteria(EXCLUSION_CRITERIA);
                    villageList.setPhoto_required(PHOTO_REQUIRED);
                    villageList.setEleigible_auto_rejection(AUTO_REJECTED);
                    villageList.setExclusion_criteria_ta(EXCLUSION_CRITERIA_TA);

                    Scheme.add(villageList);
                } while (VillageList.moveToNext());
            }
            Log.d("Schemespinnersize", "" + Scheme.size());

        }
        homeScreenBinding.schemeSpinner.setAdapter(new CommonAdapter(this, Scheme, "SchemeList"));
    }
    public void communityFilterSpinner() {
        Cursor CommunityList = null;
        CommunityList = db.rawQuery("SELECT * FROM " + DBHelper.COMMUNITY_TABLE_NAME , null);

        Community.clear();
        KVVTSurvey CommunityListValue = new KVVTSurvey();
        CommunityListValue.setCommunity_name(getResources().getString(R.string.select_community));
        Community.add(CommunityListValue);
        if (CommunityList.getCount() > 0) {
            if (CommunityList.moveToFirst()) {
                do {
                    KVVTSurvey CommunityValueList = new KVVTSurvey();
                    String community_id = CommunityList.getString(CommunityList.getColumnIndexOrThrow(AppConstant.COMMUNITY_ID));
                    String community_name = CommunityList.getString(CommunityList.getColumnIndexOrThrow(AppConstant.COMMUNITY_NAME));

                    CommunityValueList.setCommunity_id(community_id);
                    CommunityValueList.setCommunity_name(community_name);

                    Community.add(CommunityValueList);
                } while (CommunityList.moveToNext());
            }
            Log.d("Communityspinnersize", "" + Community.size());

        }
        homeScreenBinding.communitySpinner.setAdapter(new CommonAdapter(this, Community, "CommunityList"));
    }


    public void habitationFilterSpinner(String dcode,String bcode, String pvcode) {
        Cursor HABList = null;
        HABList = db.rawQuery("SELECT * FROM " + DBHelper.HABITATION_TABLE_NAME + " where dcode = '" + dcode + "'and bcode = '" + bcode + "' and pvcode = '" + pvcode + "' order by habitation_name asc", null);

        Habitation.clear();
        HabitationOrdered.clear();

        if (HABList.getCount() > 0) {
            if (HABList.moveToFirst()) {
                do {
                    KVVTSurvey habList = new KVVTSurvey();
                    String districtCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String habCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABB_CODE));
                    String habName = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABITATION_NAME));
                    String habName_ta = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABITATION_NAME_TA));

                    habList.setDistictCode(districtCode);
                    habList.setBlockCode(blockCode);
                    habList.setPvCode(pvCode);
                    habList.setHabCode(habCode);
                    habList.setHabitationName(habName);
                    habList.setHabitationNameTa(habName_ta);

                    HabitationOrdered.add(habList);
                } while (HABList.moveToNext());
            }
            Log.d("Habitationspinnersize", "" + HabitationOrdered.size());

        }
        Collections.sort(HabitationOrdered, (lhs, rhs) -> lhs.getHabitationNameTa().compareTo(rhs.getHabitationNameTa()));
        KVVTSurvey habitationListValue = new KVVTSurvey();
        habitationListValue.setHabitationName(getResources().getString(R.string.select_habitation));
        habitationListValue.setHabitationNameTa(getResources().getString(R.string.select_habitation));
        Habitation.add(habitationListValue);
        for (int i = 0; i < HabitationOrdered.size(); i++) {
            KVVTSurvey habList = new KVVTSurvey();
            String districtCode = HabitationOrdered.get(i).getDistictCode();
            String blockCode = HabitationOrdered.get(i).getBlockCode();
            String pvCode = HabitationOrdered.get(i).getPvCode();
            String habCode = HabitationOrdered.get(i).getHabCode();
            String habName = HabitationOrdered.get(i).getHabitationName();
            String habName_ta = HabitationOrdered.get(i).getHabitationNameTa();

            habList.setDistictCode(districtCode);
            habList.setBlockCode(blockCode);
            habList.setPvCode(pvCode);
            habList.setHabCode(habCode);
            habList.setHabitationName(habName);
            habList.setHabitationNameTa(habName_ta);

            Habitation.add(habList);
        }
        homeScreenBinding.habitationSpinner.setAdapter(new CommonAdapter(this, Habitation, "HabitationList"));
    }
    public void streetFilterSpinner(String dcode,String bcode, String pvcode, String hab_code) {
        Cursor STRList = null;
        STRList = db.rawQuery("SELECT * FROM " + DBHelper.STREET_TABLE_NAME + " where dcode = '" + dcode + "'and bcode = '" + bcode + "' and pvcode = '" + pvcode + "' and hab_code = '" + hab_code + "' order by street_name_e asc", null);

        Street.clear();
        StreetOrdered.clear();

        if (STRList.getCount() > 0) {
            if (STRList.moveToFirst()) {
                do {
                    KVVTSurvey strList = new KVVTSurvey();
                    String districtCode = STRList.getString(STRList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = STRList.getString(STRList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = STRList.getString(STRList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String habCode = STRList.getString(STRList.getColumnIndexOrThrow(AppConstant.HABIT_CODE));
                    String strCode = STRList.getString(STRList.getColumnIndexOrThrow(AppConstant.STREET_CODE));
                    String strName = STRList.getString(STRList.getColumnIndexOrThrow(AppConstant.STREET_NAME_EN));
                    String strNameTa = STRList.getString(STRList.getColumnIndexOrThrow(AppConstant.STREET_NAME_TA));

                    strList.setDistictCode(districtCode);
                    strList.setBlockCode(blockCode);
                    strList.setPvCode(pvCode);
                    strList.setHabCode(habCode);
                    strList.setStreetCode(strCode);
                    strList.setStreetName(strName);
                    strList.setStreetNameTa(strNameTa);

                    StreetOrdered.add(strList);
                } while (STRList.moveToNext());
            }
            Log.d("Streetspinnersize", "" + StreetOrdered.size());

        }
        Collections.sort(StreetOrdered, (lhs, rhs) -> lhs.getStreetNameTa().compareTo(rhs.getStreetNameTa()));
        KVVTSurvey strListValue = new KVVTSurvey();
        strListValue.setStreetNameTa(getResources().getString(R.string.select_street));
        Street.add(strListValue);
        for (int i = 0; i < StreetOrdered.size(); i++) {
            KVVTSurvey strList = new KVVTSurvey();
            String districtCode = StreetOrdered.get(i).getDistictCode();
            String blockCode = StreetOrdered.get(i).getBlockCode();
            String pvCode = StreetOrdered.get(i).getPvCode();
            String habCode = StreetOrdered.get(i).getHabCode();
            String strCode = StreetOrdered.get(i).getStreetCode();
            String strName = StreetOrdered.get(i).getStreetName();
            String strNameTa = StreetOrdered.get(i).getStreetNameTa();

            strList.setDistictCode(districtCode);
            strList.setBlockCode(blockCode);
            strList.setPvCode(pvCode);
            strList.setHabCode(habCode);
            strList.setStreetCode(strCode);
            strList.setStreetName(strName);
            strList.setStreetNameTa(strNameTa);

            Street.add(strList);
        }
        homeScreenBinding.streetSpinner.setAdapter(new CommonAdapter(this, Street, "StreetList"));
        homeScreenBinding.streetSpinner.setSelection(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void logout() {
        dbData.open();
        ArrayList<KVVTSurvey> ImageCount = dbData.getSavedKVVTDetails();
        if (!Utils.isOnline()) {
            Utils.showAlert(this, "Logging out while offline may leads to loss of data!");
        } else {
            if (!(ImageCount.size() > 0)) {
                closeApplication();
            } else {
                Utils.showAlert(this, "Sync all the data before logout!");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncButtonVisibility();
    }


    public void viewServerData() {
        homeScreenBinding.viewServerData.setVisibility(View.VISIBLE);
        homeScreenBinding.viewServerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isOnline()) {
                    getKVVTList();
                } else {
                    Utils.showAlert(HomePage.this, "Your Internet seems to be Offline.Data can be viewed only in Online mode.");
                }
            }
        });
    }

    public void getKVVTList() {
        try {
            new ApiService(this).makeJSONObjectRequest("KVVTList", Api.Method.POST, UrlGenerator.getKVVTListUrl(), kvvtListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
     public void getAllKVVTList() {
            try {
                new ApiService(this).makeJSONObjectRequest("AllKVVTList", Api.Method.POST, UrlGenerator.getKVVTListUrl(), AllkvvtListJsonParams(), "not cache", this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    public JSONObject kvvtListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.kvvtListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("KVVTList", "" + authKey);
        return dataSet;
    }
    public JSONObject AllkvvtListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.AllkvvtListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("AllKVVTList", "" + authKey);
        return dataSet;
    }
    public JSONObject validateBenificiaryIdJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.validateBeniIdJsonParamsJsonParams(prefManager.getPvCode(), prefManager.getHabCode(),homeScreenBinding.benificiaryId.getText().toString()).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("validateBenificiaryId", "" + dataSet);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("KVVTList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                Log.d("KVVTListResponse", "" + responseDecryptedBlockKey);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertKVVTTask().execute(jsonObject);
                }else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")){
                    Utils.showAlert(this,"No Record Found!");
                }

            }
            if ("AllKVVTList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                Log.d("AllKVVTListResponse", "" + responseDecryptedBlockKey);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertAllKVVTTask().execute(jsonObject);
                }else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")){
                    Utils.showAlert(this,"No Record Found!");
                }

            }
            if ("StreetList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertStreetTask().execute(jsonObject);
                }
                Log.d("StreetList", "" + responseDecryptedBlockKey);
            }

            if ("ValidateBenificiaryId".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                Log.d("ValidateBeniIdResponse", "" + responseDecryptedBlockKey);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    flag=true;
                    JSONArray jsonArray=new JSONArray();
                    jsonArray=jsonObject.getJSONArray("JSON_DATA");
                    JSONObject js=jsonArray.getJSONObject(0);
                    String name= js.getString("c_name");
                    String fatherName= js.getString("beneficiary_father_name");
                    homeScreenBinding.nameLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.VISIBLE);
                    homeScreenBinding.name.setText(name);
                    homeScreenBinding.fatherName.setText(fatherName);
                }else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")){
                    homeScreenBinding.nameLayout.setVisibility(View.GONE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
                    homeScreenBinding.name.setText("");
                    homeScreenBinding.fatherName.setText("");
                    flag=false;
                    Utils.showAlert(this,"Invalid Beneficiary Id!");
                }else {homeScreenBinding.nameLayout.setVisibility(View.GONE);
                    homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
                    homeScreenBinding.name.setText("");
                    homeScreenBinding.fatherName.setText("");
                    flag=false;}

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void dataFromServer() {
        Intent intent = new Intent(this, ViewServerDataScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class InsertStreetTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<KVVTSurvey> streetlist_count = dbData.getAll_Street(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),prefManager.getHabCode());
            if (streetlist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        KVVTSurvey streetListValue = new KVVTSurvey();
                        try {
                            streetListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            streetListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            streetListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            streetListValue.setHabCode(jsonArray.getJSONObject(i).getString(AppConstant.HABIT_CODE));
                            streetListValue.setStreetCode(jsonArray.getJSONObject(i).getString(AppConstant.STREET_CODE));
                            streetListValue.setStreetName(jsonArray.getJSONObject(i).getString(AppConstant.STREET_NAME_EN));
                            streetListValue.setStreetNameTa(jsonArray.getJSONObject(i).getString(AppConstant.STREET_NAME_TA));

                            dbData.insertStreet(streetListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            streetFilterSpinner(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),prefManager.getHabCode());
        }

    }


    public void closeApplication() {
        new MyDialog(this).exitDialog(this, getResources().getString(R.string.are_u_sure_log_out), "Logout");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, getResources().getString(R.string.are_u_sure_exit), "Exit");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        } else {

            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    }

    public void validateFields() {
        if ((homeScreenBinding.villageSpinner.getSelectedItem() != null) &&(!getResources().getString(R.string.select_village).equalsIgnoreCase(Village.get(homeScreenBinding.villageSpinner.getSelectedItemPosition()).getPvName()))
        && (Village.get(homeScreenBinding.villageSpinner.getSelectedItemPosition()).getPvName() != null)) {
            if ((homeScreenBinding.habitationSpinner.getSelectedItem() != null) &&(!getResources().getString(R.string.select_habitation).equalsIgnoreCase(Habitation.get(homeScreenBinding.habitationSpinner.getSelectedItemPosition()).getHabitationName()))
            && (Habitation.get(homeScreenBinding.habitationSpinner.getSelectedItemPosition()).getHabitationName() != null)) {
                if ((homeScreenBinding.existingUserYes.isChecked()) || homeScreenBinding.existingUserNo.isChecked()) {
                    if((homeScreenBinding.existingUserYes.isChecked())){
                        validateExUser();
                    }else if((homeScreenBinding.existingUserNo.isChecked())){
                        validateNewUser();
                    }
                } else {
                    Utils.showAlert(this, getResources().getString(R.string.choose_existing_hut_dweeler_or_not));
                }
            } else {
                Utils.showAlert(this, getResources().getString(R.string.select_habitation));
            }
        } else {
            Utils.showAlert(this, getResources().getString(R.string.select_village));
        }

    }

    private void validateNewUser() {
        if (!homeScreenBinding.name.getText().toString().isEmpty()) {
            if(homeScreenBinding.radioHusband.isChecked() || homeScreenBinding.radioFather.isChecked()){
            if (!homeScreenBinding.fatherName.getText().toString().isEmpty()) {
                if ((homeScreenBinding.streetSpinner.getSelectedItem() != null) &&(!getResources().getString(R.string.select_street).equalsIgnoreCase(Street.get(homeScreenBinding.streetSpinner.getSelectedItemPosition()).getStreetNameTa()))
                && (Street.get(homeScreenBinding.streetSpinner.getSelectedItemPosition()).getStreetNameTa() != null)) {
                    if (!homeScreenBinding.doorNo.getText().toString().isEmpty()) {
                        if ((homeScreenBinding.communitySpinner.getSelectedItem() != null) &&(!getResources().getString(R.string.select_community).equalsIgnoreCase(Community.get(homeScreenBinding.communitySpinner.getSelectedItemPosition()).getCommunity_name()))
                        && (Community.get(homeScreenBinding.communitySpinner.getSelectedItemPosition()).getCommunity_name() != null)) {
                            checkLegalYesNo();
                        }else {
                            Utils.showAlert(this, getResources().getString(R.string.select_community));
                        }
                    }else {
                        Utils.showAlert(this, getResources().getString(R.string.enter_door_no));
                    }

                }else {
                    Utils.showAlert(this, getResources().getString(R.string.select_street));
                }

            }else {
                Utils.showAlert(this, getResources().getString(R.string.enter_father_or_husband_name));
            }
            }else {
                Utils.showAlert(this, getResources().getString(R.string.choose_father_or_husband_name));
            }
        }else {
            Utils.showAlert(this, getResources().getString(R.string.enter_beneficiary_name));
        }
    }

    private void validateExUser() {
        if (!homeScreenBinding.benificiaryId.getText().toString().isEmpty()) {
            if (flag) {
                checkLegalYesNo();

            } else {
                Utils.showAlert(this, getResources().getString(R.string.validate_beneficiary_id));
            }
        } else {
            Utils.showAlert(this, getResources().getString(R.string.enter_beneficiary_id));
        }
    }
/*
    public void validateFields() {
        if (!getResources().getString(R.string.select_village).equalsIgnoreCase(Village.get(homeScreenBinding.villageSpinner.getSelectedItemPosition()).getPvName())) {
            if (!"Select Habitation".equalsIgnoreCase(Habitation.get(homeScreenBinding.habitationSpinner.getSelectedItemPosition()).getHabitationName())) {
                        if (!homeScreenBinding.benificiaryId.getText().toString().isEmpty()) {
                            if (flag) {
                                checkLegalYesNo();

                            } else {
                                Utils.showAlert(this, "First validate benificiary Id!");
                            }
                        } else {
                            Utils.showAlert(this, "Enter the  benificiary Id!");
                        }
            } else {
                Utils.showAlert(this, "Select Habitation!");
            }
        } else {
            Utils.showAlert(this, "Select Village!");
        }

    }
*/

    public void checkLegalYesNo() {
        if(isExistingUser.equals("Y")) {
            if (!getResources().getString(R.string.select_automatic_exclusion_creteria).equalsIgnoreCase(Scheme.get(homeScreenBinding.schemeSpinner.getSelectedItemPosition()).getExclusion_criteria())
                    && (Scheme.get(homeScreenBinding.schemeSpinner.getSelectedItemPosition()).getExclusion_criteria() != null)) {
                if (auto_rejection.equals("N")) {
                /*if (homeScreenBinding.pattaYes.isChecked() || homeScreenBinding.pattaNo.isChecked()) {
                    if (homeScreenBinding.awaasYes.isChecked() || homeScreenBinding.awassNo.isChecked()) {
                        if (photoRequired.equals("Y")) {
                            takePhoto("Take Photo");
                        } else {
                            takePhoto("Save details");
                        }
                    } else {
                        Utils.showAlert(this, "Please Choose Awaas Plus List Status!");
                    }
                } else {
                    Utils.showAlert(this, "Please Choose Patta Available Status!");
                }*/
                    checkBoxConditionCheck();
                } else {
                    if (photoRequired.equals("Y")) {
                        takePhoto("Take Photo");
                    } else {
                        takePhoto("Save details");
                    }
                }
            } else {
                Utils.showAlert(this, getResources().getString(R.string.select_automatic_exclusion_creteria));
            }
        }
        else {
            if (auto_rejection.equals("N")) {
                /*if (homeScreenBinding.pattaYes.isChecked() || homeScreenBinding.pattaNo.isChecked()) {
                    if (homeScreenBinding.awaasYes.isChecked() || homeScreenBinding.awassNo.isChecked()) {
                        if (photoRequired.equals("Y")) {
                            takePhoto("Take Photo");
                        } else {
                            takePhoto("Save details");
                        }
                    } else {
                        Utils.showAlert(this, "Please Choose Awaas Plus List Status!");
                    }
                } else {
                    Utils.showAlert(this, "Please Choose Patta Available Status!");
                }*/
                checkBoxConditionCheck();
            } else {
                if (photoRequired.equals("Y")) {
                    takePhoto("Take Photo");
                } else {
                    takePhoto("Save details");
                }
            }
        }
        /*if ((homeScreenBinding.migYes.isChecked()) || homeScreenBinding.migNo.isChecked()) {
            if((homeScreenBinding.migYes.isChecked())){
                if (!"Select Exclusion Criteria".equalsIgnoreCase(Scheme.get(homeScreenBinding.schemeSpinner.getSelectedItemPosition()).getExclusion_criteria())
                && (Scheme.get(homeScreenBinding.schemeSpinner.getSelectedItemPosition()).getExclusion_criteria() != null)) {
                     if(photoRequired.equals("Y")){
                         takePhoto("Take Photo");
                     }else {
                         takePhoto("Save details");
                     }
                }else {
                    Utils.showAlert(this, "Select Exclusion Criteria!");
                }
            }
            else if((homeScreenBinding.migNo.isChecked())){
                takePhoto(homeScreenBinding.saveData.getText().toString());
            }
        }
        else {
            Utils.showAlert(this, "Check the beneficiary is Eligible to reject or not!");
        }*/
    }


    public void takePhoto(String buttonTxt) {
        Log.d("buttonTxt",""+buttonTxt);

        String street_code = "";
        String community_id = "";
        String door_no = "";
        String pvcode = Village.get(homeScreenBinding.villageSpinner.getSelectedItemPosition()).getPvCode();
        String habcode = Habitation.get(homeScreenBinding.habitationSpinner.getSelectedItemPosition()).getHabCode();

        String benificiary_id = homeScreenBinding.benificiaryId.getText().toString();
        String benificiary_name = homeScreenBinding.name.getText().toString();
        String benificiary_fatherName = homeScreenBinding.fatherName.getText().toString();


        if(isExistingUser.equalsIgnoreCase("N")){
             street_code = Street.get(homeScreenBinding.streetSpinner.getSelectedItemPosition()).getStreetCode();
             community_id = Community.get(homeScreenBinding.communitySpinner.getSelectedItemPosition()).getCommunity_id();
             door_no = homeScreenBinding.doorNo.getText().toString();
        }else{
             street_code ="";
             community_id = "";
             door_no ="";
        }

        ContentValues registerValue = new ContentValues();
        registerValue.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
        registerValue.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
        registerValue.put(AppConstant.PV_CODE, pvcode);
        registerValue.put(AppConstant.HAB_CODE, habcode);
        registerValue.put(AppConstant.PV_NAME, Village.get(homeScreenBinding.villageSpinner.getSelectedItemPosition()).getPvNameTa());
        registerValue.put(AppConstant.HABITATION_NAME, Habitation.get(homeScreenBinding.habitationSpinner.getSelectedItemPosition()).getHabitationNameTa());
        registerValue.put(AppConstant.BENEFICIARY_NAME, benificiary_name);
        registerValue.put(AppConstant.BENEFICIARY_FATHER_NAME, benificiary_fatherName);
        registerValue.put(AppConstant.BENEFICIARY_ID, benificiary_id);
        registerValue.put(AppConstant.EXCLUSION_CRITERIA_ID, prefManager.getKeySchemeCode());
        registerValue.put(AppConstant.EXCLUSION_CRITERIA_TA, exclusion_criteria_ta);
        registerValue.put(AppConstant.PERSON_ELIGIBLE, auto_rejection);
        registerValue.put(AppConstant.BUTTON_TEXT, buttonTxt);
        registerValue.put(AppConstant.EXISTING_USER, isExistingUser);
        registerValue.put(AppConstant.BENEFICIARY_FAT_HUS_STATUS, isFaHus);
        registerValue.put(AppConstant.STREET_CODE, street_code);
        registerValue.put(AppConstant.COMMUNITY_ID, community_id);
        registerValue.put(AppConstant.DOOR_NO, door_no);
        registerValue.put(AppConstant.IS_AWAAS_PLUS_LISTED, is_awaas_plus_list);
        registerValue.put(AppConstant.PATTA_AVAILABLE, patta_avilable_status);
        registerValue.put(AppConstant.IS_DOCUMENT_AVAILABLE, is_document_available);
        registerValue.put(AppConstant.IS_NATHAM_LAND_AVAILABLE, is_natham_land_available);

        long id = db.insert(DBHelper.SAVE_KVVT_DETAILS, null, registerValue);
        Log.d("insert_id",String.valueOf(id));
        Log.d("isFaHus",isFaHus);
        flag=false;

        if(buttonTxt.equals("Take Photo")){
            if(id > 0) {

                Cursor cursor = db.rawQuery("SELECT MAX(id) FROM " + DBHelper.SAVE_KVVT_DETAILS, null);
                Log.d("cursor_count", String.valueOf(cursor.getCount()));
                if (cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            lastInsertedID = String.valueOf(cursor.getInt(0));
                            Log.d("lastID", "" + lastInsertedID);
                        } while (cursor.moveToNext());
                    }
                }

                Intent intent = new Intent(this, TakePhotoScreen.class);
                intent.putExtra("lastInsertedID",lastInsertedID);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }else {
            Utils.showAlert(this,"Saved");
            syncButtonVisibility();
            emptyValue();
//            finish();
//            startActivity(getIntent());
        }


    }

    public void syncButtonVisibility() {
        dbData.open();
        ArrayList<KVVTSurvey> workImageCount = dbData.getSavedKVVTDetails();

        if (workImageCount.size() > 0) {
            homeScreenBinding.synData.setVisibility(View.VISIBLE);
        }else {
            homeScreenBinding.synData.setVisibility(View.GONE);
        }
    }

    public void emptyValue() {
        flag=false;
        homeScreenBinding.villageSpinner.setSelection(0);
        homeScreenBinding.habitationSpinner.setSelection(0);
        homeScreenBinding.schemeSpinner.setSelection(0);
        homeScreenBinding.streetSpinner.setSelection(0);
        homeScreenBinding.communitySpinner.setSelection(0);
        //homeScreenBinding.scheLayout.setVisibility(View.GONE);
        //homeScreenBinding.selectScheTv.setVisibility(View.GONE);
//        homeScreenBinding.takePicLayout.setVisibility(View.GONE);
        homeScreenBinding.nameLayout.setVisibility(View.GONE);
        homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
        homeScreenBinding.strLayout.setVisibility(View.GONE);
        homeScreenBinding.selectStrTv.setVisibility(View.GONE);
        homeScreenBinding.selectCommunityTv.setVisibility(View.GONE);
        homeScreenBinding.communityLayout.setVisibility(View.GONE);
        homeScreenBinding.FHRadioLayout.setVisibility(View.GONE);
        homeScreenBinding.doorNoLayout.setVisibility(View.GONE);
        homeScreenBinding.tick.setVisibility(View.GONE);
        homeScreenBinding.name.setText("");
        homeScreenBinding.fatherName.setText("");
        homeScreenBinding.benificiaryId.setText("");
        homeScreenBinding.doorNo.setText("");
        homeScreenBinding.migYes.setChecked(false);
        homeScreenBinding.migNo.setChecked(false);
        homeScreenBinding.migYes.setEnabled(true);
        homeScreenBinding.migNo.setEnabled(true);
        homeScreenBinding.migYesLayout.setVisibility(View.VISIBLE);
        homeScreenBinding.migNoLayout.setVisibility(View.VISIBLE);
        homeScreenBinding.existingUserNo.setChecked(false);
        homeScreenBinding.existingUserYes.setChecked(false);
        homeScreenBinding.eligibleTv.setVisibility(View.GONE);
        homeScreenBinding.benfMigTv.setVisibility(View.GONE);
        isEligible = "";
        isExistingUser = "";
        isFaHus = "";

    }

    public void openPendingScreen() {
        Intent intent = new Intent(this, PendingScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    public class InsertKVVTTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.deleteKVVTTable();
            dbData.open();
            ArrayList<KVVTSurvey> all_kvvtListCount = dbData.getAll_KVVTList("","");
            if (all_kvvtListCount.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        KVVTSurvey kvvtSurvey = new KVVTSurvey();
                        try {
                            kvvtSurvey.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            kvvtSurvey.setHabCode(jsonArray.getJSONObject(i).getString(AppConstant.HABIT_CODE));
                            kvvtSurvey.setBeneficiaryId(jsonArray.getJSONObject(i).getString(AppConstant.BENEFICIARY_ID));
                            kvvtSurvey.setBeneficiaryName(jsonArray.getJSONObject(i).getString(AppConstant.BENEFICIARY_NAME));
                            kvvtSurvey.setBeneficiaryFatherName(jsonArray.getJSONObject(i).getString(AppConstant.BENEFICIARY_FATHER_NAME));
                            kvvtSurvey.setExclusion_criteria_id(jsonArray.getJSONObject(i).getString(AppConstant.EXCLUSION_CRITERIA_ID));
                            kvvtSurvey.setHabitationName(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME));
                            kvvtSurvey.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));
                            kvvtSurvey.setEligible_for_auto_exclusion(jsonArray.getJSONObject(i).getString(AppConstant.ELIGIBLE_FOR_AUTO_EXCLUSION));
                            kvvtSurvey.setPhoto_availavle(jsonArray.getJSONObject(i).getString(AppConstant.PHOTO_AVAILABLE));
                            kvvtSurvey.setPatta_available_status(jsonArray.getJSONObject(i).getString(AppConstant.PATTA_AVAILABLE));
                            kvvtSurvey.setIs_awaas_plus_list(jsonArray.getJSONObject(i).getString(AppConstant.IS_AWAAS_PLUS_LISTED));
                            kvvtSurvey.setHabitationNameTa(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME_TA));
                            kvvtSurvey.setPvNameTa(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME_TA));
                            kvvtSurvey.setExclusion_criteria_ta(jsonArray.getJSONObject(i).getString(AppConstant.EXCLUSION_CRITERIA_TA));

                            kvvtSurvey.setIS_DOCUMENT_AVAILABLE(jsonArray.getJSONObject(i).getString(AppConstant.IS_DOCUMENT_AVAILABLE));
                            kvvtSurvey.setIS_NATHAM_LAND_AVAILABLE(jsonArray.getJSONObject(i).getString("is_natham_land"));
                            dbData.insertKVVT(kvvtSurvey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(HomePage.this, "Downloading", true, false, null);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressHUD!=null){
                progressHUD.cancel();
            }
            dataFromServer();

        }
    }
    public class InsertAllKVVTTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.deleteAllKVVTTable();
            dbData.open();
            ArrayList<KVVTSurvey> all_kvvtListCount = dbData.getSourceAll_KVVTList("","");
            if (all_kvvtListCount.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        KVVTSurvey kvvtSurvey = new KVVTSurvey();
                        try {

                            kvvtSurvey.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            kvvtSurvey.setHabCode(jsonArray.getJSONObject(i).getString(AppConstant.HABIT_CODE));
                            kvvtSurvey.setBeneficiaryId(jsonArray.getJSONObject(i).getString(AppConstant.BENEFICIARY_ID));
                            kvvtSurvey.setBeneficiaryName(jsonArray.getJSONObject(i).getString(AppConstant.BENEFICIARY_NAME));
                            kvvtSurvey.setBeneficiaryFatherName(jsonArray.getJSONObject(i).getString(AppConstant.BENEFICIARY_FATHER_NAME));
                            kvvtSurvey.setExclusion_criteria_id(jsonArray.getJSONObject(i).getString(AppConstant.EXCLUSION_CRITERIA_ID));
                            kvvtSurvey.setHabitationName(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME));
                            kvvtSurvey.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));
                            kvvtSurvey.setEligible_for_auto_exclusion(jsonArray.getJSONObject(i).getString(AppConstant.ELIGIBLE_FOR_AUTO_EXCLUSION));
                            kvvtSurvey.setPatta_available_status(jsonArray.getJSONObject(i).getString(AppConstant.PATTA_AVAILABLE));
                            kvvtSurvey.setIs_awaas_plus_list(jsonArray.getJSONObject(i).getString(AppConstant.IS_AWAAS_PLUS_LISTED));


                            kvvtSurvey.setIS_DOCUMENT_AVAILABLE(jsonArray.getJSONObject(i).getString(AppConstant.IS_DOCUMENT_AVAILABLE));
                            kvvtSurvey.setIS_NATHAM_LAND_AVAILABLE(jsonArray.getJSONObject(i).getString("is_natham_land"));
                            dbData.insertAllKVVT(kvvtSurvey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(HomePage.this, "Downloading", true, false, null);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressHUD!=null){
                progressHUD.cancel();
            }


        }
    }

    public void getCurrentYearPreviousYear(){
        /*int month=0;
        current_year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        if(month <=3) {
            previous_year = current_year - 1;
            homeScreenBinding.headingTextYear.setText(""+previous_year+"-"+current_year);
        }
        else if(month > 3){
            previous_year = current_year + 1;
            homeScreenBinding.headingTextYear.setText(""+current_year+"-"+previous_year);
        }*/

        homeScreenBinding.headingTextYear.setText("2020 - 2021");
    }

    public void checkBoxConditionCheck(){
        if(homeScreenBinding.pattaYes.isChecked()||homeScreenBinding.pattaNo.isChecked()){
            if(homeScreenBinding.pattaYes.isChecked()){
                if (homeScreenBinding.awaasYes.isChecked() || homeScreenBinding.awassNo.isChecked()) {
                    if (photoRequired.equals("Y")) {
                        takePhoto("Take Photo");
                    } else {
                        takePhoto("Save details");
                    }
                } else {
                    Utils.showAlert(this, getResources().getString(R.string.choose_awass_list));

                }
            }
            else if(homeScreenBinding.pattaNo.isChecked()) {
                if(homeScreenBinding.documentYes.isChecked()||homeScreenBinding.documentNo.isChecked()){
                    if(homeScreenBinding.documentYes.isChecked()){
                        if (homeScreenBinding.awaasYes.isChecked() || homeScreenBinding.awassNo.isChecked()) {
                            if (photoRequired.equals("Y")) {
                                takePhoto("Take Photo");
                            }
                            else {
                                takePhoto("Save details");
                            }
                        }
                        else {
                            Utils.showAlert(this, getResources().getString(R.string.choose_awass_list));

                        }
                    }
                    else if(homeScreenBinding.documentNo.isChecked()){
                        if(homeScreenBinding.nathamLandYes.isChecked()||homeScreenBinding.nathamLandNo.isChecked()){
                            if (homeScreenBinding.awaasYes.isChecked() || homeScreenBinding.awassNo.isChecked()) {
                                if (photoRequired.equals("Y")) {
                                    takePhoto("Take Photo");
                                }
                                else {
                                    takePhoto("Save details");
                                }
                            } else {
                                Utils.showAlert(this, getResources().getString(R.string.choose_awass_list));
                            }
                        }
                        else {
                            Utils.showAlert(this, getResources().getString(R.string.choose_natham_land));

                        }

                    }
                }
                else {
                    Utils.showAlert(this, getResources().getString(R.string.choose_document_status));

                }
            }
        }
        else {
            Utils.showAlert(this, getResources().getString(R.string.choose_patta_status));

        }

    }

    public class fetchParticularKvvtIdtask extends AsyncTask<Void, Void,
            ArrayList<KVVTSurvey>> {
        @Override
        protected ArrayList<KVVTSurvey> doInBackground(Void... params) {
            dbData.open();
            kavvtIdList = new ArrayList<>();
            kavvtIdList = dbData.getSourceAll_KVVTList(prefManager.getPvCode(), prefManager.getHabCode());
            Log.d("savedList_COUNT", String.valueOf(kavvtIdList.size()));
            return kavvtIdList;
        }

        @Override
        protected void onPostExecute(ArrayList<KVVTSurvey> savedList) {
            super.onPostExecute(savedList);

        }


    }

}
