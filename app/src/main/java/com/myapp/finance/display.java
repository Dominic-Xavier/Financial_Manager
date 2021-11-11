package com.myapp.finance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class display extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        int total_values = 0;

        TextView total_amount = new TextView(this);

        Intent in = getIntent();
        String data = in.getStringExtra("Jsondata");
        System.out.println("Json Data is"+data);
        if(data==null){
            Toast.makeText(this, "No Data..!", Toast.LENGTH_SHORT).show();
            return;
        }

        TableLayout t1 = findViewById(R.id.tbl);
        Button close = findViewById(R.id.cl);
        t1.setColumnStretchable(0, true);
        t1.setColumnStretchable(1, true);
        t1.setColumnStretchable(2, true);
        Integer Amot = 0;
        TableLayout.LayoutParams Params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        Params.setMargins(0, 0, 20, 0);
        try {
            JSONArray getResponse = new JSONArray(data);
            String date = "", Des = "";
            for (int k = 0; k < getResponse.length(); k++) {
                TableRow row = new TableRow(display.this);
                JSONObject jobj = getResponse.getJSONObject(k);
                try {
                    //Keyword = jobj.getString("Inc_Des");
                    date = jobj.getString("Date");
                    Des = jobj.getString("IncomeDes");
                    Amot = jobj.getInt("IncomeAmt");
                } catch (Exception jerror) {
                    date = jobj.getString("Date");
                    Des = jobj.getString("ExpenseDes");
                    Amot = jobj.getInt("ExpenseAmt");
                }
                //Counting Total values
                String col_name[] = {date, Des, Amot + ""};
                for (String i : col_name) {
                    row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    TextView tv = new TextView(this);
                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv.setGravity(Gravity.CENTER);
                    tv.setText(i);
                    tv.setTextSize(15);
                    tv.setPadding(5, 5, 5, 5);
                    row.addView(tv);
                }
                t1.addView(row);
                total_values += Amot;
            }
            total_amount.setText("Total:" + total_values);
            total_amount.setGravity(Gravity.RIGHT);
            total_amount.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            total_amount.setLayoutParams(Params);
            t1.addView(total_amount);
        } catch (JSONException e) {
            new sql(this).show("Sorry", e.toString(), "OK");
        } catch (Exception e) {
            new sql(this).show("Error", e.toString(), "OK");
        }

        close.setOnClickListener((v) -> {
            if (v.getId() == R.id.cl) {
                startActivity(new Intent(display.this, Database.class));
                finish();
            }
        });
    }

    public void onBackPressed() {
        startActivity(new Intent(this, Database.class));
        finish();
    }
}