package com.nic.KVVTSurvey.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.nic.KVVTSurvey.R;
import com.nic.KVVTSurvey.activity.FullImageActivity;
import com.nic.KVVTSurvey.constant.AppConstant;
import com.nic.KVVTSurvey.databinding.ViewServerDataAdapterBinding;
import com.nic.KVVTSurvey.model.KVVTSurvey;
import com.nic.KVVTSurvey.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ViewServerDataListAdapter extends RecyclerView.Adapter<ViewServerDataListAdapter.MyViewHolder> implements Filterable {
    private List<KVVTSurvey> serverDataListValues;
    private List<KVVTSurvey> serverDataListValuesFiltered;
    private String letter;
    private Context context;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    private LayoutInflater layoutInflater;

    public ViewServerDataListAdapter(Context context, List<KVVTSurvey> serverDataListValues) {
        this.context = context;
        this.serverDataListValues = serverDataListValues;
        this.serverDataListValuesFiltered = serverDataListValues;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ViewServerDataAdapterBinding viewServerDataAdapterBinding;

        public MyViewHolder(ViewServerDataAdapterBinding Binding) {
            super(Binding.getRoot());
            viewServerDataAdapterBinding = Binding;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        ViewServerDataAdapterBinding viewServerDataAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.view_server_data_adapter, viewGroup, false);
        return new MyViewHolder(viewServerDataAdapterBinding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.viewServerDataAdapterBinding.name.setText(serverDataListValuesFiltered.get(position).getBeneficiaryName());
        holder.viewServerDataAdapterBinding.fatherName.setText(serverDataListValuesFiltered.get(position).getBeneficiaryFatherName());
        holder.viewServerDataAdapterBinding.villageName.setText(serverDataListValuesFiltered.get(position).getPvNameTa());
        holder.viewServerDataAdapterBinding.habName.setText(serverDataListValuesFiltered.get(position).getHabitationNameTa());
        holder.viewServerDataAdapterBinding.secId.setText(serverDataListValuesFiltered.get(position).getBeneficiaryId());
//        holder.viewServerDataAdapterBinding.eligibleId.setText(serverDataListValuesFiltered.get(position).getEligible_for_auto_exclusion());

        /*if(serverDataListValuesFiltered.get(position).getEligible_for_auto_exclusion().toString().equals("Y")){
            *//*holder.viewServerDataAdapterBinding.eligibleLayout.setVisibility(View.VISIBLE);
            holder.viewServerDataAdapterBinding.legalView1.setVisibility(View.VISIBLE);
            //holder.viewServerDataAdapterBinding.eligibleId.setText(context.getResources().getString(R.string.yes));
            holder.viewServerDataAdapterBinding.eligibleId.setText(serverDataListValuesFiltered.get(position).getExclusion_criteria_ta());
            *//*
            holder.viewServerDataAdapterBinding.eligibleLayout.setVisibility(View.GONE);
            holder.viewServerDataAdapterBinding.legalView1.setVisibility(View.GONE);
            //holder.viewServerDataAdapterBinding.eligibleId.setText(context.getResources().getString(R.string.no));
            holder.viewServerDataAdapterBinding.eligibleId.setText("-");

        }
        else {
            if(serverDataListValuesFiltered.get(position).getExclusion_criteria_ta()!=null&&
                    !serverDataListValuesFiltered.get(position).getExclusion_criteria_ta().equals("")){
*//*holder.viewServerDataAdapterBinding.eligibleLayout.setVisibility(View.GONE);
            holder.viewServerDataAdapterBinding.legalView1.setVisibility(View.GONE);
            //holder.viewServerDataAdapterBinding.eligibleId.setText(context.getResources().getString(R.string.no));
            holder.viewServerDataAdapterBinding.eligibleId.setText("-");
            *//*
                holder.viewServerDataAdapterBinding.eligibleLayout.setVisibility(View.VISIBLE);
                holder.viewServerDataAdapterBinding.legalView1.setVisibility(View.VISIBLE);
                //holder.viewServerDataAdapterBinding.eligibleId.setText(context.getResources().getString(R.string.yes));
                holder.viewServerDataAdapterBinding.eligibleId.setText(serverDataListValuesFiltered.get(position).getExclusion_criteria_ta());

            }
            else {
*//*holder.viewServerDataAdapterBinding.eligibleLayout.setVisibility(View.GONE);
            holder.viewServerDataAdapterBinding.legalView1.setVisibility(View.GONE);
            //holder.viewServerDataAdapterBinding.eligibleId.setText(context.getResources().getString(R.string.no));
            holder.viewServerDataAdapterBinding.eligibleId.setText("-");
            *//*
                holder.viewServerDataAdapterBinding.eligibleLayout.setVisibility(View.GONE);
                holder.viewServerDataAdapterBinding.legalView1.setVisibility(View.GONE);
                //holder.viewServerDataAdapterBinding.eligibleId.setText(context.getResources().getString(R.string.yes));
                holder.viewServerDataAdapterBinding.eligibleId.setText(serverDataListValuesFiltered.get(position).getExclusion_criteria_ta());

            }

        }
*/


            if(serverDataListValuesFiltered.get(position).getExclusion_criteria_ta()!=null&&
                    !serverDataListValuesFiltered.get(position).getExclusion_criteria_ta().equals("")
            &&!serverDataListValuesFiltered.get(position).getExclusion_criteria_ta().equals("null")){
/*holder.viewServerDataAdapterBinding.eligibleLayout.setVisibility(View.GONE);
            holder.viewServerDataAdapterBinding.legalView1.setVisibility(View.GONE);
            //holder.viewServerDataAdapterBinding.eligibleId.setText(context.getResources().getString(R.string.no));
            holder.viewServerDataAdapterBinding.eligibleId.setText("-");
            */
                holder.viewServerDataAdapterBinding.eligibleLayout.setVisibility(View.VISIBLE);
                holder.viewServerDataAdapterBinding.legalView1.setVisibility(View.VISIBLE);
                //holder.viewServerDataAdapterBinding.eligibleId.setText(context.getResources().getString(R.string.yes));
                holder.viewServerDataAdapterBinding.eligibleId.setText(serverDataListValuesFiltered.get(position).getExclusion_criteria_ta());

            }
            else {
/*holder.viewServerDataAdapterBinding.eligibleLayout.setVisibility(View.GONE);
            holder.viewServerDataAdapterBinding.legalView1.setVisibility(View.GONE);
            //holder.viewServerDataAdapterBinding.eligibleId.setText(context.getResources().getString(R.string.no));
            holder.viewServerDataAdapterBinding.eligibleId.setText("-");
            */
                holder.viewServerDataAdapterBinding.eligibleLayout.setVisibility(View.GONE);
                holder.viewServerDataAdapterBinding.legalView1.setVisibility(View.GONE);
                //holder.viewServerDataAdapterBinding.eligibleId.setText(context.getResources().getString(R.string.yes));
                holder.viewServerDataAdapterBinding.eligibleId.setText(serverDataListValuesFiltered.get(position).getExclusion_criteria_ta());

            }



        if(serverDataListValuesFiltered.get(position).getPatta_available_status()!=null && !serverDataListValuesFiltered.get(position).getPatta_available_status().equals("")){
            if(serverDataListValuesFiltered.get(position).getPatta_available_status().equals("Y")){
                holder.viewServerDataAdapterBinding.beneficiaryPattaTv.setText(context.getResources().getString(R.string.available));
            }
            else {
                holder.viewServerDataAdapterBinding.beneficiaryPattaTv.setText(context.getResources().getString(R.string.no));
            }
        }
        else {
            holder.viewServerDataAdapterBinding.beneficiaryPattaTv.setText("-");
        }
        if(serverDataListValuesFiltered.get(position).getIs_awaas_plus_list()!=null && !serverDataListValuesFiltered.get(position).getIs_awaas_plus_list().equals("")){
            if(serverDataListValuesFiltered.get(position).getIs_awaas_plus_list().equals("Y")){
                holder.viewServerDataAdapterBinding.beneficiaryAwaasTv.setText(context.getResources().getString(R.string.yes));
            }
            else {
                holder.viewServerDataAdapterBinding.beneficiaryAwaasTv.setText(context.getResources().getString(R.string.no));
            }

        }
        else {
            holder.viewServerDataAdapterBinding.beneficiaryAwaasTv.setText("-");
        }

        if(serverDataListValuesFiltered.get(position).getIS_DOCUMENT_AVAILABLE()!=null && !serverDataListValuesFiltered.get(position).getIS_DOCUMENT_AVAILABLE().equals("")){
            if(serverDataListValuesFiltered.get(position).getIS_DOCUMENT_AVAILABLE().equals("Y")){
                holder.viewServerDataAdapterBinding.documentStatusTv.setText(context.getResources().getString(R.string.available));
            }
            else {
                holder.viewServerDataAdapterBinding.documentStatusTv.setText(context.getResources().getString(R.string.no));
            }

        }
        else {
            holder.viewServerDataAdapterBinding.documentStatusTv.setText("-");
        }
        if(serverDataListValuesFiltered.get(position).getIS_NATHAM_LAND_AVAILABLE()!=null && !serverDataListValuesFiltered.get(position).getIS_NATHAM_LAND_AVAILABLE().equals("")){
            if(serverDataListValuesFiltered.get(position).getIS_NATHAM_LAND_AVAILABLE().equals("Y")){
                holder.viewServerDataAdapterBinding.nathamLandStatusTv.setText(context.getResources().getString(R.string.yes));
            }
            else {
                holder.viewServerDataAdapterBinding.nathamLandStatusTv.setText(context.getResources().getString(R.string.no));
            }

        }
        else {
            holder.viewServerDataAdapterBinding.nathamLandStatusTv.setText("-");
        }


        if(serverDataListValuesFiltered.get(position).getPhoto_availavle().toString().equals("Y")){
            holder.viewServerDataAdapterBinding.viewServerImages.setVisibility(View.VISIBLE);
        }else {
            holder.viewServerDataAdapterBinding.viewServerImages.setVisibility(View.GONE);
        }

        holder.viewServerDataAdapterBinding.viewServerImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isOnline()) {
                    viewServerImages(position);
                } else {
                    Activity activity = (Activity) context;
                    Utils.showAlert(activity, activity.getResources().getString(R.string.no_internet));
                }
            }
        });
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    serverDataListValuesFiltered = serverDataListValues;
                } else {
                    List<KVVTSurvey> filteredList = new ArrayList<>();
                    for (KVVTSurvey row : serverDataListValues) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getBeneficiaryId().toLowerCase().contains(charString.toLowerCase()) || row.getBeneficiaryName().contains(charString.toUpperCase())) {
                            filteredList.add(row);
                        }
                    }

                    serverDataListValuesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = serverDataListValuesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                serverDataListValuesFiltered = (ArrayList<KVVTSurvey>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void viewServerImages(int pos) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(activity, FullImageActivity.class);
        intent.putExtra("Key", "Online");
        intent.putExtra(AppConstant.PV_CODE, serverDataListValuesFiltered.get(pos).getPvCode());
        intent.putExtra(AppConstant.HAB_CODE, serverDataListValuesFiltered.get(pos).getHabCode());
        intent.putExtra(AppConstant.KVVT_ID, serverDataListValuesFiltered.get(pos).getBeneficiaryId());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public int getItemCount() {
        return serverDataListValuesFiltered == null ? 0 : serverDataListValuesFiltered.size();
    }
}
