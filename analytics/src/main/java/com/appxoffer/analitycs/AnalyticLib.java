package com.appxoffer.analitycs;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by mujy on 12/9/16.
 */

public class AnalyticLib {
    private final static long BEGIN = 1483228800000l; //1-1-2017
    private final static String TAG="AnalyticLib";
    private final static String URL = "http://dashboard.appxoffer.com/create_data";
    private static String api_key;
    private static final byte[] IV = "rmfo9xuc4q2oawbr".getBytes();
    private static final String SALT = "q5u12e4iu13l8c79";

    public static synchronized void init(Context context, String api_key) throws AnalyticsException{
        if (!checkPermission(context,"android.permission.READ_PHONE_STATE")){
            throw new AnalyticsException("please set android.permission.READ_PHONE_STATE");
        }
        if (!checkPermission(context,"android.permission.INTERNET")){
            throw new AnalyticsException("please set android.permission.INTERNET");
        }
        if (!checkPermission(context,"android.permission.ACCESS_NETWORK_STATE")){
            throw new AnalyticsException("please set android.permission.ACCESS_NETWORK_STATE");
        }

        AnalyticLib.api_key = api_key;
    }

    private static boolean checkPermission(Context context,String permission)
    {

        //String permission = "android.permission.INTERNET";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private static String encrypt(byte[] key,String text){
        byte k[] = key;
        if (key.length>16){
            k = new byte[16];
            System.arraycopy(key,0,k,0,16);
        }
        SecretKeySpec skeySpec = new SecretKeySpec(k, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec,new IvParameterSpec(IV));
            byte[] encrypted = cipher.doFinal(text.getBytes());
            return new String(Base64.encode(encrypted, Base64.NO_WRAP));
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String hash(String text){
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(text.getBytes());
            return toHex(digest.digest());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private  static String toHex(byte bs[]){
        StringBuilder buffer = new StringBuilder(bs.length*2);
        for(byte b:bs){
            int i = b & 0xFF;
            if (i<0x10)buffer.append(0);
            buffer.append(Integer.toHexString(i));
        }
        return buffer.toString();
    }
    public static void onStart(Context context) throws AnalyticsException{
        Log.d(TAG,"onStart:"+api_key);
        String click = "start";
        final String packageName = context.getPackageName().toLowerCase();

        if (api_key==null){
            throw new AnalyticsException("api_key not set");
        }
        TelephonyManager mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei;
        try {
            imei = mngr.getDeviceId();
        } catch (Exception e){
            e.printStackTrace();
            throw  new AnalyticsException("cannot get device id");
        }
        Log.d(TAG,"package:"+packageName+","+imei);
        imei = encrypt(hash(SALT+packageName).getBytes(),imei);
        String network = mngr.getNetworkOperatorName();
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        /*Network ns[] = connManager.getAllNetworks();
        for(Network n:ns){
            if (n.isConnected()){
                wifi = n.getType()==ConnectivityManager.TYPE_WIFI;
                break;
            }
        }*/

        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String wifi = String.valueOf(mWifi.isConnected());


        String activity = context.getClass().getCanonicalName().toString();
        if (activity.toLowerCase().startsWith(packageName)){
            activity = activity.substring(packageName.length());
        }
        Uploader task = new Uploader();
        task.execute(activity,imei,network,wifi,click,packageName);
    }

    static class Uploader extends AsyncTask<Object,Integer,String> {

        @Override
        protected void onPostExecute(String s) {

            System.out.println("response:"+s);
        }

        @Override
        protected String doInBackground(Object... params) {

            String imei = params[1].toString(); // URL to call
            String activity = params[0].toString();
            String operator = params[2].toString();
            String resultToDisplay = "";
            InputStream in = null;
            HttpURLConnection conn = null;
            OutputStream os = null;
            String wifi = params[3].toString();
            String click = params[4].toString();
            String packageName = params[5].toString();
            try {

                URL url = new URL(URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                MessageDigest md5 = MessageDigest.getInstance("md5");
                StringBuilder sb = new StringBuilder();
                sb.append("a=").append(URLEncoder.encode(activity,"utf-8"));
                md5.update(activity.getBytes());
                sb.append("&b=").append(URLEncoder.encode(Build.BRAND,"utf-8"));
                md5.update(Build.BRAND.getBytes());
                sb.append("&c=").append(URLEncoder.encode(click,"utf-8")); //bollean
                md5.update(click.getBytes());
                sb.append("&i=").append(URLEncoder.encode(imei,"utf-8"));
                md5.update(imei.getBytes());
                sb.append("&m=").append(URLEncoder.encode(Build.MODEL,"utf-8"));
                md5.update(Build.MODEL.getBytes());
                sb.append("&n=").append(URLEncoder.encode(operator,"utf-8"));
                md5.update(operator.getBytes());
                sb.append("&o=android");
                md5.update("android".getBytes());
                long t = System.currentTimeMillis() - BEGIN;
                String ts = String.valueOf(t);
                sb.append("&t=").append(ts);
                md5.update(ts.getBytes());
                sb.append("&v=").append(Build.VERSION.SDK_INT);
                md5.update(String.valueOf(Build.VERSION.SDK_INT).getBytes());
                sb.append("&w=").append(wifi); //bollean
                md5.update(wifi.getBytes());


                md5.update(packageName.getBytes());
                md5.update(SALT.getBytes());
                String sig = toHex(md5.digest());
                Log.d(TAG,"post:"+sb+", sig:"+sig);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("user-agent","analytics/1.0");
                conn.setRequestProperty("content-type","application/x-www-form-urlencoded");
                conn.setRequestProperty("Api-key",api_key);
                conn.setRequestProperty("Sig",sig);
                conn.setRequestProperty("content-length",String.valueOf(sb.length()));

                os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(sb.toString());
                writer.flush();
                writer.close();
                os.close();
                os = null;
                in = new BufferedInputStream(conn.getInputStream());
                StringBuilder buffer = new StringBuilder();
                byte buf[] = new byte[128];
                int r;
                while ((r=in.read(buf))>0){
                    buffer.append(new String(buf,0,r));
                }
                return buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());

            } finally {
                if (in != null){
                    try {
                        in.close();
                    }catch (IOException e){}
                }
                if (os != null){
                    try {
                        os.close();
                    }catch (IOException e){}
                }
                if (conn!=null){
                    conn.disconnect();
                }
            }
            return null;
        }
    }
}
