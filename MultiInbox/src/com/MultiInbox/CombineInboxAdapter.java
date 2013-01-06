package com.MultiInbox;

import java.util.ArrayList;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CombineInboxAdapter extends BaseAdapter{

    private ArrayList<CombineInbox> mCombineInboxList;
    private LayoutInflater mInflater;
    
    public CombineInboxAdapter(Context context , ArrayList<CombineInbox> combineInboxList) {
        // TODO Auto-generated constructor stub
        this.mCombineInboxList = combineInboxList;
        mInflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mCombineInboxList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mCombineInboxList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.inbox_item,null);
            holder = new ViewHolder();
            holder.textTitle = (TextView)convertView.findViewById(R.id.txt_subject);
            holder.textDescription = (TextView)convertView.findViewById(R.id.txt_message);
            holder.textType = (TextView)convertView.findViewById(R.id.txt_type);
            
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }
        
        holder.textTitle.setText(mCombineInboxList.get(position).getTitle());
        holder.textDescription.setText(mCombineInboxList.get(position).getDescription());
        holder.textType.setText(mCombineInboxList.get(position).getType().toString());
        
        return convertView;
    }
    
    class ViewHolder{
        TextView textTitle,textDescription,textType;
    }

}
