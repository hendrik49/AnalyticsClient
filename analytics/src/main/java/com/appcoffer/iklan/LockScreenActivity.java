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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gudangapp.analytics.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LockScreenActivity extends AppCompatActivity {

    private final String url = "http://dashboard.appxoffer.com/iklan";
    RequestQueue queue;
    String images[] = {
            "http://www.supermetroemall.com/image/cache/data/Susu/Ultra_Milk_Cokel_510cb00d6153f-500x500.jpg",
            "http://www.supermetroemall.com/image/cache/data/Susu/Ultra_Milk_full__510cb079dd072-500x500.jpg",
            "https://s3-ap-southeast-1.amazonaws.com/mbiz-images/catalog/ultra-uht-milk-strawberry-250-ml.jpg"
    };
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_lock);
        UnlockBar unlock = (UnlockBar) findViewById(R.id.unlock);
        queue = Volley.newRequestQueue(this);

        loadData();

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

    public void loadData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadImage(response);
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", "error => " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Api-Key", "264d277baad16c73231065bcdd020c02");
                params.put("Sig", "1");
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void saveRetention(final String ret) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("open", ret);
                params.put("retention", ret);
                params.put("domain", "http://itsalif.info");
                return params;
            }
        };
    }

    void loadImage(String response) {

        imageView = (ImageView) findViewById(R.id.imageViewIklan);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i = 0;

            public void run() {
                Picasso.with(getApplicationContext())
                        .load(images[i])
                        .placeholder(R.drawable.unlock_thumb)
                        .error(R.drawable.unlock_thumb)
                        .resize(400, 400)
                        .into(imageView);
                i++;
                if (i > images.length - 1) {
                    i = 0;
                    Log.d("pict", images[i]);
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
