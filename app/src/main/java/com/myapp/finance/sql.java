package com.myapp.finance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class sql extends SQLiteOpenHelper {
    Context context;
    static SharedPreferences preferences;
    AlertDialog dialog;
    SQLiteDatabase db;
    ContentValues cv;
    private final static String TABLE_NAME = "financialdata";
    List<String> dates = new ArrayList<>();
    List<String> totAlExp = new ArrayList<>();
    List<String> totAlInc = new ArrayList<>();
    Map<String, List<String>> map = new HashMap<>();

    public sql(Context context) {
        super(context, "Finance.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.db = sqLiteDatabase;
        sqLiteDatabase.execSQL("create table "+TABLE_NAME+"(Dates text NOT NULL, ExpenseAmount int, IncomeAmount int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
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
        else if(key.equals("ImageName"))
            return preferences.getString("ImageName",null);
        else
            return null;
    }

    public static void delete_data(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove("Username");
        edit.remove("ImageName");
        edit.commit();
    }

    public void insertData(String date, String expenseAmount, String incomeAmount){
        try {
            if(expenseAmount==null)
                expenseAmount = String.valueOf(0);
            if(incomeAmount==null)
                incomeAmount = String.valueOf(0);
            db = this.getWritableDatabase();
            cv = new ContentValues();
            cv.put("Dates", date);
            cv.put("ExpenseAmount", expenseAmount);
            cv.put("IncomeAmount", incomeAmount);
            long insert = db.insert(TABLE_NAME, null, cv);
            if(insert==-1)
                Toast.makeText(context, "Error in inserting values...!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> getDatas(){
        db = this.getReadableDatabase();
        String Query1 = "SELECT sum(ExpenseAmount) AS ExpenseTotal, sum(IncomeAmount) AS IncomeTotal, substr(Dates,4) AS Month_and_Year" +
                "    FROM "+TABLE_NAME+
                "  GROUP BY substr(Dates,4)" +
                "    ORDER BY substr(Dates,7,2)||substr(Dates,4,2)";
        String qery = "select year (Dates) as year, month (Dates) as month,  sum(IncomeAmount) as Total_income, sum(ExpenseAmount) as Total_Expense FROM "+TABLE_NAME+" group by year(Dates), month(Dates)";
        String query = "SELECT sum(IncomeAmount) AS Total_income, sum(ExpenseAmount) AS Total_Expense, substr(Dates,4) AS Month_and_Year FROM "+TABLE_NAME;
        Cursor cursor = db.rawQuery(Query1,null);
        while (cursor.moveToNext()){
            String totalEXP =  cursor.getString(0);
            String totalINC =  cursor.getString(1);
            String monthWiseDate =  cursor.getString(2);
            dates.add(monthWiseDate);
            totAlExp.add(totalEXP);
            totAlInc.add(totalINC);
            System.out.println("All Datas are "+totalEXP+" "+totalINC+" "+monthWiseDate);
        }
        map.put("allDates", dates);
        map.put("allExpenses", totAlExp);
        map.put("allIncomes", totAlInc);
        return map;
    }

    public void deleteAllDataInTable(){
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
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
        t.setText("Start After");
        t.setTextSize(20);
        t.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        return t;
    }
    public TextView E_date(){
        TextView t = new TextView(context);
        t.setText("End Before");
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
            //url="http://192.168.1.5/Display.php";
            keyword = "Expense";
            Toast.makeText(context,"eXpense is selected",Toast.LENGTH_SHORT).show();
            return keyword+";;"+url;
        }

        if(income.isChecked()){
            //url="http://192.168.1.5/Display.php";
            keyword = "Income";
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

    public String ip(String KEY){
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

