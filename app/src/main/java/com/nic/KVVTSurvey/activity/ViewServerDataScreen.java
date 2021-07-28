package com.nic.KVVTSurvey.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.nic.KVVTSurvey.R;
import com.nic.KVVTSurvey.adapter.CommonAdapter;
import com.nic.KVVTSurvey.adapter.ViewServerDataListAdapter;
import com.nic.KVVTSurvey.api.Api;
import com.nic.KVVTSurvey.api.ServerResponse;
import com.nic.KVVTSurvey.constant.AppConstant;
import com.nic.KVVTSurvey.dataBase.DBHelper;
import com.nic.KVVTSurvey.dataBase.dbData;
import com.nic.KVVTSurvey.databinding.ViewServerDataScreenBinding;
import com.nic.KVVTSurvey.model.KVVTSurvey;
import com.nic.KVVTSurvey.session.PrefManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewServerDataScreen extends AppCompatActivity implements Api.ServerResponseListener {
    public ViewServerDataScreenBinding viewServerDataScreenBinding;
    private ShimmerRecyclerView recyclerView;
    private ViewServerDataListAdapter viewServerDataListAdapter;
    public dbData dbData = new dbData(this);
    private SearchView searchView;
    private PrefManager prefManager;
    String pref_Village;
    private List<KVVTSurvey> Village = new ArrayList<>();
    private List<KVVTSurvey> Habitation = new ArrayList<>();
    private List<KVVTSurvey> VillageOrdered = new ArrayList<>();
    private List<KVVTSurvey> HabitationOrdered = new ArrayList<>();
    ArrayList<KVVTSurvey> savedAllList = new ArrayList<>();

    public static SQLiteDatabase db;
    public static DBHelper dbHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewServerDataScreenBinding = DataBindingUtil.setContentView(this, R.layout.view_server_data_screen);
        viewServerDataScreenBinding.setActivity(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        prefManager = new PrefManager(this);

        setSupportActionBar(viewServerDataScreenBinding.toolbar);
        initRecyclerView();
        viewServerDataScreenBinding.headerLayout.setVisibility(View.VISIBLE);
        viewServerDataScreenBinding.searchLayout.setVisibility(View.GONE);
        viewServerDataScreenBinding.searchImg.setVisibility(View.GONE);
        villageFilterSpinner(prefManager.getBlockCode());
        viewServerDataScreenBinding.villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    pref_Village = Village.get(position).getPvName();
                    prefManager.setVillageListPvName(pref_Village);
                    prefManager.setPvCode(Village.get(position).getPvCode());
                    habitationFilterSpinner(prefManager.getDistrictCode(), prefManager.getBlockCode(), prefManager.getPvCode());
                    viewServerDataScreenBinding.serverDataList.setVisibility(View.GONE);
                    viewServerDataScreenBinding.notFoundTv.setVisibility(View.GONE);
                    viewServerDataScreenBinding.searchImg.setVisibility(View.GONE);
                } else {
                    viewServerDataScreenBinding.searchImg.setVisibility(View.GONE);
                    prefManager.setVillageListPvName("");
                    prefManager.setPvCode("");
                    prefManager.setHabCode("");
                    viewServerDataScreenBinding.habitationSpinner.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewServerDataScreenBinding.habitationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setHabCode(Habitation.get(position).getHabCode());
                    new fetchScheduletask().execute();
                } else {
                    prefManager.setHabCode("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewServerDataScreenBinding.searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewServerDataScreenBinding.headerLayout.setVisibility(View.GONE);
                viewServerDataScreenBinding.searchLayout.setVisibility(View.VISIBLE);
            }
        });
        viewServerDataScreenBinding.searchEt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //do here your stuff f
                    searchAction();
                    return true;
                }
                return false;
            }
        });
    }


    public void villageFilterSpinner(String filterVillage) {
        Cursor VillageList = null;
        VillageList = db.rawQuery("SELECT * FROM " + DBHelper.VILLAGE_TABLE_NAME + " where dcode = " + prefManager.getDistrictCode() + " and bcode = '" + filterVillage + "'", null);

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

                    villageList.setDistictCode(districtCode);
                    villageList.setBlockCode(blockCode);
                    villageList.setPvCode(pvCode);
                    villageList.setPvName(pvname);

                    VillageOrdered.add(villageList);
                    Log.d("spinnersize", "" + Village.size());
                } while (VillageList.moveToNext());
            }
        }
        Collections.sort(VillageOrdered, (lhs, rhs) -> lhs.getPvName().compareTo(rhs.getPvName()));
        KVVTSurvey villageListValue = new KVVTSurvey();
        villageListValue.setPvName("Select Village");
        Village.add(villageListValue);
        for (int i = 0; i < VillageOrdered.size(); i++) {
            KVVTSurvey villageList = new KVVTSurvey();
            String districtCode = VillageOrdered.get(i).getDistictCode();
            String blockCode = VillageOrdered.get(i).getBlockCode();
            String pvCode = VillageOrdered.get(i).getPvCode();
            String pvname = VillageOrdered.get(i).getPvName();

            villageList.setDistictCode(districtCode);
            villageList.setBlockCode(blockCode);
            villageList.setPvCode(pvCode);
            villageList.setPvName(pvname);

            Village.add(villageList);
        }
        viewServerDataScreenBinding.villageSpinner.setAdapter(new CommonAdapter(this, Village, "VillageList"));
    }

    public void habitationFilterSpinner(String dcode, String bcode, String pvcode) {
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

                    habList.setDistictCode(districtCode);
                    habList.setBlockCode(blockCode);
                    habList.setPvCode(pvCode);
                    habList.setHabCode(habCode);
                    habList.setHabitationName(habName);

                    HabitationOrdered.add(habList);
                    Log.d("spinnersize", "" + HabitationOrdered.size());
                } while (HABList.moveToNext());
            }
        }
        Collections.sort(HabitationOrdered, (lhs, rhs) -> lhs.getHabitationName().compareTo(rhs.getHabitationName()));
        KVVTSurvey habitationListValue = new KVVTSurvey();
        habitationListValue.setHabitationName("Select Habitation");
        Habitation.add(habitationListValue);
        for (int i = 0; i < HabitationOrdered.size(); i++) {
            KVVTSurvey habList = new KVVTSurvey();
            String districtCode = HabitationOrdered.get(i).getDistictCode();
            String blockCode = HabitationOrdered.get(i).getBlockCode();
            String pvCode = HabitationOrdered.get(i).getPvCode();
            String habCode = HabitationOrdered.get(i).getHabCode();
            String habName = HabitationOrdered.get(i).getHabitationName();

            habList.setDistictCode(districtCode);
            habList.setBlockCode(blockCode);
            habList.setPvCode(pvCode);
            habList.setHabCode(habCode);
            habList.setHabitationName(habName);

            Habitation.add(habList);
        }
        viewServerDataScreenBinding.habitationSpinner.setAdapter(new CommonAdapter(this, Habitation, "HabitationList"));
    }

    private void initRecyclerView() {
        recyclerView = viewServerDataScreenBinding.serverDataList;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
    }

    public class fetchScheduletask extends AsyncTask<Void, Void,
            ArrayList<KVVTSurvey>> {
        @Override
        protected ArrayList<KVVTSurvey> doInBackground(Void... params) {
            dbData.open();
            savedAllList.clear();
            savedAllList = dbData.getAll_KVVTList(prefManager.getPvCode(), prefManager.getHabCode());
            Log.d("savedList_COUNT", String.valueOf(savedAllList.size()));
            return savedAllList;
        }

        @Override
        protected void onPostExecute(ArrayList<KVVTSurvey> savedList) {
            super.onPostExecute(savedList);

            viewServerDataListAdapter = new ViewServerDataListAdapter(ViewServerDataScreen.this, savedList);
            if (savedList.size() > 0) {
                viewServerDataScreenBinding.searchImg.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                viewServerDataScreenBinding.notFoundTv.setVisibility(View.GONE);
                recyclerView.setAdapter(viewServerDataListAdapter);
                recyclerView.showShimmerAdapter();
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadCards();
                    }
                }, 1000);
            } else {
                viewServerDataScreenBinding.searchImg.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                viewServerDataScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
            }
        }


    }

    private void loadCards() {

        recyclerView.hideShimmerAdapter();

    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                viewServerDataListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                viewServerDataListAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }
