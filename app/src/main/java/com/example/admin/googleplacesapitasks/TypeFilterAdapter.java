package com.example.admin.googleplacesapitasks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

/**
 * Created by Admin on 2/3/2016.
 */
public class TypeFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
//    ArrayList<String> arrayList;
    boolean isSelected = false,unSelectAll=false;
    ArrayList<CheckModel> modelList = new ArrayList<>();
    public TypeFilterAdapter(Context context,ArrayList<CheckModel> arrayList)
    {
        this.mContext = context;
        this.modelList = arrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.type_list_item, parent,false);
        TypeViewHolder viewHolder = new TypeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CheckModel checkModel = modelList.get(position);
        Log.e("Came->adapter ", "" + checkModel.getTypes() + " " + checkModel.isSelected());
        TypeViewHolder viewHolder = (TypeViewHolder)holder;
        viewHolder.checkBox.setText(checkModel.getTypes());
//        if (checkModel.isSelected())
//        {
//            viewHolder.checkBox.toggle();
//        }
        viewHolder.checkBox.setChecked(checkModel.isSelected());
        if (isSelected){
            viewHolder.checkBox.setChecked(true);
            checkModel.setIsSelected(true);
//            isSelected=false;
//            Log.e("came->adapter", "selected");
        }
        if (unSelectAll){
            viewHolder.checkBox.setChecked(false);
            checkModel.setIsSelected(false);
//            unSelectAll=false;
//            Log.e("came->adapter", "Unselected");
        }

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkModel.setIsSelected(isChecked);
            }
        });
//        Log.e("modelSize:",""+modelList.size());
//        while(modelList.size()!=0)
//        {
//            viewHolder.checkBox.setChecked(modelList.get(position).isSelected);
//        }
//        modelList.add(checkModel);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }



    class TypeViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        public TypeViewHolder(View itemView){
            super(itemView);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkbox);
        }
    }

    public void selectAll(boolean isSelected){
      this.isSelected = isSelected;
        unSelectAll=false;
//      modelList.clear();
      notifyDataSetChanged();
    }
    public void unSelectAll(boolean b) {
        this.unSelectAll=b;
        isSelected=false;
        notifyDataSetChanged();
    }
    public ArrayList<CheckModel> getModelList()
    {
        return modelList;
    }
    public void setModelList(ArrayList<CheckModel> modelList){
        this.modelList = modelList;
        notifyDataSetChanged();
    }
}
