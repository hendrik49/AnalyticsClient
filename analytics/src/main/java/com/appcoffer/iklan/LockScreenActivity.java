package com.appcoffer.iklan;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LockScreenActivity extends AppCompatActivity {

    private final String url = "http://dashboard.appxoffer.com/iklan";
    RequestQueue queue;
    String images[];
    String titles[];
    String descriptions[];
    private ImageView imageView;
    TextView title;
    TextView descrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_lock);
        UnlockBar unlock = (UnlockBar) findViewById(R.id.unlock);
        title = (TextView) findViewById(R.id.textTitle);
        descrip = (TextView) findViewById(R.id.description);

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
                        try {
                            loadImage(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    void loadImage(String response) throws JSONException {

        JSONObject json = new JSONObject(response);
        JSONObject data = json.getJSONObject("iklans");
        JSONArray iklans = data.getJSONArray("data");

        images = new String[iklans.length()];
        titles = new String[iklans.length()];
        descriptions = new String[iklans.length()];

        for (int i = 0; i < iklans.length(); i++) {
            JSONObject iklan = iklans.getJSONObject(i);
            String picture = iklan.getString("picture");
            String name = iklan.getString("name");
            String description = iklan.getString("description");
            images[i] = picture;
            titles[i] = name;
            descriptions[i] = description;
        }

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
                    title.setText(titles[i]);
                    descrip.setText(descriptions[i]);
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
