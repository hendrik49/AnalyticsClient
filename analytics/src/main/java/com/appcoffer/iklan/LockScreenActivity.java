package com.appcoffer.iklan;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.gudangapp.analytics.R;
import com.squareup.picasso.Picasso;


public class LockScreenActivity extends AppCompatActivity {

    private ImageView imageView;
    String images[] = {
            "http://localhost:8000/upload/images/susu2.jpg",
            "http://localhost:8000/upload/images/susu.jpeg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_lock);
        UnlockBar unlock = (UnlockBar) findViewById(R.id.unlock);

        loadImage();
        unlock.setOnUnlockListenerRight(new UnlockBar.OnUnlockListener() {
            @Override
            public void onUnlock() {
                Toast.makeText(LockScreenActivity.this, "Right Action", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        unlock.setOnUnlockListenerLeft(new UnlockBar.OnUnlockListener() {
            @Override
            public void onUnlock() {
                Toast.makeText(LockScreenActivity.this, "Left Action", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    void loadImage() {

        imageView = (ImageView) findViewById(R.id.imageViewIklan);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i=0;
            public void run() {
                Picasso.with(getApplicationContext())
                        .load(images[i])
                        .placeholder(R.drawable.unlock_thumb)
                        .error(R.drawable.iklan)
                        .resize(400, 400)
                        .into(imageView);
                i++;
                if(i>images.length-1) {
                    i=0;
                    Log.d("pict",images[i]);
                }
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 5000);

    }


    @Override
    public void onAttachedToWindow() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.onAttachedToWindow();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((LockApplication) getApplication()).lockScreenShow = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((LockApplication) getApplication()).lockScreenShow = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }
}