*/

    @Override
    public void onBackPressed() {
       /* if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }*/
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void searchAction() {
        hideKeyboard(this);
        if (savedAllList.size() > 0) {
            ArrayList<KVVTSurvey> filteredList = new ArrayList<>();
            if (!viewServerDataScreenBinding.searchEt.getText().toString().equals("") && viewServerDataScreenBinding.searchEt.getText().toString().length() > 0) {
                String text = viewServerDataScreenBinding.searchEt.getText().toString();
                for (int i = 0; i < savedAllList.size(); i++) {
                    if (text.equals(savedAllList.get(i).getBeneficiaryId())) {
                        KVVTSurvey card = new KVVTSurvey();
                        card.setPvCode(savedAllList.get(i).getPvCode());
                        card.setHabCode(savedAllList.get(i).getHabCode());
                        card.setBeneficiaryId(savedAllList.get(i).getBeneficiaryId());
                        card.setBeneficiaryName(savedAllList.get(i).getBeneficiaryName());
                        card.setBeneficiaryFatherName(savedAllList.get(i).getBeneficiaryFatherName());
                        card.setEligible_for_auto_exclusion(savedAllList.get(i).getEligible_for_auto_exclusion());
                        card.setHabitationName(savedAllList.get(i).getHabitationName());
                        card.setExclusion_criteria_id(savedAllList.get(i).getExclusion_criteria_id());
                        card.setPvName(savedAllList.get(i).getPvName());
                        filteredList.add(card);
                    } else {

                    }
                }
                viewServerDataListAdapter = new ViewServerDataListAdapter(ViewServerDataScreen.this, filteredList);
                if (filteredList.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    viewServerDataScreenBinding.notFoundTv.setVisibility(View.GONE);
                    recyclerView.setAdapter(viewServerDataListAdapter);
                    recyclerView.showShimmerAdapter();
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadCards();
                        }
                    }, 1000);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    viewServerDataScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
                }

            }
        }

    }

    public void reloadPreviousList() {
        viewServerDataScreenBinding.headerLayout.setVisibility(View.VISIBLE);
        viewServerDataScreenBinding.searchLayout.setVisibility(View.GONE);
        viewServerDataScreenBinding.searchEt.setText("");
        if (savedAllList.size() > 0) {
            viewServerDataListAdapter = new ViewServerDataListAdapter(ViewServerDataScreen.this, savedAllList);
            if (savedAllList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                viewServerDataScreenBinding.notFoundTv.setVisibility(View.GONE);
                recyclerView.setAdapter(viewServerDataListAdapter);
                recyclerView.showShimmerAdapter();
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadCards();
                    }
                }, 1000);
            } else {
                recyclerView.setVisibility(View.GONE);
                viewServerDataScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
            }

        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
