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
        if(!pendingListValues.get(position).getPersonAlive().equalsIgnoreCase("")){
            holder.pendingAdapterBinding.aliveLayout.setVisibility(View.VISIBLE);
            holder.pendingAdapterBinding.aliveView.setVisibility(View.VISIBLE);
            holder.pendingAdapterBinding.beneficiaryAliveTv.setText(pendingListValues.get(position).getPersonAlive());
        }
        if(!pendingListValues.get(position).getIsLegel().equalsIgnoreCase("")){
            holder.pendingAdapterBinding.legalHeirLayout.setVisibility(View.VISIBLE);
            holder.pendingAdapterBinding.legalView.setVisibility(View.GONE);
            holder.pendingAdapterBinding.legalHeirTv.setText(pendingListValues.get(position).getIsLegel());
        }
        if(!pendingListValues.get(position).getIsEligible().equalsIgnoreCase("")){
            holder.pendingAdapterBinding.legalView.setVisibility(View.VISIBLE);
            holder.pendingAdapterBinding.beneficiaryMigratedLayout.setVisibility(View.VISIBLE);
            holder.pendingAdapterBinding.beneficiaryMigratedTv.setText(pendingListValues.get(position).getIsEligible());
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
        String beneficiary_name = pendingListValues.get(position).getBeneficiaryName();
        String father_name = pendingListValues.get(position).getFatherName();
        String benificiary_id = pendingListValues.get(position).getBeneficiaryId();
        String person_alive = pendingListValues.get(position).getPersonAlive();
        String legal_heir_available = pendingListValues.get(position).getIsLegel();
        String person_eligible = pendingListValues.get(position).getIsEligible();
        String button_text = pendingListValues.get(position).getButtonText();

        String kvvt_id = pendingListValues.get(position).getKvvtId();
        prefManager.setKeyDeletePosition(position);
        prefManager.setKeyDeleteId(kvvt_id);

        try {
            dataset.put(AppConstant.KEY_SERVICE_ID,AppConstant.KVVT_SOURCE_SAVE);
            dataset.put(AppConstant.PV_CODE, pvcode);
            dataset.put(AppConstant.HAB_CODE, habcode);
            /*dataset.put(AppConstant.BENEFICIARY_NAME, beneficiary_name);
            dataset.put(AppConstant.BENEFICIARY_FATHER_NAME, father_name);
            dataset.put(AppConstant.PERSON_ALIVE, person_alive);
            dataset.put(AppConstant.LEGAL_HEIR_AVAILABLE, legal_heir_available);*/
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
