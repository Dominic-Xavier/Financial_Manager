package com.myapp.finance;

import android.content.Context;
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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Income extends Fragment {

    Context context;

    View v;
    TableLayout income;

    private List<String> Date = new ArrayList();
    private List<String> Income = new ArrayList();
    private List<String> Amount = new ArrayList();


    public Income(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_income, container, false);
        income = v.findViewById(R.id.total_income);
        context = container.getContext();

        Date = getArguments().getStringArrayList("date");
        Income = getArguments().getStringArrayList("Inc_Des");
        Amount = getArguments().getStringArrayList("Inc_Amt");
        System.out.println("Datas of income fragment:"+Income);

        TextView total_amount = new TextView(context);

        String date="",Des="";
        int Amot,total_values = 0;
        for (int k = 0; k < Income.size(); k++) {
            TableRow row = new TableRow(context);
                date = Date.get(k);
                Des = Income.get(k);
                Amot = Integer.parseInt(Amount.get(k));
            //Counting Total values
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
            income.addView(row);
            total_values +=Amot;
        }
        total_amount.setText("Total:"+total_values);
        total_amount.setGravity(Gravity.RIGHT);
        total_amount.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        total_amount.setLayoutParams(new MainFragment().setMargins(0,0,20,0));
        income.addView(total_amount);

        return v;
    }
}
