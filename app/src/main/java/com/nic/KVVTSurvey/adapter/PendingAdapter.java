package com.nic.KVVTSurvey.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.KVVTSurvey.R;
import com.nic.KVVTSurvey.activity.FullImageActivity;
import com.nic.KVVTSurvey.activity.PendingScreen;
import com.nic.KVVTSurvey.constant.AppConstant;
import com.nic.KVVTSurvey.dataBase.DBHelper;
import com.nic.KVVTSurvey.databinding.PendingAdapterBinding;
import com.nic.KVVTSurvey.model.KVVTSurvey;
import com.nic.KVVTSurvey.session.PrefManager;
import com.nic.KVVTSurvey.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.nic.KVVTSurvey.activity.HomePage.db;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private List<KVVTSurvey> pendingListValues;
    static JSONObject dataset = new JSONObject();

    private LayoutInflater layoutInflater;

    public PendingAdapter(Activity context, List<KVVTSurvey> pendingListValues) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.pendingListValues = pendingListValues;
    }

    @Override
    public PendingAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        PendingAdapterBinding pendingAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.pending_adapter, viewGroup, false);
        return new PendingAdapter.MyViewHolder(pendingAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private PendingAdapterBinding pendingAdapterBinding;

        public MyViewHolder(PendingAdapterBinding Binding) {
            super(Binding.getRoot());
            pendingAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.pendingAdapterBinding.habName.setText(pendingListValues.get(position).getHabitationName());
        holder.pendingAdapterBinding.villageName.setText(pendingListValues.get(position).getPvName());
        holder.pendingAdapterBinding.secId.setText(pendingListValues.get(position).getBeneficiaryId());
        holder.pendingAdapterBinding.name.setText(pendingListValues.get(position).getBeneficiaryName());
        holder.pendingAdapterBinding.beneficiaryNameHeader.setText(pendingListValues.get(position).getBeneficiaryName());
        if(!pendingListValues.get(position).getIsEligible().equalsIgnoreCase("")){
            holder.pendingAdapterBinding.legalView.setVisibility(View.VISIBLE);
            holder.pendingAdapterBinding.beneficiaryMigratedLayout.setVisibility(View.VISIBLE);
            holder.pendingAdapterBinding.beneficiaryMigratedTv.setText(pendingListValues.get(position).getIsEligible());
        }

        if(pendingListValues.get(position).getIsEligible().toString().equals("Y")){
            holder.pendingAdapterBinding.beneficiaryMigratedTv.setText("Yes");
        }else {
            holder.pendingAdapterBinding.beneficiaryMigratedTv.setText("No");
        }

        if(pendingListValues.get(position).getExisting_user().toString().equals("N")){
            holder.pendingAdapterBinding.secId.setText("New User");
        }else {
        }

        String button_text = pendingListValues.get(position).getButtonText();

        String kvvt_id = pendingListValues.get(position).getKvvtId();
       final Cursor image = db.rawQuery("Select * from " + DBHelper.SAVE_KVVT_IMAGES + " where kvvt_id =" + kvvt_id, null);

        if(image.getCount() > 0) {
            holder.pendingAdapterBinding.viewOfflineImages.setVisibility(View.VISIBLE);
        }
        else {
            holder.pendingAdapterBinding.viewOfflineImages.setVisibility(View.GONE);
        }

        holder.pendingAdapterBinding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(button_text.equals("Take Photo")) {
                    if(image.getCount() == 2 ) {
                        uploadPending(position);
                    }
                    else {
                        new AlertDialog.Builder(context)
                                .setTitle("Alert")
                                .setMessage("There's some photos are missing.Please, delete it and enter details once again")
                                .setIcon(R.mipmap.alert)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                }).show();

                    }
                }
                else if(button_text.equals("Save details")){
                    uploadPending(position);
                }
            }
        });

        holder.pendingAdapterBinding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePending(position);
            }
        });



        holder.pendingAdapterBinding.viewOfflineImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewImages(position);
            }
        });


    }


    public void deletePending(int position) {
        String kvvt_id = pendingListValues.get(position).getKvvtId();

        int sdsm = db.delete(DBHelper.SAVE_KVVT_DETAILS, "id = ? ", new String[]{kvvt_id});
        int sdsm1 = db.delete(DBHelper.SAVE_KVVT_IMAGES, "kvvt_id = ? ", new String[]{kvvt_id});
        pendingListValues.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, pendingListValues.size());
        Log.d("sdsm", String.valueOf(sdsm));
    }

    public void viewImages(int position){
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("Key", "Offline");
        intent.putExtra(AppConstant.KVVT_ID, pendingListValues.get(position).getKvvtId());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public void uploadPending(int position) {
        dataset = new JSONObject();
        String dcode = pendingListValues.get(position).getDistictCode();
        String bcode = pendingListValues.get(position).getBlockCode();
        String pvcode = pendingListValues.get(position).getPvCode();
        String habcode = pendingListValues.get(position).getHabCode();
        String exclusion_criteria_id = pendingListValues.get(position).getExclusion_criteria_id();
        String benificiary_id = pendingListValues.get(position).getBeneficiaryId();
        String person_eligible = pendingListValues.get(position).getIsEligible();
        String button_text = pendingListValues.get(position).getButtonText();

        String existing_user = pendingListValues.get(position).getExisting_user();
        String beneficiary_name = pendingListValues.get(position).getBeneficiaryName();
        String father_husband_name = pendingListValues.get(position).getBeneficiaryFatherName();
        String doorno = pendingListValues.get(position).getDoor_no();
        String street_code = pendingListValues.get(position).getStreetCode();
        String community = pendingListValues.get(position).getCommunity_id();
        String father_or_husband_type = pendingListValues.get(position).getFat_hus_status();

        String kvvt_id = pendingListValues.get(position).getKvvtId();
        prefManager.setKeyDeletePosition(position);
        prefManager.setKeyDeleteId(kvvt_id);

        try {
            dataset.put(AppConstant.KEY_SERVICE_ID,AppConstant.KVVT_SOURCE_SAVE);
            dataset.put(AppConstant.PV_CODE, pvcode);
            dataset.put(AppConstant.HAB_CODE, habcode);
            dataset.put(AppConstant.EXISTING_USER, existing_user);
            if(existing_user.equals("N")){
                dataset.put("name_head_household", beneficiary_name);
                dataset.put("father_husband_name", father_husband_name);
                dataset.put("doorno", doorno);
                dataset.put(AppConstant.STREET_CODE, street_code);
                dataset.put("community", community);
                dataset.put("father_or_husband_type", father_or_husband_type);
            }
            dataset.put(AppConstant.EXCLUSION_CRITERIA_ID, exclusion_criteria_id);
            dataset.put("eligible_for_auto_exclusion", person_eligible);
            dataset.put("benificiary_id", benificiary_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray imageArray = new JSONArray();

        if(button_text.equals("Take Photo")){
            String image_sql = "Select * from " + DBHelper.SAVE_KVVT_IMAGES + " where kvvt_id =" + kvvt_id ;
            Log.d("sql", image_sql);
            Cursor image = db.rawQuery(image_sql, null);

            if (image.getCount() > 0) {
                if (image.moveToFirst()) {
                    do {
                        String latitude = image.getString(image.getColumnIndexOrThrow(AppConstant.KEY_LATITUDE));
                        String longitude = image.getString(image.getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE));
                        String images = image.getString(image.getColumnIndexOrThrow(AppConstant.KEY_IMAGE));
                        String type_of_photo = image.getString(image.getColumnIndexOrThrow(AppConstant.TYPE_OF_PHOTO));

                        JSONObject imageJson = new JSONObject();

                        try {
                            imageJson.put(AppConstant.TYPE_OF_PHOTO,type_of_photo);
                            imageJson.put(AppConstant.KEY_LATITUDE,latitude);
                            imageJson.put(AppConstant.KEY_LONGITUDE,longitude);
                            imageJson.put(AppConstant.KEY_IMAGE,images.trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        imageArray.put(imageJson);

                    } while (image.moveToNext());
                }
            }
        }

        try {
            dataset.put("images", imageArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Utils.isOnline()) {
            ((PendingScreen)context).saveKVVTImagesJsonParams(dataset);
        } else {
            Utils.showAlert(context, "Turn On Mobile Data To Upload");
        }

    }

    @Override
    public int getItemCount() {
        return pendingListValues.size();
    }


}
