package com.myapp.finance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import androidx.annotation.RequiresApi;

public class Getjsonarray extends AsyncTask<String,String,String> {

    Context context;
    ProgressDialog progress;

    String res;

    public Getjsonarray(Context context){
        this.context = context;
    }
    JSONArray jrr;
    String urls="";

    @Override
    protected void onPreExecute() {
        progress = new sql(context).loading("Loading");
        progress.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String u="",User_id="",sdate="",edate="",keyword="";
        u = strings[0];
        User_id = strings[1];
        try{
            switch (u){
                case "http://192.168.1.5/Display.php":{
                    urls = u;
                    sdate = strings[2];
                    edate = strings[3];
                    keyword = strings[4];
                    break;
                }
                case "http://192.168.1.5/User_details.php":{
                    urls = u;
                    sdate = "Nothing";
                    edate = "Nothing";
                    keyword="Nothing";
                    break;
                }
                case "http://192.168.1.5/Total_exp_inc.php":{
                    urls = u;
                    sdate = "Nothing";
                    edate = "Nothing";
                    keyword="Nothing";
                    break;
                }
                case "http://192.168.1.5/Total_Data.php":{
                    urls = u;
                    sdate = strings[2];
                    edate = strings[3];
                    keyword="Nothing";
                    break;
                }
                default:
                    new sql(context).show("Error","No URL","Ok");
            }
        URL url = new URL(urls);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setDoInput(true);
        http.setDoOutput(true);

        OutputStream ops = http.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
        String data = URLEncoder.encode("User_id","UTF-8")+"="+URLEncoder.encode(User_id,"UTF-8")
                +"&&"+URLEncoder.encode("sdate","UTF-8")+"="+URLEncoder.encode(sdate,"UTF-8")
                +"&&"+URLEncoder.encode("edate","UTF-8")+"="+URLEncoder.encode(edate,"UTF-8")
                +"&&"+URLEncoder.encode("keyword","UTF-8")+"="+URLEncoder.encode(keyword,"UTF-8");
        writer.write(data);
        writer.flush();
        writer.close();


            InputStream ips = http.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(ips, "UTF-8"));
            StringBuffer responseStrBuilder = new StringBuffer();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            res = responseStrBuilder.toString();

            jrr = new JSONArray(responseStrBuilder.toString());

            System.out.println("Json array:"+jrr);

        ips.close();
        ops.close();
        http.disconnect();
        return res;
        } catch (
        MalformedURLException e) {
                res = e.getMessage();
        } catch (IOException e) {
                res = e.getMessage();
        } catch (JSONException e) {
                e.printStackTrace();
        }

    return res;
    }

    @Override
    protected void onPostExecute(String s) {
        System.out.println("String JSon:"+s);
        Intent intent_name = new Intent();
        switch (urls){
            case "http://192.168.1.5/User_details.php":{
                intent_name.putExtra("Jsondata",s);
                intent_name.setClass(context.getApplicationContext(),Account_Details.class);
                context.startActivity(intent_name);
                ((Activity)context).finish();
                System.out.println("Jsondata:"+s);
                break;
            }
            case "http://192.168.1.5/Display.php":{
                    intent_name.putExtra("Jsondata",s);
                    intent_name.setClass(context.getApplicationContext(),display.class);
                    context.startActivity(intent_name);
                    ((Activity)context).finish();
                    System.out.println("Jsondata:"+s);
                    break;
                }
            case "http://192.168.1.5/Total_exp_inc.php":{
                intent_name.putExtra("Jsondata",s);
                intent_name.setClass(context.getApplicationContext(),FlowTracker.class);
                context.startActivity(intent_name);
                ((Activity)context).finish();
                System.out.println("Jsondata:"+s);
                break;
            }
            case "http://192.168.1.5/Total_Data.php":{
                System.out.println("Jsondata:"+s);
                intent_name.putExtra("Jsondata",s);
                intent_name.setClass(context.getApplicationContext(),MainFragment.class);
                context.startActivity(intent_name);
                ((Activity)context).finish();
                break;
            }
            default:
                new sql(context).show("Error","Error Occured","Ok");
                break;
        }
        progress.dismiss();
    }
}