package com.myapp.finance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FlowTracker extends AppCompatActivity implements RecyclerBaseAdapter.RecyclerClickListener {

    List<String> years = new ArrayList<>();
    List<String> months = new ArrayList<>();
    List<Integer> total_values = new ArrayList<>();
    ProgressBar progressBar;
    TextView loadingData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        TextView close = findViewById(R.id.quit_recycler);
        progressBar = findViewById(R.id.loadingData);
        loadingData = findViewById(R.id.loadingTextView);
        List<JSONObject> list = new ArrayList<>();
        sql s = new sql(FlowTracker.this);

        Intent in = getIntent();
        String data = in.getStringExtra("Jsondata");
        System.out.println("All_Json data:"+data);

        try{
            JSONArray getResponse = new JSONArray(data);
            for(int i=0;i<getResponse.length();i++){
                list.add(getResponse.getJSONObject(i));
            }
        }
        catch (Exception e){e.printStackTrace();}

        System.out.println("List of data ois "+list);

        s.deleteAllDataInTable();
        for (JSONObject jobj:list){
            String exp, date = null;
            try {
                date = jobj.getString("monthAndYear");
                exp = jobj.getString("ExpenseAmt");
                s.insertData(date, exp, null);
            } catch (JSONException e) {
                try {
                    String inc = jobj.getString("IncomeAmt");
                    s.insertData(date, null, inc);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        Map<String, List<String>> map = s.getDatas();
        List<String> date = map.get("allDates");
        for (String dates:date){
            String[] arr = dates.split("-");
            years.add(arr[1]);
            months.add(arr[0]);
        }
        List<String> totalExp = map.get("allExpenses");
        List<String> totalInc = map.get("allIncomes");
        for (int j = 0;j<totalExp.size();j++){
            String exp = totalExp.get(j);
            String inc = totalInc.get(j);
            int total = Integer.parseInt(inc) - Integer.parseInt(exp);
            total_values.add(total);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerBaseAdapter adapter = new RecyclerBaseAdapter(this,years,months,totalExp,totalInc,total_values);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.GONE);
        loadingData.setVisibility(View.GONE);

        close.setOnClickListener((v)-> {
            exitActivity();
        });
    }

    @Override
    public void onBackPressed() {
        exitActivity();
    }

    @Override
    public void onClickView(int position) {
        years.get(position);
        months.get(position);
    }

    private void exitActivity(){
        Intent intent = new Intent(FlowTracker.this,Database.class);
        startActivity(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }
}
