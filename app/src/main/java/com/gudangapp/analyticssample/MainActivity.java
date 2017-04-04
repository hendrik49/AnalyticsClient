package com.gudangapp.analyticssample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.appcoffer.iklan.iklan.LockScreen;
import com.appcoffer.iklan.iklan.LockscreenService;
import com.appxoffer.analitycs.AnalyticLib;
import com.appxoffer.analitycs.AnalyticsException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            LockScreen.getInstance().init(this);
            LockScreen.getInstance().active();
            LockscreenService.setKey("264d277baad16c73231065bcdd020c02");
            AnalyticLib.init(this, "264d277baad16c73231065bcdd020c03");
        }catch (AnalyticsException e){
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        finish();
        try {
            AnalyticLib.onStart(this);
        }catch (AnalyticsException e){
            e.printStackTrace();
        }
    }
}
