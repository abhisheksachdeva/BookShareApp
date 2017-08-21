package com.sdsmdg.bookshareapp.BSA.ui.adapter.Local;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.ui.College;

import java.util.ArrayList;

/**
 * Created by Harshit Bansal on 5/16/2017.
 */

public class CollegeAdapter extends BaseAdapter {
    Context context;
    ArrayList<College> collegeArrayList;
    LayoutInflater inflater;

    public CollegeAdapter(Context context, ArrayList<College> collegeArrayList)
    {
        this.context = context;
        this.collegeArrayList = collegeArrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return collegeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return collegeArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        view = inflater.inflate(R.layout.college_layout , parent ,false);
        TextView name = (TextView) view.findViewById(R.id.text_college);
        TextView domain = (TextView) view.findViewById(R.id.text_domain);
        name.setText(collegeArrayList.get(position).getCollegeName());
        domain.setText(collegeArrayList.get(position).getCollegeDomain());
        return view;
    }
}

