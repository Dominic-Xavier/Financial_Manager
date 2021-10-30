package com.myapp.finance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.myapp.finance.FireBase.DatabaseOperations;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class Database extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    List<EditText> list = new ArrayList<EditText>();
    List<EditText> list1 = new ArrayList<EditText>();
    List<String> desc = new ArrayList<String>();
    List<Integer> amts = new ArrayList<Integer>();
    TableLayout t1;
    TableLayout t2;
    Button add_row;
    Button del_row;
    AlertDialog al;
    JSONObject jobj;
    ActionBarDrawerToggle action;
    DrawerLayout drawerLayout;
    Toolbar mTool;
    String u_id,option_selected;
    TableRow row;
    DatabaseOperations databaseOperations = new DatabaseOperations(this);

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        mTool = findViewById(R.id.m_tool);
        setSupportActionBar(mTool);

        drawerLayout = findViewById(R.id.drawer_layout);
        action = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(action);
        action.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        NavigationView mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Select,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
        sql s = new sql(this);


        //This View Elements are part of AlertDialogue in display Button
        Button insert = findViewById(R.id.ins);
        Button ret = findViewById(R.id.ret);
        add_row = findViewById(R.id.add);
        del_row = findViewById(R.id.delete);
        t1 = findViewById(R.id.tbl);
        t2 = findViewById(R.id.table_lay);
        TextView name = findViewById(R.id.user_name);
        TextView text = findViewById(R.id.date);
        EditText str = s.startDate();
        EditText etr = s.endDate();

        u_id = sql.getData("Username", this);
        name.setText("User:" + u_id);

        //Used for redirecting user to login page
        Handler handler = new Handler();
        handler.postAtFrontOfQueue(() -> {
            if (u_id == null || u_id == "") {
                startActivity(new Intent(Database.this, login.class));
                finish();
            }
        });

        String date = s.date();
        text.setText("Date:" + date);

        add_row.setOnClickListener(this);
        del_row.setOnClickListener(this);


        insert.setOnClickListener((v) -> {

            try {
                if(option_selected.equals("Select One"))
                    new sql(this).show("Error","Please select one option","ok");
                else{
                    String keyword;
                    if(option_selected.equals("Expense"))
                        keyword = option_selected;
                    else
                        keyword = "Income";

                    String des = "";
                    int amt = 0;

                    for (EditText text2 : list) {
                        des = text2.getText().toString();
                        desc.add(des);
                    }

                    for (EditText text1 : list1) {
                        amt = Integer.parseInt(text1.getText().toString());
                        amts.add(amt);
                    }

                    List<String> json = new ArrayList();
                    ListIterator l = desc.listIterator();
                    ListIterator l2 = amts.listIterator();
                    Map<String, Object> map = new HashMap<>();

                    if(isNetworkAvailable()){
                        while (l.hasNext() && l2.hasNext()){
                            map.put(String.valueOf(l.next()), (Integer)l2.next());
                            databaseOperations.insertData(map, keyword);
                        }
                    }
                    else
                        new sql(Database.this).show("Network Error", "Check your Internet connection and try again", "Ok");
                    /*while (l.hasNext() && l2.hasNext()) {
                        jobj = new JSONObject();
                        jobj.put("User_id", u_id);
                        jobj.put("option",keyword);
                        jobj.put("Des", l.next());
                        jobj.put("Amount", l2.next());
                        json.add((String) l.next());
                        json1.add((Integer) l2.next());
                    }*/

                    StringBuffer add = new StringBuffer();
                            add.append(json.toString());
                    System.out.println("Datas of json" + json);

                    /*if (isNetworkAvailable()) {
                        Data d = new Data(this);
                        d.setAdd(add);
                        System.out.println("Add values:"+add.toString());
                        desc.removeAll(desc);
                        amts.removeAll(amts);
                        jobj.remove("Des");
                        jobj.remove("Amount");
                        d.execute(keyword);

                    } else
                        new sql(Database.this).show("Network Error", "Check your Internet connection and try again", "Ok");*/
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ret.setOnClickListener((v) -> {
            try {
                LinearLayout layout = new LinearLayout(this);
                LinearLayout checkBox = new LinearLayout(this);
                checkBox.setOrientation(LinearLayout.HORIZONTAL);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(s.S_date());
                layout.addView(str);
                layout.addView(s.E_date());
                layout.addView(etr);
                checkBox.setGravity(Gravity.CENTER);
                CheckBox income = s.iNcome();
                checkBox.addView(income);
                CheckBox expense = s.eXpense();
                checkBox.addView(expense);
                layout.addView(checkBox);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Enter the Date")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String st_date = str.getText().toString();
                                String e_date = etr.getText().toString();
                                if (s.validformat(st_date) && s.validformat(e_date)) {
                                    String value = s.onCheckboxClicked(expense,income),url = "";
                                    String Keyword[] = value.split(";;");
                                    //Keyword[0] denotes keyword and Keyword[1] denotes URL
                                    System.out.println("Keyword:"+Keyword[0]+":"+Keyword[1]);
                                    if(Keyword[0].equals("-")){
                                        str.setText("");
                                        etr.setText("");
                                    }
                                    else if(Keyword[0].equals("Both"))
                                        databaseOperations
                                        //new Getjsonarray(Database.this).execute(Keyword[1], u_id, st_date, e_date);

                                    else if(Keyword[0].equals("Expense") || Keyword[0].equals("Income"))
                                        databaseOperations.displayData(Keyword[0], st_date, e_date);
                                        //new Getjsonarray(Database.this).execute(Keyword[1],u_id,st_date, e_date,Keyword[0]);

                                } else {
                                    s.show("Error", "Invalid format", "Ok");
                                    str.setText("");
                                    etr.setText("");
                                }
                                layout.removeAllViews();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                layout.removeAllViews();
                                str.setText("");
                                etr.setText("");
                            }
                        });
                AlertDialog al = alert.create();
                al.setView(layout);
                al.setCancelable(false);//from touching back button
                al.setCanceledOnTouchOutside(false);//from touching apart from Alert
                al.show();
            } catch (Exception e) {
                new sql(this).show("Error", e.toString(), "Ok");
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (action.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    public AlertDialog logout() {
        AlertDialog.Builder bl = new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sql.delete_data(getApplicationContext());
                        finish();
                        startActivity(new Intent(Database.this, login.class));
                    }
                })
                .setNegativeButton("No", null);
        al = bl.create();
        al.show();
        return al;
    }

    public void onBackPressed() {
        logout();
    }

    public TableRow.LayoutParams Params(int left,int top,int right,int bottom){
        TableRow.LayoutParams Params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        Params.setMargins(left,top,right ,bottom);
        return Params;
    }

    public EditText des() {
        EditText et1 = new EditText(Database.this);
        et1.setTextSize(15);
        et1.setId(R.id.Description);
        et1.setGravity(Gravity.CENTER);
        et1.setMaxWidth(et1.getWidth());
        et1.setLayoutParams(Params(40,5,5,5));
        list.add(et1);
        return et1;
    }

    public EditText amt() {
        EditText et2 = new EditText(Database.this);
        et2.setTextSize(15);
        et2.setId(R.id.Amount);
        et2.setGravity(Gravity.CENTER);
        et2.setInputType(InputType.TYPE_CLASS_NUMBER);
        et2.setMaxWidth(et2.getWidth());
        et2.setLayoutParams(Params(5,5,40,5));
        list1.add(et2);
        return et2;
    }

    @Override
    public void onClick(View v) {
        row = new TableRow(this);
        switch (v.getId()) {
            case R.id.add: {
                row.addView(des());
                row.addView(amt());
                t2.addView(row);
                System.out.println("Child Count:"+t2.getChildCount());
                break;
            }
            case R.id.delete: {
                if(t2.getChildCount()!=0){
                    row.removeView(des());
                    row.removeView(amt());
                    t2.removeViewAt(t2.getChildCount()-1);
                    list.clear();
                    list1.clear();
                    desc.clear();
                    amts.clear();
                    if (jobj != null) {
                        jobj.remove("Des");
                        jobj.remove("Amount");
                    }
                }
                break;
            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }

    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if (wifiInfo.getNetworkId() == -1) {
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        } else {
            return false; // Wi-Fi adapter is OFF
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.logout:
                logout();
                break;
            case R.id.my_account: {
                String url = "http://192.168.1.5/User_details.php";
                new Getjsonarray(this).execute(url,u_id);
                break;
            }
            case R.id.expense:{
                final String ip = "http://192.168.1.5/Total_exp_inc.php";
                new Getjsonarray(this).execute(ip,u_id);
                break;
            }
        }
        return true;
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p>
     * Implementers can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        option_selected = parent.getItemAtPosition(position).toString();
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        new sql(this).show("Error","Please select one option","Ok");
    }

}

