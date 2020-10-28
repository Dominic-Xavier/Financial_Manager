package com.myapp.finance;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormatSymbols;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleInstrumentedTest {

    @Test
    public void show(){
        String data = getMonthForInt(2);
        System.out.println("show"+data);
    }

    @Test
    public void show1(){
        String data = getMonthForInt(4);
        System.out.println("show1"+data);
    }

    @Test
    public void show2(){
        String data = getMonthForInt(6);
        System.out.println("show2"+data);
    }

    @Test
    public void show3(){
        String data = getMonthForInt(33);
        System.out.println("show3"+data);
    }

    @Test
    public void show4(){
        String data = getMonthForInt(5);
        System.out.println("show4"+data);
    }

    @Test
    public String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 12 ) {
            month = months[num];
        }
        return month;
    }
}
