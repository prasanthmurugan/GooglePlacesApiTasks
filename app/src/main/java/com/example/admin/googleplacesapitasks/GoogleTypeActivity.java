package com.example.admin.googleplacesapitasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowId;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Admin on 2/3/2016.
 */
public class GoogleTypeActivity extends Activity {
    RecyclerView recyclerView;
    ArrayList<String> arrayList=new ArrayList<>();
    ArrayList<CheckModel> modelArrayList=new ArrayList<>();
    TypeFilterAdapter typeFilterAdapter;
    Button btnSelectAll,btnDone;
    boolean alreadySelected,unSelect=false;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.type_list_layout);
        init();
        setUpDefaults();
        setUpEvents();
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        btnSelectAll = (Button)findViewById(R.id.select_all);
        btnDone = (Button)findViewById(R.id.done);
    }

    private void setUpDefaults() {
        if (getIntent().hasExtra("alreadySelected")) {
            alreadySelected=getIntent().getExtras().getBoolean("alreadySelected");
            if (alreadySelected)
            {
                modelArrayList=(ArrayList<CheckModel>)getIntent().getSerializableExtra("model");
                for (int i=0;i<modelArrayList.size();i++)
                {
                 Log.e("came->","old modelList-> "+""+modelArrayList.get(i).getTypes()+"- "+modelArrayList.get(i).isSelected());
                    if (modelArrayList.get(i).isSelected){
                        count++;
                    }
                }
                Log.e("count",""+count);
                if (count==modelArrayList.size()){
                    btnSelectAll.setText(getResources().getString(R.string.unselect_all));
                }
//                if (allSelected){
//                    btnSelectAll.setText(getResources().getString(R.string.unselect_all));
//                }
            }
        }
        else {
            Log.e("came->","newCreation");
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_banks), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_fire_brigade), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_fuels), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_grocery), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_hospital), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_library), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_parks), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_police), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_post_office), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_railway_station), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_restaurant), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_xhopping), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_university), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_transports), false));
            modelArrayList.add(new CheckModel(getResources().getString(R.string.type_schools), false));
        }

        typeFilterAdapter=new TypeFilterAdapter(this,modelArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        typeFilterAdapter.setModelList((ArrayList<CheckModel>)getIntent().getSerializableExtra("model"));
        recyclerView.setAdapter(typeFilterAdapter);

    }

    private void setUpEvents() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelArrayList=typeFilterAdapter.getModelList();
                Intent intent=new Intent();
                intent.putExtra("model",modelArrayList);
                Log.e("Came->", "btn-onclick");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSelectAll.getText().equals(getResources().getString(R.string.select_all)))
                {
                    btnSelectAll.setText(getResources().getString(R.string.unselect_all));
                    typeFilterAdapter.selectAll(true);
                    Log.e("came->","selected");

                }else{
                    alreadySelected = false;
                    btnSelectAll.setText(getResources().getString(R.string.select_all));
                    typeFilterAdapter.unSelectAll(true);
                    Log.e("came->","Unselected");
                }
            }
        });
    }
}
