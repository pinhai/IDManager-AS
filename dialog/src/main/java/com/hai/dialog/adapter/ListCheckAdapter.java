package com.hai.dialog.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.hai.dialog.R;

import java.util.ArrayList;
import java.util.List;

public class ListCheckAdapter<T> extends BaseAdapter {

    private Context context;
    private List<T> data;
    private List<Boolean> mCheckItems;
    private OnItemCheckListener listener;

    public ListCheckAdapter(Context context, List<T> data, OnItemCheckListener listener){
        this(context, data, null, listener);
    }

    public ListCheckAdapter(Context context, List<T> data, List<Boolean> checkItems, OnItemCheckListener listener){

        this.context = context;
        this.data = data;
        this.listener = listener;
        if(checkItems == null || checkItems.size() < data.size()){
            mCheckItems = new ArrayList<>();
            for(int i=0; i<data.size(); i++){
                mCheckItems.add(false);
            }
        }else {
            this.mCheckItems = checkItems;
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_check, null);
            holder.cb_item = convertView.findViewById(R.id.tv_item);
            holder.cl_itemCheck = convertView.findViewById(R.id.cl_itemCheck);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final T item = data.get(position);
        holder.cb_item.setText(listener.getText(item));
        holder.cb_item.setChecked(mCheckItems.get(position));
        holder.cl_itemCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cb_item.setChecked(!holder.cb_item.isChecked());
                boolean check = listener.onItemCheck(item, position, holder.cb_item.isChecked());
                holder.cb_item.setChecked(check);
                mCheckItems.set(position, check);
            }
        });

        return convertView;
    }

    class ViewHolder {
        ConstraintLayout cl_itemCheck;
        CheckBox cb_item;
    }

    public interface OnItemCheckListener<T>{
        String getText(T item);
        //返回值将决定checkbox是否会被checked
        boolean onItemCheck(T city, int position, boolean isChecked);
    }
}
