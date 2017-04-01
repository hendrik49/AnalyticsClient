package com.gudangapp.analyticssample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.appxoffer.analitycs.AnalyticLib;
import com.appxoffer.analitycs.AnalyticsException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try {
            AnalyticLib.init(this, "264d277baad16c73231065bcdd020c03");
        }catch (AnalyticsException e){
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        try {
            AnalyticLib.onStart(this);
        }catch (AnalyticsException e){
            e.printStackTrace();
        }
    }
}
