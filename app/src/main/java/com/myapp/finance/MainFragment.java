package com.myapp.finance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainFragment extends AppCompatActivity {

    Button close;
    TabLayout tabs;
    ViewPager swipe;
    List date,Exp_Des,Exp_Amt,Inc_Des,Inc_amt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment);

        close = findViewById(R.id.quit);
        tabs = findViewById(R.id.tabLayout);
        swipe = findViewById(R.id.swiper);

        Intent json = getIntent();

        Bundle data = new Bundle();

        date = new ArrayList<String>();
        Exp_Des = new ArrayList<String>();
        Inc_Des = new ArrayList<String>();
        Exp_Amt = new ArrayList<String>();
        Inc_amt = new ArrayList<String>();

        String Json_data = json.getStringExtra("Jsondata");

        System.out.println("Json Datas of main fragment:"+Json_data);

        try {
            JSONArray jrr = new JSONArray(Json_data);
            JSONObject jobj = new JSONObject();
            for(int i=0;i<jrr.length();i++){
                jobj = jrr.getJSONObject(i);
                date.add(jobj.getString("Date"));
                Exp_Des.add(jobj.getString("Exp_Des"));
                Exp_Amt.add(jobj.getString("Exp_Amt"));
                Inc_Des.add(jobj.getString("Inc_Des"));
                Inc_amt.add(jobj.getString("Inc_Amt"));
                System.out.println("All Data:"+date+":"+Exp_Des+":"+Exp_Amt+":"+Inc_Des+":"+Inc_amt);
            }

        } catch (JSONException e) {
            new sql(this).show("Json Error",e.toString(),"Ok");
        }
        catch (Exception el){
            new sql(this).show("Main Error",el.toString(),"Ok");
        }

        close.setOnClickListener((v)-> {
            startActivity(new Intent(MainFragment.this,Database.class));
            finish();
        });


        data.putStringArrayList("date", (ArrayList<String>) date);
        data.putStringArrayList("Inc_Des", (ArrayList<String>) Inc_Des);
        data.putStringArrayList("Inc_Amt", (ArrayList<String>) Inc_amt);
        data.putStringArrayList("Exp_Des", (ArrayList<String>) Exp_Des);
        data.putStringArrayList("Exp_Amt", (ArrayList<String>) Exp_Amt);

        Income income = new Income();
        Expense expense = new Expense();

        System.out.println("Datas of bundle:"+data);
        PageViewAdapter adapter = new PageViewAdapter(getSupportFragmentManager(),tabs.getTabCount());
        adapter.addFragment(income,"Income");
        income.setArguments(data);
        adapter.addFragment(expense,"Expense");
        expense.setArguments(data);
        swipe.setAdapter(adapter);
        tabs.setupWithViewPager(swipe);

    }

    public TableLayout.LayoutParams setMargins(int left, int top, int right, int bottom){
        TableLayout.LayoutParams Params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        Params.setMargins(left,top,right,bottom);
        return Params;
    }
}
