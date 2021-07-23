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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.KVVTSurvey.R;
import com.nic.KVVTSurvey.adapter.CommonAdapter;
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
    private List<KVVTSurvey> Habitation = new ArrayList<>();
    private List<KVVTSurvey> Scheme = new ArrayList<>();
    String lastInsertedID;
    String isMigrated = "";
    private ProgressHUD progressHUD;
    Boolean flag=false;
    Boolean Etflag=false;


    String pref_Village;
    String pref_Scheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        homeScreenBinding = DataBindingUtil.setContentView(this, R.layout.home_screen);
        homeScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            isHome = bundle.getString("Home");
        }
        villageFilterSpinner(prefManager.getBlockCode());
        schemeFilterSpinner(prefManager.getBlockCode());
        homeScreenBinding.selectScheTv.setVisibility(View.GONE);
        homeScreenBinding.scheLayout.setVisibility(View.GONE);
//        homeScreenBinding.takePicLayout.setVisibility(View.GONE);
        homeScreenBinding.nameLayout.setVisibility(View.GONE);
        homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
        homeScreenBinding.villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    pref_Village = Village.get(position).getPvName();
                    prefManager.setVillageListPvName(pref_Village);
                    prefManager.setPvCode(Village.get(position).getPvCode());
                    habitationFilterSpinner(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode());
                }else {
                    prefManager.setVillageListPvName("");
                    prefManager.setPvCode("");
                    homeScreenBinding.habitationSpinner.setAdapter(null);
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

                    pref_Scheme = Scheme.get(position).getExclusion_criteria();
                    prefManager.setKeySchemeListName(pref_Scheme);
                    prefManager.setKeySchemeCode(Scheme.get(position).getExclusion_criteria_id());
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
                }else {
                    prefManager.setHabCode("");
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
                    isMigrated = "Y";
                    homeScreenBinding.migNo.setChecked(false);
                    validateYesNo();
                }
            }
        });

        homeScreenBinding.migNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isMigrated = "N";
                    homeScreenBinding.migYes.setChecked(false);
                    validateYesNo();
                }
            }
        });
        homeScreenBinding.seccId.setOnTouchListener(new View.OnTouchListener() {
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
        homeScreenBinding.seccId.addTextChangedListener(new TextWatcher() {
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
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!homeScreenBinding.seccId.getText().toString().isEmpty()){
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
                    if(!homeScreenBinding.seccId.getText().equals("")){
                        validateBenificiaryId(homeScreenBinding.seccId.getText().toString());}
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
        viewServerData();

    }

    private void validateBenificiaryId(String toString) {
        try {
            new ApiService(this).makeJSONObjectRequest("ValidateBenificiaryId", Api.Method.POST, UrlGenerator.getPMAYListUrl(), validateBenificiaryIdJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void validateYesNo() {
        if ( isMigrated.equalsIgnoreCase("Y")) {
            homeScreenBinding.takePicLayout.setVisibility(View.GONE);
            homeScreenBinding.selectScheTv.setVisibility(View.VISIBLE);
            homeScreenBinding.scheLayout.setVisibility(View.VISIBLE);
            homeScreenBinding.takePhotoTv.setText("Save details");
        } else if (isMigrated.equalsIgnoreCase("N")) {
            homeScreenBinding.schemeSpinner.setSelection(0);
            prefManager.setKeySchemeCode("");
            homeScreenBinding.takePicLayout.setVisibility(View.GONE);
            homeScreenBinding.selectScheTv.setVisibility(View.GONE);
            homeScreenBinding.scheLayout.setVisibility(View.GONE);
            homeScreenBinding.takePhotoTv.setText("Take Photo");
        }
    }

//    public boolean validateCheck() {
//        if (isAlive.equalsIgnoreCase("") || isMigrated.equalsIgnoreCase("Y")) {
//            homeScreenBinding.takePhotoTv.setText("Save details");
//        } else if (isAlive.equalsIgnoreCase("Y") && isMigrated.equalsIgnoreCase("N")) {
//            homeScreenBinding.takePhotoTv.setText("Take Photo");
//        } else if (isAlive.equalsIgnoreCase("N") && isLegal.equalsIgnoreCase("N")) {
//            homeScreenBinding.takePhotoTv.setText("Save details");
//        } else if (isAlive.equalsIgnoreCase("N") && isLegal.equalsIgnoreCase("Y") && isMigrated.equalsIgnoreCase("Y")) {
//            homeScreenBinding.takePhotoTv.setText("Save details");
//        } else if (isAlive.equalsIgnoreCase("N") && isLegal.equalsIgnoreCase("Y") && isMigrated.equalsIgnoreCase("N")) {
//            homeScreenBinding.takePhotoTv.setText("Take Photo");
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
        KVVTSurvey villageListValue = new KVVTSurvey();
        villageListValue.setPvName("Select Village");
        Village.add(villageListValue);
        if (VillageList.getCount() > 0) {
            if (VillageList.moveToFirst()) {
                do {
                    KVVTSurvey villageList = new KVVTSurvey();
                    String districtCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String pvname = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_NAME));

                    villageList.setDistictCode(districtCode);
                    villageList.setBlockCode(blockCode);
                    villageList.setPvCode(pvCode);
                    villageList.setPvName(pvname);

                    Village.add(villageList);
                    Log.d("spinnersize", "" + Village.size());
                } while (VillageList.moveToNext());
            }
        }
        homeScreenBinding.villageSpinner.setAdapter(new CommonAdapter(this, Village, "VillageList"));
    }
    public void schemeFilterSpinner(String filterVillage) {
        Cursor VillageList = null;
        VillageList = db.rawQuery("SELECT * FROM " + DBHelper.SCHEME_TABLE_NAME , null);

        Scheme.clear();
        KVVTSurvey villageListValue = new KVVTSurvey();
        villageListValue.setExclusion_criteria("Select Exclusion Criteria");
        Scheme.add(villageListValue);
        if (VillageList.getCount() > 0) {
            if (VillageList.moveToFirst()) {
                do {
                    KVVTSurvey villageList = new KVVTSurvey();
                    String EXCLUSION_CRITERIA_ID = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.EXCLUSION_CRITERIA_ID));
                    String EXCLUSION_CRITERIA = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.EXCLUSION_CRITERIA));

                    villageList.setExclusion_criteria_id(EXCLUSION_CRITERIA_ID);
                    villageList.setExclusion_criteria(EXCLUSION_CRITERIA);

                    Scheme.add(villageList);
                    Log.d("spinnersize", "" + Scheme.size());
                } while (VillageList.moveToNext());
            }
        }
        homeScreenBinding.schemeSpinner.setAdapter(new CommonAdapter(this, Scheme, "SchemeList"));
    }


    public void habitationFilterSpinner(String dcode,String bcode, String pvcode) {
        Cursor HABList = null;
        HABList = db.rawQuery("SELECT * FROM " + DBHelper.HABITATION_TABLE_NAME + " where dcode = '" + dcode + "'and bcode = '" + bcode + "' and pvcode = '" + pvcode + "' order by habitation_name asc", null);

        Habitation.clear();
        KVVTSurvey habitationListValue = new KVVTSurvey();
        habitationListValue.setHabitationName("Select Habitation");
        Habitation.add(habitationListValue);
        if (HABList.getCount() > 0) {
            if (HABList.moveToFirst()) {
                do {
                    KVVTSurvey habList = new KVVTSurvey();
                    String districtCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String habCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABB_CODE));
                    String habName = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABITATION_NAME));

                    habList.setDistictCode(districtCode);
                    habList.setBlockCode(blockCode);
                    habList.setPvCode(pvCode);
                    habList.setHabCode(habCode);
                    habList.setHabitationName(habName);

                    Habitation.add(habList);
                    Log.d("spinnersize", "" + Habitation.size());
                } while (HABList.moveToNext());
            }
        }
        homeScreenBinding.habitationSpinner.setAdapter(new CommonAdapter(this, Habitation, "HabitationList"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void logout() {
        dbData.open();
        ArrayList<KVVTSurvey> ImageCount = dbData.getSavedPMAYDetails();
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
                    getPMAYList();
                } else {
                    Utils.showAlert(HomePage.this, "Your Internet seems to be Offline.Data can be viewed only in Online mode.");
                }
            }
        });
    }

    public void getPMAYList() {
        try {
            new ApiService(this).makeJSONObjectRequest("PMAYList", Api.Method.POST, UrlGenerator.getPMAYListUrl(), pmayListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject pmayListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.pmayListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("PMAYList", "" + authKey);
        return dataSet;
    }
    public JSONObject validateBenificiaryIdJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.validateBeniIdJsonParamsJsonParams(prefManager.getPvCode(), prefManager.getHabCode(),homeScreenBinding.seccId.getText().toString()).toString());
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

            if ("PMAYList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                Log.d("PMAYListResponse", "" + responseDecryptedBlockKey);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertPMAYTask().execute(jsonObject);
                }else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")){
                    Utils.showAlert(this,"No Record Found!");
                }

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
                    Utils.showAlert(this,"Invalid Benificiary Id!");
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



    public void closeApplication() {
        new MyDialog(this).exitDialog(this, "Are you sure you want to Logout?", "Logout");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, "Are you sure you want to exit ?", "Exit");
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
        if (!"Select Village".equalsIgnoreCase(Village.get(homeScreenBinding.villageSpinner.getSelectedItemPosition()).getPvName())) {
            if (!"Select Habitation".equalsIgnoreCase(Habitation.get(homeScreenBinding.habitationSpinner.getSelectedItemPosition()).getHabitationName())) {
                        if (!homeScreenBinding.seccId.getText().toString().isEmpty()) {
//                            if (Utils.isValidMobile(homeScreenBinding.seccId.getText().toString())) {
                                checkLegalYesNo();

                           /* } else {
                                Utils.showAlert(this, "Seec Id Must be 7 Digit!");
                            }*/
                        } else {
                            Utils.showAlert(this, "Enter the  Seec Id!");
                        }
            } else {
                Utils.showAlert(this, "Select Habitation!");
            }
        } else {
            Utils.showAlert(this, "Select Village!");
        }

    }

    public void checkLegalYesNo() {

        if ((homeScreenBinding.migYes.isChecked()) || homeScreenBinding.migNo.isChecked()) {
            if((homeScreenBinding.migYes.isChecked())){
                if (!"Select Exclusion Criteria".equalsIgnoreCase(Scheme.get(homeScreenBinding.schemeSpinner.getSelectedItemPosition()).getPvName())) {
                    takePhoto(homeScreenBinding.takePhotoTv.getText().toString());
                }else {
                    Utils.showAlert(this, "Select Exclusion Criteria!");
                }
            }else if((homeScreenBinding.migNo.isChecked())){
                takePhoto(homeScreenBinding.takePhotoTv.getText().toString());
            }
        } else {
            Utils.showAlert(this, "Check the beneficiary is Eligible to reject or not!");
        }
    }


    public void takePhoto(String buttonTxt) {
        Log.d("buttonTxt",""+buttonTxt);
        String pvcode = Village.get(homeScreenBinding.villageSpinner.getSelectedItemPosition()).getPvCode();
        String habcode = Habitation.get(homeScreenBinding.habitationSpinner.getSelectedItemPosition()).getHabCode();
       /* String beneficiary_name = homeScreenBinding.name.getText().toString();
        String father_name = homeScreenBinding.fatherName.getText().toString();*/
        String secc_id = homeScreenBinding.seccId.getText().toString();

        ContentValues registerValue = new ContentValues();
        registerValue.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
        registerValue.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
        registerValue.put(AppConstant.PV_CODE, pvcode);
        registerValue.put(AppConstant.HAB_CODE, habcode);
        registerValue.put(AppConstant.PV_NAME, Village.get(homeScreenBinding.villageSpinner.getSelectedItemPosition()).getPvName());
        registerValue.put(AppConstant.HABITATION_NAME, Habitation.get(homeScreenBinding.habitationSpinner.getSelectedItemPosition()).getHabitationName());
        registerValue.put(AppConstant.BENEFICIARY_NAME, "");
        registerValue.put(AppConstant.BENEFICIARY_FATHER_NAME, "");
        registerValue.put(AppConstant.SECC_ID, secc_id);
        registerValue.put(AppConstant.EXCLUSION_CRITERIA_ID, prefManager.getKeySchemeCode());
        registerValue.put(AppConstant.PERSON_ALIVE, "");
        registerValue.put(AppConstant.LEGAL_HEIR_AVAILABLE, "");
        registerValue.put(AppConstant.PERSON_MIGRATED, isMigrated);
        registerValue.put(AppConstant.BUTTON_TEXT, buttonTxt);

        long id = db.insert(DBHelper.SAVE_PMAY_DETAILS, null, registerValue);
        Log.d("insert_id",String.valueOf(id));

        if(buttonTxt.equals("Take Photo")){
            if(id > 0) {

                Cursor cursor = db.rawQuery("SELECT MAX(id) FROM " + DBHelper.SAVE_PMAY_DETAILS, null);
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
        ArrayList<KVVTSurvey> workImageCount = dbData.getSavedPMAYDetails();

        if (workImageCount.size() > 0) {
            homeScreenBinding.synData.setVisibility(View.VISIBLE);
        }else {
            homeScreenBinding.synData.setVisibility(View.GONE);
        }
    }

    public void emptyValue() {
        homeScreenBinding.villageSpinner.setSelection(0);
        homeScreenBinding.habitationSpinner.setSelection(0);
        homeScreenBinding.schemeSpinner.setSelection(0);
        homeScreenBinding.scheLayout.setVisibility(View.GONE);
        homeScreenBinding.selectScheTv.setVisibility(View.GONE);
//        homeScreenBinding.takePicLayout.setVisibility(View.GONE);
        homeScreenBinding.nameLayout.setVisibility(View.GONE);
        homeScreenBinding.fatherNameLayout.setVisibility(View.GONE);
        homeScreenBinding.name.setText("");
        homeScreenBinding.fatherName.setText("");
        homeScreenBinding.seccId.setText("");
        homeScreenBinding.migYes.setChecked(false);
        homeScreenBinding.migNo.setChecked(false);
        isMigrated = "";

    }

    public void openPendingScreen() {
        Intent intent = new Intent(this, PendingScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    public class InsertPMAYTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.deletePMAYTable();
            dbData.open();
            ArrayList<KVVTSurvey> all_pmayListCount = dbData.getAll_PMAYList("","");
            if (all_pmayListCount.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        KVVTSurvey pmaySurvey = new KVVTSurvey();
                        try {
                            pmaySurvey.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            pmaySurvey.setHabCode(jsonArray.getJSONObject(i).getString(AppConstant.HABIT_CODE));
                            pmaySurvey.setBeneficiaryId(jsonArray.getJSONObject(i).getString(AppConstant.BENEFICIARY_ID));
                            pmaySurvey.setBeneficiaryName(jsonArray.getJSONObject(i).getString(AppConstant.BENEFICIARY_NAME));
                            pmaySurvey.setBeneficiaryFatherName(jsonArray.getJSONObject(i).getString(AppConstant.BENEFICIARY_FATHER_NAME));
                            pmaySurvey.setExclusion_criteria_id(jsonArray.getJSONObject(i).getString(AppConstant.EXCLUSION_CRITERIA_ID));
                            pmaySurvey.setHabitationName(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME));
                            pmaySurvey.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));
                            pmaySurvey.setEligible_for_auto_exclusion(jsonArray.getJSONObject(i).getString(AppConstant.ELIGIBLE_FOR_AUTO_EXCLUSION));
                            dbData.insertPMAY(pmaySurvey);
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

}
