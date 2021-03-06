package com.nic.KVVTSurvey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nic.KVVTSurvey.R;
import com.nic.KVVTSurvey.model.KVVTSurvey;

import java.util.List;

/**
 * Created by shanmugapriyan on 25/05/16.
 */
public class CommonAdapter extends BaseAdapter {
    private List<KVVTSurvey> pmgsySurveys;
    private Context mContext;
    private String type;


    public CommonAdapter(Context mContext, List<KVVTSurvey> pmgsySurvey, String type) {
        this.pmgsySurveys = pmgsySurvey;
        this.mContext = mContext;
        this.type = type;
    }


    public int getCount() {
        return pmgsySurveys.size();
    }


    public Object getItem(int position) {
        return position;
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.spinner_drop_down_item, parent, false);
//        TextView tv_type = (TextView) view.findViewById(R.id.tv_spinner_item);
        View view = inflater.inflate(R.layout.spinner_value, parent, false);
        TextView tv_type = (TextView) view.findViewById(R.id.spinner_list_value);
        KVVTSurvey pmgsySurvey = pmgsySurveys.get(position);

        if (type.equalsIgnoreCase("BlockList")) {
            tv_type.setText(pmgsySurvey.getBlockName());
        } else if (type.equalsIgnoreCase("VillageList")) {
            tv_type.setText(pmgsySurvey.getPvNameTa());
        } else if (type.equalsIgnoreCase("HabitationList")) {
            tv_type.setText(pmgsySurvey.getHabitationNameTa());
        }else if (type.equalsIgnoreCase("SchemeList")) {
            tv_type.setText(pmgsySurvey.getExclusion_criteria_ta());
        }else if (type.equalsIgnoreCase("StreetList")) {
            tv_type.setText(pmgsySurvey.getStreetNameTa());
        }else if (type.equalsIgnoreCase("CommunityList")) {
            tv_type.setText(pmgsySurvey.getCommunity_name());
        }
        return view;
    }

}