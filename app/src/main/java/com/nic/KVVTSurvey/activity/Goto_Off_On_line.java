package com.nic.KVVTSurvey.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.nic.KVVTSurvey.R;
import com.nic.KVVTSurvey.adapter.CommonAdapter;
import com.nic.KVVTSurvey.constant.AppConstant;
import com.nic.KVVTSurvey.dataBase.DBHelper;
import com.nic.KVVTSurvey.dataBase.dbData;
import com.nic.KVVTSurvey.databinding.ActivityGotoOffOnLineBinding;
import com.nic.KVVTSurvey.model.KVVTSurvey;
import com.nic.KVVTSurvey.session.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Goto_Off_On_line extends AppCompatActivity {
    ActivityGotoOffOnLineBinding activityGotoOffOnLineBinding;
    private PrefManager prefManager;
    public com.nic.KVVTSurvey.dataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private List<KVVTSurvey> Village = new ArrayList<>();
    private List<KVVTSurvey> VillageOrdered = new ArrayList<>();

    final ArrayList<Integer> mVillageItems = new ArrayList<>();
    String[] villageStrings;
    boolean[] villageCheckedItems;
    ArrayList<JSONArray> myVillageCodelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGotoOffOnLineBinding = DataBindingUtil.setContentView(this, R.layout.home_screen);
        activityGotoOffOnLineBinding.setActivity(this);

        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        villageFilterSpinner(prefManager.getBlockCode());

        activityGotoOffOnLineBinding.selectVillagesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                villageCheckbox();
            }
        });
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
       /* for (int i = 0; i < Village.size(); i++) {
            myVillageList.add(Village.get(i).getVillageListPvName());
            JSONArray jsonArray = new JSONArray();
            if(prefManager.getLevels().equalsIgnoreCase("S")) {
                jsonArray.put(Village.get(i).getVillageListDistrictCode());
            }
            jsonArray.put(Village.get(i).getVillageListBlockCode());
            jsonArray.put(Village.get(i).getVillageListPvCode());

            myVillageCodelist.add(jsonArray);
        }*/
        villageStrings = Village.toArray(new String[Village.size()]);
        // villageCodeStrings = myVillageCodelist.toArray(new String[myVillageCodelist.size()]);
        villageCheckedItems = new boolean[villageStrings.length];
    }


    public void villageCheckbox() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Goto_Off_On_line.this);
        mBuilder.setTitle(R.string.select_village);
        mBuilder.setMultiChoiceItems(villageStrings, villageCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    if (!mVillageItems.contains(position)) {
                        mVillageItems.add(position);
                    }
                } else if (mVillageItems.contains(position)) {
                    mVillageItems.remove((Integer.valueOf(position)));
                }
                JSONArray villageCodeJsonArray = new JSONArray();

                for (int i = 0; i < mVillageItems.size(); i++) {
                    villageCodeJsonArray.put(myVillageCodelist.get(mVillageItems.get(i)));
                }
                prefManager.setVillagePvCodeJson(villageCodeJsonArray);
                Log.d("villagecode", "" + villageCodeJsonArray);
//                if (isChecked) {
//                    mVillageItems.add(position);
//                } else {
//                    mVillageItems.remove((Integer.valueOf(position)));
//                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mVillageItems.size(); i++) {
                    item = item + villageStrings[mVillageItems.get(i)];
                    if (i != mVillageItems.size() - 1) {
                        item = item + ", ";
                    }
                }

                activityGotoOffOnLineBinding.selectedVillageText.setText(item);
            }
        });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < villageCheckedItems.length; i++) {
                    villageCheckedItems[i] = false;
                    mVillageItems.clear();
                    activityGotoOffOnLineBinding.selectedVillageText.setText("");

                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();


    }


}
