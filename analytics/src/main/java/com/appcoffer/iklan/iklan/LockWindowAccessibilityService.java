package com.appcoffer.iklan.iklan;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.appcoffer.iklan.LockApplication;

/**
 * Created by andika on 2/25/17.
 */

public class LockWindowAccessibilityService extends AccessibilityService {

    @Override
    protected boolean onKeyEvent(KeyEvent event) {

        LockScreen.getInstance().init(this);
        if(  ((LockApplication) getApplication()).lockScreenShow ){
            if(event.getKeyCode()==KeyEvent.KEYCODE_HOME || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){
                return true;
            }
        }

        return super.onKeyEvent(event);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d("onAccessibilityEvent","onAccessibilityEvent");
    }

    @Override
    public void onInterrupt() {

    }

}

