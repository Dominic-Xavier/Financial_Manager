package com.myapp.finance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import androidx.annotation.RequiresApi;

public class sql extends SQLiteOpenHelper {
    static Context context;
    static SharedPreferences preferences;
    AlertDialog dialog;
    Uri image;


    public sql(Context context) {
        super(context, "database.db", null, 1);
        this.context = context;
    }

    public static void setData(String Key,String value,Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(Key,value);
        edit.commit();
    }

    public static String getData(String key,Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(key.equals("Username"))
            return preferences.getString("Username",null);
        else if(key.equals("u_id"))
            return preferences.getString("u_id",null);
        else if(key.equals("Profile_Pic"))
            return preferences.getString("Profile_Pic",null);
        else
            return null;
    }

    public static void delete_data(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove("Username");
        edit.remove("u_id");
        edit.commit();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table data (directory_name text NOT NULL,file_name text NOT NULL,Path text NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists data");
        onCreate(db);
    }

    public Boolean image_Details(String directory_Name,String file_Name,String path) {
        SQLiteDatabase s = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        long row = 0;
            cv.put("directory_Name", directory_Name);
            cv.put("file_Name", file_Name);
            cv.put("Path",path);

            row = s.insert("data", null, cv);

            if (row == -1)
                return false;
            else
                return true;
    }

    public String date(){
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        return date;
    }

    public Boolean validformat(String date) {
        Boolean b = false;
        try {
            Date d = null;
            final String Dateformat = "dd-mm-yyyy";
            DateFormat df = new SimpleDateFormat(Dateformat);
            df.setLenient(false);
            d = df.parse(date);
            String split[] = date.split("-");
            int dte = Integer.parseInt(split[0]);
            int month = Integer.parseInt(split[1]);
            int year = Integer.parseInt(split[2]);
            if (date.equals(df.format(d)) && dte<=31 && month<=12 && year<9999)
                b = true;
            else
                b = false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return b;
    }

    public AlertDialog show(String title,String Message,String buttonname){
        AlertDialog.Builder b = new AlertDialog.Builder(context).setTitle(title).setMessage(Message).setPositiveButton(buttonname,null);
        dialog = b.create();
        dialog.show();
        return dialog;
    }

    public TextView S_date(){
        TextView t = new TextView(context);
        t.setText("Start Date");
        t.setTextSize(20);
        t.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        return t;
    }
    public TextView E_date(){
        TextView t = new TextView(context);
        t.setText("End Date");
        t.setTextSize(20);
        t.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        return t;
    }

    public String getMonthForInt(int num) {
        return new DateFormatSymbols().getMonths()[num-1];
    }


    public CheckBox iNcome(){
        CheckBox income = new CheckBox(context);
        income.setText("Income");
        income.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        income.setId(R.id.income_check_box);
        return income;
    }


    public CheckBox eXpense(){
        CheckBox expense = new CheckBox(context);
        expense.setText("Expense");
        expense.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        expense.setId(R.id.expense_check_box);
        return expense;
    }

    public String onCheckboxClicked(CheckBox Expense, CheckBox Income){

        CheckBox expense = Expense;
        CheckBox income = Income;

        String url="-",keyword="-";
        if(expense.isChecked() && income.isChecked()){
            url="http://192.168.1.5/Total_Data.php";
            keyword = "Both";
            return keyword+";;"+url;
        }

        if(expense.isChecked()){
            url="http://192.168.1.5/Display.php";
            keyword = "expense";
            Toast.makeText(context,"eXpense is selected",Toast.LENGTH_SHORT).show();
            return keyword+";;"+url;
        }

        if(income.isChecked()){
            url="http://192.168.1.5/Display.php";
            keyword = "income";
            Toast.makeText(context,"iNcome is selected",Toast.LENGTH_SHORT).show();
            return keyword+";;"+url;
        }
        else{
            Toast.makeText(context,"Please Select an option",Toast.LENGTH_SHORT).show();
            return keyword+";;"+url;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public EditText startDate(){
        EditText start = new EditText(context);
        start.setInputType(InputType.TYPE_CLASS_DATETIME);
        start.setGravity(Gravity.CENTER);
        start.setHint("DD-MM-YYYY");
        start.setHintTextColor(Color.GRAY);
        start.setId(R.id.start_date);
        return start;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public EditText endDate(){
        EditText end = new EditText(context);
        end.setInputType(InputType.TYPE_CLASS_DATETIME);
        end.setGravity(Gravity.CENTER);
        end.setHint("DD-MM-YYYY");
        end.setHintTextColor(Color.GRAY);
        end.setId(R.id.end_date);
        return end;
    }

    public ProgressDialog loading(String title){
        ProgressDialog dialog = new ProgressDialog(context,android.R.attr.progressBarStyle);
        dialog.setContentView(new ProgressBar(context));
        dialog.setTitle(title);
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static String ip_Data(String KEY) throws IOException{
        FileInputStream inputStream = new FileInputStream("./src/main/java/com/myapp/finance/config.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        String ip = properties.getProperty(KEY);
        return ip;
    }

    public static String ip(String KEY){
        String ip="";
        switch (KEY){
            case "login":
                ip = "http://192.168.1.5/login.php";
            break;
            case "register":
                ip = "http://192.168.1.5/register.php";
            break;
            case "UserAccount":
                ip = "http://192.168.1.5/User_details.php";
            break;
            case "insertingdata":
                ip = "http://192.168.1.5:80/data.php";
            break;
            case "display_data":
                ip = "http://192.168.1.5/Display.php";
            break;
            case "total_expenses_and_income":
                ip = "http://192.168.1.5/Total_exp_inc.php";
            break;
            case "Total_Data":
                ip = "http://192.168.1.5/Total_Data.php";
            break;
            default:
                Toast.makeText(context,"incorrect ip address",Toast.LENGTH_SHORT).show();
        }
        return ip;
    }
}

