package com.myapp.finance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

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

        System.out.println("Json Data of main fragment:"+Json_data);

        try {
            JSONArray jrr = new JSONArray(Json_data);
            for(int i=0;i<jrr.length();i++){
                JSONObject jobj = jrr.getJSONObject(i);
                try {
                    date.add(jobj.getString("Date"));
                    Exp_Des.add(jobj.getString("ExpenseDes"));
                    Exp_Amt.add(jobj.getString("ExpenseAmt"));
                }
                catch (Exception e){
                    try {
                        Inc_Des.add(jobj.getString("IncomeDes"));
                        Inc_amt.add(jobj.getString("IncomeAmt"));
                    }
                    catch (Exception el){
                        Toast.makeText(this,"No Value for Income",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            System.out.println("All Data:"+date+":"+Exp_Des+":"+Exp_Amt+":"+Inc_Des+":"+Inc_amt);
        }
        catch (Exception el){
            new sql(this).show("Json Error",el.toString(),"Ok");
        }

        close.setOnClickListener((v)-> {
            /*Intent intent = new Intent(MainFragment.this,Database.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MainFragment.this,Database.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
