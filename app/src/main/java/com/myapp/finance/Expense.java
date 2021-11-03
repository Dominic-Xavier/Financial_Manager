package com.myapp.finance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Expense extends Fragment {

    Context context;

    View v;

    private List<String> Date = new ArrayList<String>();
    private List<String> Expense = new ArrayList<String>();
    private List<String> Amount = new ArrayList<String>();


    public Expense(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_expense, container, false);

        TableLayout expense = v.findViewById(R.id.total_expense);
        context = container.getContext();

            Date = getArguments().getStringArrayList("date");
            Expense = getArguments().getStringArrayList("Exp_Des");
            Amount = getArguments().getStringArrayList("Exp_Amt");

        System.out.println("Datas of expense fragment:"+Expense);

        TextView total_amount = new TextView(context);

        String date="",Des="";
        int Amot,total_values = 0;
        if(Date!=null && Expense!=null ||  Amount!=null){
            for (int k = 0; k < Expense.size(); k++) {
                TableRow row = new TableRow(context);
                date = Date.get(k);
                Des = Expense.get(k);
                Amot = Integer.parseInt(Amount.get(k).trim());
                if(!Des.equals("-")){
                    String col_name[] = {date, Des, Amot + ""};
                    for (String i : col_name) {
                        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));
                        TextView tv = new TextView(context);
                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.CENTER);
                        tv.setText(i);
                        tv.setTextSize(15);
                        tv.setPadding(5, 5, 5, 5);
                        row.addView(tv);
                    }
                }
                //Counting Total values
                expense.addView(row);
                total_values +=Amot;
            }
            total_amount.setText("Total:"+total_values);
            total_amount.setGravity(Gravity.RIGHT);
            total_amount.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            total_amount.setLayoutParams(new MainFragment().setMargins(0,0,20,0));
            expense.addView(total_amount);
        }

        return v;
    }
}
