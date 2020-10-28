package com.myapp.finance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FlowTracker extends AppCompatActivity implements RecyclerBaseAdapter.RecyclerClickListener {

    List<String> years = new ArrayList<>();
    List<String> months = new ArrayList<>();
    List<String> expenses = new ArrayList<>();
    List<String> incomes = new ArrayList<>();
    List<Integer> total_values = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_expense);

        TextView close = findViewById(R.id.quit_recycler);

        Intent in = getIntent();
        String data = in.getStringExtra("Jsondata");
        System.out.println("All_Jsondata:"+data);
        try {
            JSONArray getResponse = new JSONArray(data);
            for(int i=0;i<getResponse.length();i++){
                JSONObject arr = getResponse.getJSONObject(i);
                String year = arr.getString("year");
                String month = arr.getString("month");
                String expense = arr.getString("Total_Expense");
                String income = arr.getString("Total_income");
                int tot_ex = Integer.parseInt(expense);
                int tot_in = Integer.parseInt(income);
                int total_Exp_Inc = tot_in-tot_ex;
                int mon = Integer.parseInt(month);
                String month_in_words = new sql(this).getMonthForInt(mon);
                years.add(year);
                months.add(month_in_words);
                expenses.add(expense);
                incomes.add(income);
                total_values.add(total_Exp_Inc);
            }
        } catch (JSONException e) {
            new sql(this).show("Error",e.toString(),"Ok");
        }


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerBaseAdapter adapter = new RecyclerBaseAdapter(this,years,months,expenses,incomes,total_values);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.recycler_design));
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        close.setOnClickListener((v)-> {
            startActivity(new Intent(FlowTracker.this,Database.class));
                finish();
        });
    }

    public void onBackPressed(){
        startActivity(new Intent(this,Database.class));
        finish();
    }

    @Override
    public void onClickView(int position) {
        years.get(position);
        months.get(position);
    }
}
