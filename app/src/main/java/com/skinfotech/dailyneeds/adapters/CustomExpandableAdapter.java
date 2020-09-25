package com.skinfotech.dailyneeds.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.models.MenuModel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<MenuModel> mHeaderList;
    private HashMap<MenuModel, List<MenuModel>> mSideNavigationItemMap;

    public CustomExpandableAdapter(Context context, List<MenuModel> mHeaderList, HashMap<MenuModel, List<MenuModel>> listChildData) {
        this.mContext = context;
        this.mHeaderList = mHeaderList;
        this.mSideNavigationItemMap = listChildData;
    }

    @Override
    public MenuModel getChild(int groupPosition, int childPosition) {
        return this.mSideNavigationItemMap.get(this.mHeaderList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = getChild(groupPosition, childPosition).menuName;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.second_row_item, null);
        }
        TextView txtListChild = convertView.findViewById(R.id.listTitle);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (this.mSideNavigationItemMap.get(this.mHeaderList.get(groupPosition)) == null) {
            return 0;
        } else {
            return this.mSideNavigationItemMap.get(this.mHeaderList.get(groupPosition)).size();
        }
    }

    @Override
    public MenuModel getGroup(int groupPosition) {
        return this.mHeaderList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mHeaderList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition).menuName;
        String imgStr = getGroup(groupPosition).imgStr;
        int imgArrow = getGroup(groupPosition).imgArrow;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.first_row_item, null);
        }
        TextView lblListHeader = convertView.findViewById(R.id.expandable_list_item);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        ImageView imgViewHeader = (ImageView) convertView.findViewById(R.id.image_icon);
        if (Utility.isNotEmpty(imgStr)) {
            Picasso.get().load(imgStr).placeholder(R.drawable.default_image).into(imgViewHeader);
        }
        ImageView imgExpandView = (ImageView) convertView.findViewById(R.id.arrow);
        imgExpandView.setImageResource(imgArrow);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
