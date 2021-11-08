package com.myapp.finance;

import android.content.Intent;
import android.os.Bundle;
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
    List<String> expenses = new ArrayList<>();
    List<String> incomes = new ArrayList<>();
    List<Integer> total_values = new ArrayList<>();
    String date1 = null, exp1 = null, exp2 = null, date2 = null, inc1 = null, inc2 = null;
    int totalExpenses = 0 , totalIncome = 0;
    List<String> listOfDates = new ArrayList<>();
    List<Integer> listOfTotalExpenses = new ArrayList<>();
    List<Integer> listOfTotalIncomes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_expense);

        TextView close = findViewById(R.id.quit_recycler);
        List<JSONObject> list = new ArrayList<>();

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

        System.out.println("List of values are: "+list);

        Iterator<String> l = list.get(0).keys();
        int totalExpense = 0, totalIncome = 0;
        List<Integer> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();
        List<String> datessss = new ArrayList<>();

        for (int j=0;j<list.size();j++){
            try {
                String keys = list.get(j).keys().next();
                String date = list.get(j).getString("monthAndYear");
                String date1 = date;
                if(keys.equals("ExpenseAmt")){
                    exp1 = list.get(j).getString("ExpenseAmt");
                    exp1 = exp2;
                    if(j!=0 && date.compareTo(date1)==0)
                        totalExpense = totalExpense+Integer.parseInt(exp2);
                    else{
                        l1.add(totalExpense);
                        datessss.add(date);
                    }

                }else if(keys.equals("IncomeAmt")){
                    inc1 = list.get(j).getString("IncomeAmt");
                    inc1 = inc2;
                    if(j!=0 && date.compareTo(date1)==0){
                        totalIncome = totalIncome+Integer.parseInt(inc2);
                    }else {
                        l2.add(totalIncome);
                        datessss.add(date);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        System.out.println("List of DAtas are "+datessss+" Exp "+l1+" Inc :"+l2);

        /*int i=0;
        while (i<list.size()-1){
            try{
                date1 = list.get(i).getString("monthAndYear");

                try {
                    exp1 = list.get(i).getString("ExpenseAmt");
                }
                catch (Exception e){
                    inc1 = list.get(i).getString("IncomeAmt");
                    new sql(this).show("Error", e.toString(), "Ok");
                }

                i++;
                try {
                    date2 = list.get(i).getString("monthAndYear");
                    exp2 = list.get(i).getString("ExpenseAmt");
                }
                catch (Exception e){
                    inc2 = list.get(i).getString("IncomeAmt");
                    new sql(this).show("Exp 1 Error", e.toString(), "Ok");
                }

                if(date1.compareTo(date2)==0){
                    try {
                        int expense1 = Integer.parseInt(exp1);
                        int expense2 = Integer.parseInt(exp2);
                        totalExpenses = totalExpenses + expense1 + expense2;
                    }
                    catch (Exception e1){ *//*totalIncome = totalIncome + Integer.parseInt(inc1) + Integer.parseInt(inc2);*//*
                        Toast.makeText(FlowTracker.this, e1.getMessage(), Toast.LENGTH_LONG).show();
                    *//*break;*//*}
                }
                else {
                    listOfDates.add(date1);
                    listOfTotalExpenses.add(totalExpenses);
                    listOfTotalIncomes.add(totalIncome);
                }
            }
            catch (Exception e){ new sql(this).show("Error", e.toString(),"Done");}
        }

        System.out.println("Total Expenses and incomes are "+listOfDates+" "+listOfTotalExpenses+" "+listOfTotalIncomes);*/

        /*try {
            JSONArray getResponse = new JSONArray(data);
            for(i=0;i<getResponse.length();i++){
                JSONObject arr = getResponse.getJSONObject(i);
                String monthAndYear = arr.getString("monthAndYear");
                String[] arrs = monthAndYear.split("-");
                String year = arrs[1];
                String month = arrs[0];
                String IncomeAmt = null, ExpenseAmt = null;
                try {
                    IncomeAmt = arr.getString("IncomeAmt");
                }
                catch (Exception e){

                }
                try {
                    ExpenseAmt = arr.getString("ExpenseAmt");
                }
                catch (Exception e){

                }
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));*/

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
