package com.example.kmsapp.helper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.example.kmsapp.LoginPage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Config {

    //public static String BASE_URL = "http://192.168.0.146:5004";
    public static String BASE_URL = "http://62.72.51.244:5004";

    public static boolean DEBUG_MODE = true;
    public static String DEBUG_TAG = "MSTH";

    public static String SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME";
    public static String SP_USER_ID = "SP_USER_ID";
    public static String SP_USER_TOKEN = "SP_USER_TOKEN";
    public static String SP_USER_TYPE = "SP_USER_TYPE";
    public static String SP_USER_NAMA = "SP_USER_NAMA";
    public static String SP_USER_HP = "SP_USER_HP";
    public static String SP_USER_EMAIL = "SP_USER_EMAIL";
    public static String SP_USER_AVATAR_PATH = "SP_USER_AVATAR_PATH";


    public static String RESPONSE_STATUS_FIELD = "STATUS";
    public static String RESPONSE_STATUS_VALUE_SUCCESS = "SUCCESS";
    public static String RESPONSE_STATUS_VALUE_ERROR = "ERROR";
    public static String RESPONSE_STATUS_VALUE_UNAUTHORIZED = "UNAUTHORIZED";
    public static String RESPONSE_MESSAGE_FIELD = "MESSAGE";
    public static String RESPONSE_PAYLOAD_FIELD = "PAYLOAD";



    public static RetryPolicy getRetryPolicy() {
        return new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
    }


    public static void forceLogout(final Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(Config.SP_USER_TOKEN, "");
        editor.commit();
        Toast.makeText(context, "Anda telah logout dari aplikasi.\nUntuk mengakses fitur, Anda harus login terlebih dahulu", Toast.LENGTH_LONG).show();
        ((AppCompatActivity) context).finishAffinity();
        context.startActivity(new Intent(context, LoginPage.class));
    }


    public static String buildGetParam(Map<String, String> params){
        String builderParam = "?";
        int iParam = 1;
        for (Map.Entry<String,String> entry : params.entrySet()){
            builderParam += entry.getKey() + "=" + entry.getValue();
            if(iParam < params.size()){
                builderParam += "&";
            }
            iParam++;
        }
        return  builderParam;
    }


    public static File saveBitmapToFile(Bitmap bitmap) {
        // Tentukan direktori penyimpanan di External Storage (SD Card)
        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/Simaset");

        // Buat direktori jika belum ada
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Buat nama unik untuk file berdasarkan timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";

        // Simpan bitmap ke file di direktori yang telah ditentukan
        File file = new File(directory, fileName);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // MediaScanner akan menambahkan file ke galeri
        // Jika Anda ingin langsung terlihat di galeri, bisa diaktifkan
        // MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, null);

        return file;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Fungsi untuk mengonversi String Base64 kembali ke Bitmap
    public static Bitmap base64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    public static void showDatePickerDialog(DatePickerDialog.OnDateSetListener listener, Context context) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker();
        datePickerDialog.show();
    }

    public  static void showTimePickerDialog(TimePickerDialog.OnTimeSetListener listener, Context context){
        // Get Current Time
        int mHour, mMinute;
        final String[] times = {""};
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                listener
//            new TimePickerDialog.OnTimeSetListener() {
//
//                @Override
//                public void onTimeSet(TimePicker view, int hourOfDay,
//                                      int minute) {
//
//                    times[0] = hourOfDay + ":" + minute;
//                }
//            }
                , mHour, mMinute, false);
        timePickerDialog.show();
    }


    public static String pad(int value) {
        if (value < 10) {
            return "0" + value;
        }
        return "" + value;
    }


    public static String tglIndo(String tgl, String mode) {
        if(tgl == null || tgl.equalsIgnoreCase("null")) return "";

        if(!tgl.equalsIgnoreCase("") && !mode.equalsIgnoreCase("") && !tgl.equalsIgnoreCase("0000-00-00") && !tgl.equalsIgnoreCase("0000-00-00 00:00:00")) {
            //2022-12-11T06:00:00
            String[] t;
            String jam = "";
            if(tgl.contains("T")) {
                //tgl = tgl.replace("T", " ");
                String[] x = tgl.split("T");
                //x[0] = 2022-11-11
                //x[1] = 06:00:00
                t = x[0].split("-");
                jam = x[1];
            }
            else {
                t = tgl.split("-");
            }
            //t[0] = 2022
            //t[1] = 12
            //t[2] = 11

            JSONObject bln = new JSONObject();
            try {
                bln.put("01-LONG", "Januari");
                bln.put("01-SHORT", "Jan");
                bln.put("1-LONG","Januari");
                bln.put("1-SHORT","Jan");
                bln.put("02-LONG","Februari");
                bln.put("02-SHORT","Feb");
                bln.put("2-LONG","Februari");
                bln.put("2-SHORT","Feb");
                bln.put("03-LONG","Maret");
                bln.put("03-SHORT","Mar");
                bln.put("3-LONG","Maret");
                bln.put("3-SHORT","Mar");
                bln.put("04-LONG","April");
                bln.put("04-SHORT","Apr");
                bln.put("4-LONG","April");
                bln.put("4-SHORT","Apr");
                bln.put("05-LONG","Mei");
                bln.put("05-SHORT","Mei");
                bln.put("5-LONG","Mei");
                bln.put("5-SHORT","Mei");
                bln.put("06-LONG","Juni");
                bln.put("06-SHORT","Jun");
                bln.put("6-LONG","Juni");
                bln.put("6-SHORT","Jun");
                bln.put("07-LONG","Juli");
                bln.put("07-SHORT","Jul");
                bln.put("7-LONG","Juli");
                bln.put("7-SHORT","Jul");
                bln.put("08-LONG","Agustus");
                bln.put("08-SHORT","Ags");
                bln.put("8-LONG","Agustus");
                bln.put("8-SHORT","Ags");
                bln.put("09-LONG","September");
                bln.put("09-SHORT","Sep");
                bln.put("9-LONG","September");
                bln.put("9-SHORT","Sep");
                bln.put("10-LONG","Oktober");
                bln.put("10-SHORT","Okt");
                bln.put("11-LONG","November");
                bln.put("11-SHORT","Nov");
                bln.put("12-LONG","Desember");
                bln.put("12-SHORT","Des");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            String b = t[1];

            if (!t[2].contains(":")) { //tdk ada format waktu
                //
            }
            else {
                String[] j = t[2].split(" ");
                t[2] = j[0];
                jam = j[1];
            }

            return t[2]+" "+ bln.optString(b + "-" + mode) + " " + t[0] + " " + jam;
        }
        else {
            return "-";
        }
    }


    public static final ArrayList<String> getBalitaStatus(String key, Double value){
        ArrayList<String> ret = new ArrayList<>();
//        if(true){
//            ret.add("Normal");
//            ret.add("1.1 2.2");
//            return ret;
//        }

        HashMap<String, ArrayList<Double>> ref = new HashMap<>();
        ArrayList<Double> aData;

        // berat
        aData = new ArrayList<>();aData.add(2.5);aData.add(3.9);ref.put("GENDER_MALE BERAT 0", aData);
        aData = new ArrayList<>();aData.add(46.1);aData.add(55.6);ref.put("GENDER_MALE TINGGI 0", aData);
        aData = new ArrayList<>();aData.add(33.1);aData.add(35.8);ref.put("GENDER_MALE KEPALA 0", aData);
        aData = new ArrayList<>();aData.add(3.4);aData.add(5.1);ref.put("GENDER_MALE BERAT 1", aData);
        aData = new ArrayList<>();aData.add(50.8);aData.add(60.6);ref.put("GENDER_MALE TINGGI 1", aData);
        aData = new ArrayList<>();aData.add(36.1);aData.add(38.5);ref.put("GENDER_MALE KEPALA 1", aData);
        aData = new ArrayList<>();aData.add(4.4);aData.add(6.3);ref.put("GENDER_MALE BERAT 2", aData);
        aData = new ArrayList<>();aData.add(54.4);aData.add(64.4);ref.put("GENDER_MALE TINGGI 2", aData);
        aData = new ArrayList<>();aData.add(37.9);aData.add(40.3);ref.put("GENDER_MALE KEPALA 2", aData);
        aData = new ArrayList<>();aData.add(6.3);aData.add(7.2);ref.put("GENDER_MALE BERAT 3", aData);
        aData = new ArrayList<>();aData.add(57.3);aData.add(67.6);ref.put("GENDER_MALE TINGGI 3", aData);
        aData = new ArrayList<>();aData.add(39.3);aData.add(41.7);ref.put("GENDER_MALE KEPALA 3", aData);
        aData = new ArrayList<>();aData.add(5.6);aData.add(7.9);ref.put("GENDER_MALE BERAT 4", aData);
        aData = new ArrayList<>();aData.add(59.7);aData.add(70.1);ref.put("GENDER_MALE TINGGI 4", aData);
        aData = new ArrayList<>();aData.add(40.4);aData.add(42.9);ref.put("GENDER_MALE KEPALA 4", aData);
        aData = new ArrayList<>();aData.add(6.1);aData.add(8.4);ref.put("GENDER_MALE BERAT 5", aData);
        aData = new ArrayList<>();aData.add(61.7);aData.add(72.2);ref.put("GENDER_MALE TINGGI 5", aData);
        aData = new ArrayList<>();aData.add(41.3);aData.add(43.8);ref.put("GENDER_MALE KEPALA 5", aData);
        aData = new ArrayList<>();aData.add(6.4);aData.add(8.9);ref.put("GENDER_MALE BERAT 6", aData);
        aData = new ArrayList<>();aData.add(63.3);aData.add(74.0);ref.put("GENDER_MALE TINGGI 6", aData);
        aData = new ArrayList<>();aData.add(42.1);aData.add(44.6);ref.put("GENDER_MALE KEPALA 6", aData);
        aData = new ArrayList<>();aData.add(6.7);aData.add(9.3);ref.put("GENDER_MALE BERAT 7", aData);
        aData = new ArrayList<>();aData.add(64.8);aData.add(75.7);ref.put("GENDER_MALE TINGGI 7", aData);
        aData = new ArrayList<>();aData.add(42.7);aData.add(45.3);ref.put("GENDER_MALE KEPALA 7", aData);
        aData = new ArrayList<>();aData.add(7.0);aData.add(9.6);ref.put("GENDER_MALE BERAT 8", aData);
        aData = new ArrayList<>();aData.add(66.2);aData.add(77.2);ref.put("GENDER_MALE TINGGI 8", aData);
        aData = new ArrayList<>();aData.add(43.2);aData.add(45.8);ref.put("GENDER_MALE KEPALA 8", aData);
        aData = new ArrayList<>();aData.add(7.2);aData.add(10.0);ref.put("GENDER_MALE BERAT 9", aData);
        aData = new ArrayList<>();aData.add(67.5);aData.add(78.7);ref.put("GENDER_MALE TINGGI 9", aData);
        aData = new ArrayList<>();aData.add(43.7);aData.add(46.3);ref.put("GENDER_MALE KEPALA 9", aData);
        aData = new ArrayList<>();aData.add(7.5);aData.add(10.3);ref.put("GENDER_MALE BERAT 10", aData);
        aData = new ArrayList<>();aData.add(68.7);aData.add(80.1);ref.put("GENDER_MALE TINGGI 10", aData);
        aData = new ArrayList<>();aData.add(44.1);aData.add(46.7);ref.put("GENDER_MALE KEPALA 10", aData);
        aData = new ArrayList<>();aData.add(7.7);aData.add(10.5);ref.put("GENDER_MALE BERAT 11", aData);
        aData = new ArrayList<>();aData.add(69.9);aData.add(81.5);ref.put("GENDER_MALE TINGGI 11", aData);
        aData = new ArrayList<>();aData.add(44.4);aData.add(47.1);ref.put("GENDER_MALE KEPALA 11", aData);
        aData = new ArrayList<>();aData.add(7.8);aData.add(10.8);ref.put("GENDER_MALE BERAT 12", aData);
        aData = new ArrayList<>();aData.add(71.0);aData.add(82.9);ref.put("GENDER_MALE TINGGI 12", aData);
        aData = new ArrayList<>();aData.add(44.7);aData.add(47.4);ref.put("GENDER_MALE KEPALA 12", aData);
        aData = new ArrayList<>();aData.add(8.0);aData.add(11.1);ref.put("GENDER_MALE BERAT 13", aData);
        aData = new ArrayList<>();aData.add(72.1);aData.add(84.2);ref.put("GENDER_MALE TINGGI 13", aData);
        aData = new ArrayList<>();aData.add(45.0);aData.add(47.7);ref.put("GENDER_MALE KEPALA 13", aData);
        aData = new ArrayList<>();aData.add(8.2);aData.add(11.3);ref.put("GENDER_MALE BERAT 14", aData);
        aData = new ArrayList<>();aData.add(73.1);aData.add(85.5);ref.put("GENDER_MALE TINGGI 14", aData);
        aData = new ArrayList<>();aData.add(45.2);aData.add(47.9);ref.put("GENDER_MALE KEPALA 14", aData);
        aData = new ArrayList<>();aData.add(8.4);aData.add(11.6);ref.put("GENDER_MALE BERAT 15", aData);
        aData = new ArrayList<>();aData.add(74.1);aData.add(86.7);ref.put("GENDER_MALE TINGGI 15", aData);
        aData = new ArrayList<>();aData.add(45.5);aData.add(48.2);ref.put("GENDER_MALE KEPALA 15", aData);
        aData = new ArrayList<>();aData.add(8.5);aData.add(11.8);ref.put("GENDER_MALE BERAT 16", aData);
        aData = new ArrayList<>();aData.add(75.0);aData.add(88.0);ref.put("GENDER_MALE TINGGI 16", aData);
        aData = new ArrayList<>();aData.add(45.6);aData.add(48.4);ref.put("GENDER_MALE KEPALA 16", aData);
        aData = new ArrayList<>();aData.add(8.7);aData.add(12.0);ref.put("GENDER_MALE BERAT 17", aData);
        aData = new ArrayList<>();aData.add(76.0);aData.add(89.2);ref.put("GENDER_MALE TINGGI 17", aData);
        aData = new ArrayList<>();aData.add(45.8);aData.add(48.6);ref.put("GENDER_MALE KEPALA 17", aData);
        aData = new ArrayList<>();aData.add(8.9);aData.add(12.3);ref.put("GENDER_MALE BERAT 18", aData);
        aData = new ArrayList<>();aData.add(76.9);aData.add(90.4);ref.put("GENDER_MALE TINGGI 18", aData);
        aData = new ArrayList<>();aData.add(46.0);aData.add(48.7);ref.put("GENDER_MALE KEPALA 18", aData);
        aData = new ArrayList<>();aData.add(9.0);aData.add(12.5);ref.put("GENDER_MALE BERAT 19", aData);
        aData = new ArrayList<>();aData.add(77.7);aData.add(91.5);ref.put("GENDER_MALE TINGGI 19", aData);
        aData = new ArrayList<>();aData.add(46.2);aData.add(48.9);ref.put("GENDER_MALE KEPALA 19", aData);
        aData = new ArrayList<>();aData.add(9.2);aData.add(12.7);ref.put("GENDER_MALE BERAT 20", aData);
        aData = new ArrayList<>();aData.add(78.6);aData.add(92.6);ref.put("GENDER_MALE TINGGI 20", aData);
        aData = new ArrayList<>();aData.add(46.3);aData.add(49.1);ref.put("GENDER_MALE KEPALA 20", aData);
        aData = new ArrayList<>();aData.add(9.3);aData.add(13.0);ref.put("GENDER_MALE BERAT 21", aData);
        aData = new ArrayList<>();aData.add(79.4);aData.add(93.8);ref.put("GENDER_MALE TINGGI 21", aData);
        aData = new ArrayList<>();aData.add(46.4);aData.add(49.2);ref.put("GENDER_MALE KEPALA 21", aData);
        aData = new ArrayList<>();aData.add(9.5);aData.add(13.2);ref.put("GENDER_MALE BERAT 22", aData);
        aData = new ArrayList<>();aData.add(80.2);aData.add(94.9);ref.put("GENDER_MALE TINGGI 22", aData);
        aData = new ArrayList<>();aData.add(46.6);aData.add(49.4);ref.put("GENDER_MALE KEPALA 22", aData);
        aData = new ArrayList<>();aData.add(9.7);aData.add(13.4);ref.put("GENDER_MALE BERAT 23", aData);
        aData = new ArrayList<>();aData.add(81.0);aData.add(95.9);ref.put("GENDER_MALE TINGGI 23", aData);
        aData = new ArrayList<>();aData.add(46.7);aData.add(49.5);ref.put("GENDER_MALE KEPALA 23", aData);
        aData = new ArrayList<>();aData.add(9.8);aData.add(13.7);ref.put("GENDER_MALE BERAT 24", aData);
        aData = new ArrayList<>();aData.add(81.7);aData.add(97.0);ref.put("GENDER_MALE TINGGI 24", aData);
        aData = new ArrayList<>();aData.add(46.8);aData.add(49.7);ref.put("GENDER_MALE KEPALA 24", aData);
        aData = new ArrayList<>();aData.add(10.0);aData.add(13.9);ref.put("GENDER_MALE BERAT 25", aData);
        aData = new ArrayList<>();aData.add(81.7);aData.add(97.3);ref.put("GENDER_MALE TINGGI 25", aData);
        aData = new ArrayList<>();aData.add(47.0);aData.add(49.8);ref.put("GENDER_MALE KEPALA 25", aData);
        aData = new ArrayList<>();aData.add(10.1);aData.add(14.1);ref.put("GENDER_MALE BERAT 26", aData);
        aData = new ArrayList<>();aData.add(82.5);aData.add(98.3);ref.put("GENDER_MALE TINGGI 26", aData);
        aData = new ArrayList<>();aData.add(47.1);aData.add(49.9);ref.put("GENDER_MALE KEPALA 26", aData);
        aData = new ArrayList<>();aData.add(10.2);aData.add(14.4);ref.put("GENDER_MALE BERAT 27", aData);
        aData = new ArrayList<>();aData.add(83.1);aData.add(99.3);ref.put("GENDER_MALE TINGGI 27", aData);
        aData = new ArrayList<>();aData.add(47.2);aData.add(50.0);ref.put("GENDER_MALE KEPALA 27", aData);
        aData = new ArrayList<>();aData.add(10.4);aData.add(14.6);ref.put("GENDER_MALE BERAT 28", aData);
        aData = new ArrayList<>();aData.add(83.8);aData.add(100.3);ref.put("GENDER_MALE TINGGI 28", aData);
        aData = new ArrayList<>();aData.add(47.3);aData.add(50.2);ref.put("GENDER_MALE KEPALA 28", aData);
        aData = new ArrayList<>();aData.add(10.5);aData.add(14.8);ref.put("GENDER_MALE BERAT 29", aData);
        aData = new ArrayList<>();aData.add(84.5);aData.add(101.2);ref.put("GENDER_MALE TINGGI 29", aData);
        aData = new ArrayList<>();aData.add(47.4);aData.add(50.3);ref.put("GENDER_MALE KEPALA 29", aData);
        aData = new ArrayList<>();aData.add(10.7);aData.add(15.0);ref.put("GENDER_MALE BERAT 30", aData);
        aData = new ArrayList<>();aData.add(85.1);aData.add(102.1);ref.put("GENDER_MALE TINGGI 30", aData);
        aData = new ArrayList<>();aData.add(47.5);aData.add(50.4);ref.put("GENDER_MALE KEPALA 30", aData);
        aData = new ArrayList<>();aData.add(10.8);aData.add(15.2);ref.put("GENDER_MALE BERAT 31", aData);
        aData = new ArrayList<>();aData.add(85.7);aData.add(103.0);ref.put("GENDER_MALE TINGGI 31", aData);
        aData = new ArrayList<>();aData.add(47.6);aData.add(50.5);ref.put("GENDER_MALE KEPALA 31", aData);
        aData = new ArrayList<>();aData.add(10.9);aData.add(15.5);ref.put("GENDER_MALE BERAT 32", aData);
        aData = new ArrayList<>();aData.add(86.4);aData.add(103.9);ref.put("GENDER_MALE TINGGI 32", aData);
        aData = new ArrayList<>();aData.add(47.7);aData.add(50.6);ref.put("GENDER_MALE KEPALA 32", aData);
        aData = new ArrayList<>();aData.add(11.1);aData.add(15.7);ref.put("GENDER_MALE BERAT 33", aData);
        aData = new ArrayList<>();aData.add(86.9);aData.add(104.8);ref.put("GENDER_MALE TINGGI 33", aData);
        aData = new ArrayList<>();aData.add(47.8);aData.add(50.7);ref.put("GENDER_MALE KEPALA 33", aData);
        aData = new ArrayList<>();aData.add(11.2);aData.add(15.9);ref.put("GENDER_MALE BERAT 34", aData);
        aData = new ArrayList<>();aData.add(87.5);aData.add(105.6);ref.put("GENDER_MALE TINGGI 34", aData);
        aData = new ArrayList<>();aData.add(47.8);aData.add(50.8);ref.put("GENDER_MALE KEPALA 34", aData);
        aData = new ArrayList<>();aData.add(11.3);aData.add(16.1);ref.put("GENDER_MALE BERAT 35", aData);
        aData = new ArrayList<>();aData.add(88.1);aData.add(106.4);ref.put("GENDER_MALE TINGGI 35", aData);
        aData = new ArrayList<>();aData.add(47.9);aData.add(50.8);ref.put("GENDER_MALE KEPALA 35", aData);
        aData = new ArrayList<>();aData.add(11.4);aData.add(16.3);ref.put("GENDER_MALE BERAT 36", aData);
        aData = new ArrayList<>();aData.add(88.7);aData.add(107.2);ref.put("GENDER_MALE TINGGI 36", aData);
        aData = new ArrayList<>();aData.add(48.0);aData.add(50.9);ref.put("GENDER_MALE KEPALA 36", aData);
        aData = new ArrayList<>();aData.add(11.6);aData.add(16.5);ref.put("GENDER_MALE BERAT 37", aData);
        aData = new ArrayList<>();aData.add(89.2);aData.add(108.0);ref.put("GENDER_MALE TINGGI 37", aData);
        aData = new ArrayList<>();aData.add(48.1);aData.add(51.0);ref.put("GENDER_MALE KEPALA 37", aData);
        aData = new ArrayList<>();aData.add(11.7);aData.add(16.7);ref.put("GENDER_MALE BERAT 38", aData);
        aData = new ArrayList<>();aData.add(89.8);aData.add(108.8);ref.put("GENDER_MALE TINGGI 38", aData);
        aData = new ArrayList<>();aData.add(48.1);aData.add(51.1);ref.put("GENDER_MALE KEPALA 38", aData);
        aData = new ArrayList<>();aData.add(11.8);aData.add(16.9);ref.put("GENDER_MALE BERAT 39", aData);
        aData = new ArrayList<>();aData.add(90.3);aData.add(109.5);ref.put("GENDER_MALE TINGGI 39", aData);
        aData = new ArrayList<>();aData.add(48.2);aData.add(51.2);ref.put("GENDER_MALE KEPALA 39", aData);
        aData = new ArrayList<>();aData.add(11.9);aData.add(17.1);ref.put("GENDER_MALE BERAT 40", aData);
        aData = new ArrayList<>();aData.add(90.9);aData.add(110.3);ref.put("GENDER_MALE TINGGI 40", aData);
        aData = new ArrayList<>();aData.add(48.3);aData.add(51.2);ref.put("GENDER_MALE KEPALA 40", aData);
        aData = new ArrayList<>();aData.add(12.1);aData.add(17.3);ref.put("GENDER_MALE BERAT 41", aData);
        aData = new ArrayList<>();aData.add(91.4);aData.add(111.0);ref.put("GENDER_MALE TINGGI 41", aData);
        aData = new ArrayList<>();aData.add(48.3);aData.add(51.3);ref.put("GENDER_MALE KEPALA 41", aData);
        aData = new ArrayList<>();aData.add(12.2);aData.add(17.5);ref.put("GENDER_MALE BERAT 42", aData);
        aData = new ArrayList<>();aData.add(91.9);aData.add(111.7);ref.put("GENDER_MALE TINGGI 42", aData);
        aData = new ArrayList<>();aData.add(48.4);aData.add(51.4);ref.put("GENDER_MALE KEPALA 42", aData);
        aData = new ArrayList<>();aData.add(12.3);aData.add(17.7);ref.put("GENDER_MALE BERAT 43", aData);
        aData = new ArrayList<>();aData.add(92.4);aData.add(112.5);ref.put("GENDER_MALE TINGGI 43", aData);
        aData = new ArrayList<>();aData.add(48.4);aData.add(51.4);ref.put("GENDER_MALE KEPALA 43", aData);
        aData = new ArrayList<>();aData.add(12.4);aData.add(17.9);ref.put("GENDER_MALE BERAT 44", aData);
        aData = new ArrayList<>();aData.add(93.0);aData.add(113.2);ref.put("GENDER_MALE TINGGI 44", aData);
        aData = new ArrayList<>();aData.add(48.5);aData.add(51.5);ref.put("GENDER_MALE KEPALA 44", aData);
        aData = new ArrayList<>();aData.add(12.5);aData.add(18.1);ref.put("GENDER_MALE BERAT 45", aData);
        aData = new ArrayList<>();aData.add(93.5);aData.add(113.9);ref.put("GENDER_MALE TINGGI 45", aData);
        aData = new ArrayList<>();aData.add(48.5);aData.add(51.6);ref.put("GENDER_MALE KEPALA 45", aData);
        aData = new ArrayList<>();aData.add(12.7);aData.add(18.3);ref.put("GENDER_MALE BERAT 46", aData);
        aData = new ArrayList<>();aData.add(94.0);aData.add(114.6);ref.put("GENDER_MALE TINGGI 46", aData);
        aData = new ArrayList<>();aData.add(48.6);aData.add(51.6);ref.put("GENDER_MALE KEPALA 46", aData);
        aData = new ArrayList<>();aData.add(12.8);aData.add(18.5);ref.put("GENDER_MALE BERAT 47", aData);
        aData = new ArrayList<>();aData.add(94.4);aData.add(115.2);ref.put("GENDER_MALE TINGGI 47", aData);
        aData = new ArrayList<>();aData.add(48.6);aData.add(51.7);ref.put("GENDER_MALE KEPALA 47", aData);
        aData = new ArrayList<>();aData.add(12.9);aData.add(18.7);ref.put("GENDER_MALE BERAT 48", aData);
        aData = new ArrayList<>();aData.add(94.9);aData.add(115.9);ref.put("GENDER_MALE TINGGI 48", aData);
        aData = new ArrayList<>();aData.add(48.7);aData.add(51.7);ref.put("GENDER_MALE KEPALA 48", aData);
        aData = new ArrayList<>();aData.add(13.0);aData.add(18.9);ref.put("GENDER_MALE BERAT 49", aData);
        aData = new ArrayList<>();aData.add(95.4);aData.add(116.6);ref.put("GENDER_MALE TINGGI 49", aData);
        aData = new ArrayList<>();aData.add(48.7);aData.add(51.8);ref.put("GENDER_MALE KEPALA 49", aData);
        aData = new ArrayList<>();aData.add(13.1);aData.add(19.1);ref.put("GENDER_MALE BERAT 50", aData);
        aData = new ArrayList<>();aData.add(95.9);aData.add(117.3);ref.put("GENDER_MALE TINGGI 50", aData);
        aData = new ArrayList<>();aData.add(48.8);aData.add(51.8);ref.put("GENDER_MALE KEPALA 50", aData);
        aData = new ArrayList<>();aData.add(13.3);aData.add(19.3);ref.put("GENDER_MALE BERAT 51", aData);
        aData = new ArrayList<>();aData.add(96.4);aData.add(117.9);ref.put("GENDER_MALE TINGGI 51", aData);
        aData = new ArrayList<>();aData.add(48.8);aData.add(51.9);ref.put("GENDER_MALE KEPALA 51", aData);
        aData = new ArrayList<>();aData.add(13.4);aData.add(19.5);ref.put("GENDER_MALE BERAT 52", aData);
        aData = new ArrayList<>();aData.add(96.9);aData.add(118.6);ref.put("GENDER_MALE TINGGI 52", aData);
        aData = new ArrayList<>();aData.add(48.9);aData.add(51.9);ref.put("GENDER_MALE KEPALA 52", aData);
        aData = new ArrayList<>();aData.add(13.5);aData.add(19.7);ref.put("GENDER_MALE BERAT 53", aData);
        aData = new ArrayList<>();aData.add(97.4);aData.add(119.2);ref.put("GENDER_MALE TINGGI 53", aData);
        aData = new ArrayList<>();aData.add(48.9);aData.add(52.0);ref.put("GENDER_MALE KEPALA 53", aData);
        aData = new ArrayList<>();aData.add(13.6);aData.add(19.9);ref.put("GENDER_MALE BERAT 54", aData);
        aData = new ArrayList<>();aData.add(97.8);aData.add(119.9);ref.put("GENDER_MALE TINGGI 54", aData);
        aData = new ArrayList<>();aData.add(49.0);aData.add(52.0);ref.put("GENDER_MALE KEPALA 54", aData);
        aData = new ArrayList<>();aData.add(13.7);aData.add(20.1);ref.put("GENDER_MALE BERAT 55", aData);
        aData = new ArrayList<>();aData.add(98.3);aData.add(120.6);ref.put("GENDER_MALE TINGGI 55", aData);
        aData = new ArrayList<>();aData.add(49.0);aData.add(52.1);ref.put("GENDER_MALE KEPALA 55", aData);
        aData = new ArrayList<>();aData.add(13.8);aData.add(20.3);ref.put("GENDER_MALE BERAT 56", aData);
        aData = new ArrayList<>();aData.add(98.8);aData.add(121.2);ref.put("GENDER_MALE TINGGI 56", aData);
        aData = new ArrayList<>();aData.add(49.0);aData.add(52.1);ref.put("GENDER_MALE KEPALA 56", aData);
        aData = new ArrayList<>();aData.add(13.9);aData.add(20.5);ref.put("GENDER_MALE BERAT 57", aData);
        aData = new ArrayList<>();aData.add(99.3);aData.add(121.9);ref.put("GENDER_MALE TINGGI 57", aData);
        aData = new ArrayList<>();aData.add(49.1);aData.add(52.2);ref.put("GENDER_MALE KEPALA 57", aData);
        aData = new ArrayList<>();aData.add(14.1);aData.add(20.7);ref.put("GENDER_MALE BERAT 58", aData);
        aData = new ArrayList<>();aData.add(99.7);aData.add(122.6);ref.put("GENDER_MALE TINGGI 58", aData);
        aData = new ArrayList<>();aData.add(49.1);aData.add(52.2);ref.put("GENDER_MALE KEPALA 58", aData);
        aData = new ArrayList<>();aData.add(14.2);aData.add(20.9);ref.put("GENDER_MALE BERAT 59", aData);
        aData = new ArrayList<>();aData.add(100.2);aData.add(123.2);ref.put("GENDER_MALE TINGGI 59", aData);
        aData = new ArrayList<>();aData.add(49.2);aData.add(52.2);ref.put("GENDER_MALE KEPALA 59", aData);
        aData = new ArrayList<>();aData.add(14.3);aData.add(21.1);ref.put("GENDER_MALE BERAT 60", aData);
        aData = new ArrayList<>();aData.add(100.7);aData.add(123.9);ref.put("GENDER_MALE TINGGI 60", aData);
        aData = new ArrayList<>();aData.add(49.2);aData.add(52.3);ref.put("GENDER_MALE KEPALA 60", aData);
        aData = new ArrayList<>();aData.add(2.4);aData.add(3.7);ref.put("GENDER_FEMALE BERAT 0", aData);
        aData = new ArrayList<>();aData.add(45.4);aData.add(54.7);ref.put("GENDER_FEMALE TINGGI 0", aData);
        aData = new ArrayList<>();aData.add(32.7);aData.add(35.1);ref.put("GENDER_FEMALE KEPALA 0", aData);
        aData = new ArrayList<>();aData.add(3.2);aData.add(4.8);ref.put("GENDER_FEMALE BERAT 1", aData);
        aData = new ArrayList<>();aData.add(49.8);aData.add(59.5);ref.put("GENDER_FEMALE TINGGI 1", aData);
        aData = new ArrayList<>();aData.add(35.3);aData.add(37.8);ref.put("GENDER_FEMALE KEPALA 1", aData);
        aData = new ArrayList<>();aData.add(4.0);aData.add(5.9);ref.put("GENDER_FEMALE BERAT 2", aData);
        aData = new ArrayList<>();aData.add(53.0);aData.add(63.2);ref.put("GENDER_FEMALE TINGGI 2", aData);
        aData = new ArrayList<>();aData.add(37.0);aData.add(39.5);ref.put("GENDER_FEMALE KEPALA 2", aData);
        aData = new ArrayList<>();aData.add(4.6);aData.add(6.7);ref.put("GENDER_FEMALE BERAT 3", aData);
        aData = new ArrayList<>();aData.add(55.6);aData.add(66.1);ref.put("GENDER_FEMALE TINGGI 3", aData);
        aData = new ArrayList<>();aData.add(38.2);aData.add(40.8);ref.put("GENDER_FEMALE KEPALA 3", aData);
        aData = new ArrayList<>();aData.add(5.1);aData.add(7.3);ref.put("GENDER_FEMALE BERAT 4", aData);
        aData = new ArrayList<>();aData.add(57.8);aData.add(68.6);ref.put("GENDER_FEMALE TINGGI 4", aData);
        aData = new ArrayList<>();aData.add(39.3);aData.add(41.9);ref.put("GENDER_FEMALE KEPALA 4", aData);
        aData = new ArrayList<>();aData.add(5.5);aData.add(7.8);ref.put("GENDER_FEMALE BERAT 5", aData);
        aData = new ArrayList<>();aData.add(59.6);aData.add(70.7);ref.put("GENDER_FEMALE TINGGI 5", aData);
        aData = new ArrayList<>();aData.add(40.1);aData.add(42.8);ref.put("GENDER_FEMALE KEPALA 5", aData);
        aData = new ArrayList<>();aData.add(5.8);aData.add(8.3);ref.put("GENDER_FEMALE BERAT 6", aData);
        aData = new ArrayList<>();aData.add(61.2);aData.add(72.5);ref.put("GENDER_FEMALE TINGGI 6", aData);
        aData = new ArrayList<>();aData.add(40.8);aData.add(43.5);ref.put("GENDER_FEMALE KEPALA 6", aData);
        aData = new ArrayList<>();aData.add(6.1);aData.add(8.7);ref.put("GENDER_FEMALE BERAT 7", aData);
        aData = new ArrayList<>();aData.add(62.7);aData.add(74.2);ref.put("GENDER_FEMALE TINGGI 7", aData);
        aData = new ArrayList<>();aData.add(41.5);aData.add(44.2);ref.put("GENDER_FEMALE KEPALA 7", aData);
        aData = new ArrayList<>();aData.add(6.3);aData.add(9.0);ref.put("GENDER_FEMALE BERAT 8", aData);
        aData = new ArrayList<>();aData.add(64.0);aData.add(75.8);ref.put("GENDER_FEMALE TINGGI 8", aData);
        aData = new ArrayList<>();aData.add(42.0);aData.add(44.7);ref.put("GENDER_FEMALE KEPALA 8", aData);
        aData = new ArrayList<>();aData.add(6.6);aData.add(9.3);ref.put("GENDER_FEMALE BERAT 9", aData);
        aData = new ArrayList<>();aData.add(65.3);aData.add(77.4);ref.put("GENDER_FEMALE TINGGI 9", aData);
        aData = new ArrayList<>();aData.add(42.4);aData.add(45.2);ref.put("GENDER_FEMALE KEPALA 9", aData);
        aData = new ArrayList<>();aData.add(6.8);aData.add(9.6);ref.put("GENDER_FEMALE BERAT 10", aData);
        aData = new ArrayList<>();aData.add(66.5);aData.add(78.9);ref.put("GENDER_FEMALE TINGGI 10", aData);
        aData = new ArrayList<>();aData.add(42.8);aData.add(45.6);ref.put("GENDER_FEMALE KEPALA 10", aData);
        aData = new ArrayList<>();aData.add(7.0);aData.add(9.9);ref.put("GENDER_FEMALE BERAT 11", aData);
        aData = new ArrayList<>();aData.add(67.7);aData.add(80.3);ref.put("GENDER_FEMALE TINGGI 11", aData);
        aData = new ArrayList<>();aData.add(43.2);aData.add(46.0);ref.put("GENDER_FEMALE KEPALA 11", aData);
        aData = new ArrayList<>();aData.add(7.1);aData.add(10.2);ref.put("GENDER_FEMALE BERAT 12", aData);
        aData = new ArrayList<>();aData.add(68.9);aData.add(81.7);ref.put("GENDER_FEMALE TINGGI 12", aData);
        aData = new ArrayList<>();aData.add(43.5);aData.add(46.3);ref.put("GENDER_FEMALE KEPALA 12", aData);
        aData = new ArrayList<>();aData.add(7.3);aData.add(10.4);ref.put("GENDER_FEMALE BERAT 13", aData);
        aData = new ArrayList<>();aData.add(70.0);aData.add(83.1);ref.put("GENDER_FEMALE TINGGI 13", aData);
        aData = new ArrayList<>();aData.add(43.8);aData.add(46.6);ref.put("GENDER_FEMALE KEPALA 13", aData);
        aData = new ArrayList<>();aData.add(7.5);aData.add(10.7);ref.put("GENDER_FEMALE BERAT 14", aData);
        aData = new ArrayList<>();aData.add(71.0);aData.add(84.4);ref.put("GENDER_FEMALE TINGGI 14", aData);
        aData = new ArrayList<>();aData.add(44.0);aData.add(46.8);ref.put("GENDER_FEMALE KEPALA 14", aData);
        aData = new ArrayList<>();aData.add(7.7);aData.add(10.9);ref.put("GENDER_FEMALE BERAT 15", aData);
        aData = new ArrayList<>();aData.add(72.0);aData.add(85.7);ref.put("GENDER_FEMALE TINGGI 15", aData);
        aData = new ArrayList<>();aData.add(44.2);aData.add(44.2);ref.put("GENDER_FEMALE KEPALA 15", aData);
        aData = new ArrayList<>();aData.add(7.8);aData.add(11.2);ref.put("GENDER_FEMALE BERAT 16", aData);
        aData = new ArrayList<>();aData.add(73.0);aData.add(87.0);ref.put("GENDER_FEMALE TINGGI 16", aData);
        aData = new ArrayList<>();aData.add(44.4);aData.add(47.3);ref.put("GENDER_FEMALE KEPALA 16", aData);
        aData = new ArrayList<>();aData.add(8.0);aData.add(11.4);ref.put("GENDER_FEMALE BERAT 17", aData);
        aData = new ArrayList<>();aData.add(74.0);aData.add(88.2);ref.put("GENDER_FEMALE TINGGI 17", aData);
        aData = new ArrayList<>();aData.add(44.6);aData.add(47.5);ref.put("GENDER_FEMALE KEPALA 17", aData);
        aData = new ArrayList<>();aData.add(8.2);aData.add(11.6);ref.put("GENDER_FEMALE BERAT 18", aData);
        aData = new ArrayList<>();aData.add(74.9);aData.add(89.4);ref.put("GENDER_FEMALE TINGGI 18", aData);
        aData = new ArrayList<>();aData.add(44.8);aData.add(47.7);ref.put("GENDER_FEMALE KEPALA 18", aData);
        aData = new ArrayList<>();aData.add(8.3);aData.add(11.9);ref.put("GENDER_FEMALE BERAT 19", aData);
        aData = new ArrayList<>();aData.add(75.8);aData.add(90.6);ref.put("GENDER_FEMALE TINGGI 19", aData);
        aData = new ArrayList<>();aData.add(45.0);aData.add(47.8);ref.put("GENDER_FEMALE KEPALA 19", aData);
        aData = new ArrayList<>();aData.add(8.5);aData.add(12.1);ref.put("GENDER_FEMALE BERAT 20", aData);
        aData = new ArrayList<>();aData.add(76.7);aData.add(91.7);ref.put("GENDER_FEMALE TINGGI 20", aData);
        aData = new ArrayList<>();aData.add(45.1);aData.add(48.0);ref.put("GENDER_FEMALE KEPALA 20", aData);
        aData = new ArrayList<>();aData.add(8.7);aData.add(12.4);ref.put("GENDER_FEMALE BERAT 21", aData);
        aData = new ArrayList<>();aData.add(77.5);aData.add(92.9);ref.put("GENDER_FEMALE TINGGI 21", aData);
        aData = new ArrayList<>();aData.add(45.3);aData.add(48.2);ref.put("GENDER_FEMALE KEPALA 21", aData);
        aData = new ArrayList<>();aData.add(8.8);aData.add(12.6);ref.put("GENDER_FEMALE BERAT 22", aData);
        aData = new ArrayList<>();aData.add(78.4);aData.add(94.0);ref.put("GENDER_FEMALE TINGGI 22", aData);
        aData = new ArrayList<>();aData.add(45.4);aData.add(48.3);ref.put("GENDER_FEMALE KEPALA 22", aData);
        aData = new ArrayList<>();aData.add(9.0);aData.add(12.8);ref.put("GENDER_FEMALE BERAT 23", aData);
        aData = new ArrayList<>();aData.add(79.2);aData.add(95.0);ref.put("GENDER_FEMALE TINGGI 23", aData);
        aData = new ArrayList<>();aData.add(45.6);aData.add(48.5);ref.put("GENDER_FEMALE KEPALA 23", aData);
        aData = new ArrayList<>();aData.add(9.2);aData.add(13.1);ref.put("GENDER_FEMALE BERAT 24", aData);
        aData = new ArrayList<>();aData.add(80.0);aData.add(96.1);ref.put("GENDER_FEMALE TINGGI 24", aData);
        aData = new ArrayList<>();aData.add(45.7);aData.add(48.6);ref.put("GENDER_FEMALE KEPALA 24", aData);
        aData = new ArrayList<>();aData.add(9.3);aData.add(13.3);ref.put("GENDER_FEMALE BERAT 25", aData);
        aData = new ArrayList<>();aData.add(80.0);aData.add(96.4);ref.put("GENDER_FEMALE TINGGI 25", aData);
        aData = new ArrayList<>();aData.add(45.9);aData.add(48.8);ref.put("GENDER_FEMALE KEPALA 25", aData);
        aData = new ArrayList<>();aData.add(9.5);aData.add(13.6);ref.put("GENDER_FEMALE BERAT 26", aData);
        aData = new ArrayList<>();aData.add(80.8);aData.add(97.4);ref.put("GENDER_FEMALE TINGGI 26", aData);
        aData = new ArrayList<>();aData.add(46.0);aData.add(48.9);ref.put("GENDER_FEMALE KEPALA 26", aData);
        aData = new ArrayList<>();aData.add(9.6);aData.add(13.8);ref.put("GENDER_FEMALE BERAT 27", aData);
        aData = new ArrayList<>();aData.add(81.5);aData.add(98.4);ref.put("GENDER_FEMALE TINGGI 27", aData);
        aData = new ArrayList<>();aData.add(46.1);aData.add(49.0);ref.put("GENDER_FEMALE KEPALA 27", aData);
        aData = new ArrayList<>();aData.add(9.8);aData.add(14.0);ref.put("GENDER_FEMALE BERAT 28", aData);
        aData = new ArrayList<>();aData.add(82.2);aData.add(99.4);ref.put("GENDER_FEMALE TINGGI 28", aData);
        aData = new ArrayList<>();aData.add(46.3);aData.add(49.2);ref.put("GENDER_FEMALE KEPALA 28", aData);
        aData = new ArrayList<>();aData.add(10.0);aData.add(14.3);ref.put("GENDER_FEMALE BERAT 29", aData);
        aData = new ArrayList<>();aData.add(82.9);aData.add(100.3);ref.put("GENDER_FEMALE TINGGI 29", aData);
        aData = new ArrayList<>();aData.add(46.4);aData.add(49.3);ref.put("GENDER_FEMALE KEPALA 29", aData);
        aData = new ArrayList<>();aData.add(10.1);aData.add(14.5);ref.put("GENDER_FEMALE BERAT 30", aData);
        aData = new ArrayList<>();aData.add(83.6);aData.add(101.3);ref.put("GENDER_FEMALE TINGGI 30", aData);
        aData = new ArrayList<>();aData.add(46.5);aData.add(49.4);ref.put("GENDER_FEMALE KEPALA 30", aData);
        aData = new ArrayList<>();aData.add(10.3);aData.add(14.7);ref.put("GENDER_FEMALE BERAT 31", aData);
        aData = new ArrayList<>();aData.add(84.3);aData.add(102.2);ref.put("GENDER_FEMALE TINGGI 31", aData);
        aData = new ArrayList<>();aData.add(46.6);aData.add(49.5);ref.put("GENDER_FEMALE KEPALA 31", aData);
        aData = new ArrayList<>();aData.add(10.4);aData.add(15.0);ref.put("GENDER_FEMALE BERAT 32", aData);
        aData = new ArrayList<>();aData.add(84.9);aData.add(103.1);ref.put("GENDER_FEMALE TINGGI 32", aData);
        aData = new ArrayList<>();aData.add(46.7);aData.add(49.6);ref.put("GENDER_FEMALE KEPALA 32", aData);
        aData = new ArrayList<>();aData.add(10.5);aData.add(15.2);ref.put("GENDER_FEMALE BERAT 33", aData);
        aData = new ArrayList<>();aData.add(85.6);aData.add(103.9);ref.put("GENDER_FEMALE TINGGI 33", aData);
        aData = new ArrayList<>();aData.add(46.8);aData.add(49.7);ref.put("GENDER_FEMALE KEPALA 33", aData);
        aData = new ArrayList<>();aData.add(10.7);aData.add(15.4);ref.put("GENDER_FEMALE BERAT 34", aData);
        aData = new ArrayList<>();aData.add(86.2);aData.add(104.8);ref.put("GENDER_FEMALE TINGGI 34", aData);
        aData = new ArrayList<>();aData.add(46.9);aData.add(49.8);ref.put("GENDER_FEMALE KEPALA 34", aData);
        aData = new ArrayList<>();aData.add(10.8);aData.add(15.7);ref.put("GENDER_FEMALE BERAT 35", aData);
        aData = new ArrayList<>();aData.add(86.8);aData.add(105.6);ref.put("GENDER_FEMALE TINGGI 35", aData);
        aData = new ArrayList<>();aData.add(47.0);aData.add(49.9);ref.put("GENDER_FEMALE KEPALA 35", aData);
        aData = new ArrayList<>();aData.add(11.0);aData.add(15.9);ref.put("GENDER_FEMALE BERAT 36", aData);
        aData = new ArrayList<>();aData.add(87.4);aData.add(106.5);ref.put("GENDER_FEMALE TINGGI 36", aData);
        aData = new ArrayList<>();aData.add(47.0);aData.add(50.0);ref.put("GENDER_FEMALE KEPALA 36", aData);
        aData = new ArrayList<>();aData.add(11.1);aData.add(16.1);ref.put("GENDER_FEMALE BERAT 37", aData);
        aData = new ArrayList<>();aData.add(88.0);aData.add(107.3);ref.put("GENDER_FEMALE TINGGI 37", aData);
        aData = new ArrayList<>();aData.add(47.1);aData.add(50.1);ref.put("GENDER_FEMALE KEPALA 37", aData);
        aData = new ArrayList<>();aData.add(11.2);aData.add(16.3);ref.put("GENDER_FEMALE BERAT 38", aData);
        aData = new ArrayList<>();aData.add(88.6);aData.add(108.1);ref.put("GENDER_FEMALE TINGGI 38", aData);
        aData = new ArrayList<>();aData.add(47.2);aData.add(50.1);ref.put("GENDER_FEMALE KEPALA 38", aData);
        aData = new ArrayList<>();aData.add(11.4);aData.add(16.6);ref.put("GENDER_FEMALE BERAT 39", aData);
        aData = new ArrayList<>();aData.add(89.2);aData.add(108.9);ref.put("GENDER_FEMALE TINGGI 39", aData);
        aData = new ArrayList<>();aData.add(47.3);aData.add(50.2);ref.put("GENDER_FEMALE KEPALA 39", aData);
        aData = new ArrayList<>();aData.add(11.5);aData.add(16.8);ref.put("GENDER_FEMALE BERAT 40", aData);
        aData = new ArrayList<>();aData.add(89.8);aData.add(109.7);ref.put("GENDER_FEMALE TINGGI 40", aData);
        aData = new ArrayList<>();aData.add(47.4);aData.add(50.3);ref.put("GENDER_FEMALE KEPALA 40", aData);
        aData = new ArrayList<>();aData.add(11.6);aData.add(17.0);ref.put("GENDER_FEMALE BERAT 41", aData);
        aData = new ArrayList<>();aData.add(90.4);aData.add(110.5);ref.put("GENDER_FEMALE TINGGI 41", aData);
        aData = new ArrayList<>();aData.add(47.4);aData.add(50.4);ref.put("GENDER_FEMALE KEPALA 41", aData);
        aData = new ArrayList<>();aData.add(11.8);aData.add(17.3);ref.put("GENDER_FEMALE BERAT 42", aData);
        aData = new ArrayList<>();aData.add(90.9);aData.add(111.2);ref.put("GENDER_FEMALE TINGGI 42", aData);
        aData = new ArrayList<>();aData.add(47.5);aData.add(50.4);ref.put("GENDER_FEMALE KEPALA 42", aData);
        aData = new ArrayList<>();aData.add(11.9);aData.add(17.5);ref.put("GENDER_FEMALE BERAT 43", aData);
        aData = new ArrayList<>();aData.add(91.5);aData.add(112.0);ref.put("GENDER_FEMALE TINGGI 43", aData);
        aData = new ArrayList<>();aData.add(47.6);aData.add(50.5);ref.put("GENDER_FEMALE KEPALA 43", aData);
        aData = new ArrayList<>();aData.add(12.0);aData.add(17.7);ref.put("GENDER_FEMALE BERAT 44", aData);
        aData = new ArrayList<>();aData.add(92.0);aData.add(112.7);ref.put("GENDER_FEMALE TINGGI 44", aData);
        aData = new ArrayList<>();aData.add(47.6);aData.add(50.6);ref.put("GENDER_FEMALE KEPALA 44", aData);
        aData = new ArrayList<>();aData.add(12.1);aData.add(17.9);ref.put("GENDER_FEMALE BERAT 45", aData);
        aData = new ArrayList<>();aData.add(92.5);aData.add(113.5);ref.put("GENDER_FEMALE TINGGI 45", aData);
        aData = new ArrayList<>();aData.add(47.7);aData.add(50.6);ref.put("GENDER_FEMALE KEPALA 45", aData);
        aData = new ArrayList<>();aData.add(12.3);aData.add(18.2);ref.put("GENDER_FEMALE BERAT 46", aData);
        aData = new ArrayList<>();aData.add(93.1);aData.add(114.2);ref.put("GENDER_FEMALE TINGGI 46", aData);
        aData = new ArrayList<>();aData.add(47.7);aData.add(50.7);ref.put("GENDER_FEMALE KEPALA 46", aData);
        aData = new ArrayList<>();aData.add(12.4);aData.add(18.4);ref.put("GENDER_FEMALE BERAT 47", aData);
        aData = new ArrayList<>();aData.add(93.6);aData.add(114.9);ref.put("GENDER_FEMALE TINGGI 47", aData);
        aData = new ArrayList<>();aData.add(47.8);aData.add(50.7);ref.put("GENDER_FEMALE KEPALA 47", aData);
        aData = new ArrayList<>();aData.add(12.5);aData.add(18.6);ref.put("GENDER_FEMALE BERAT 48", aData);
        aData = new ArrayList<>();aData.add(94.1);aData.add(115.7);ref.put("GENDER_FEMALE TINGGI 48", aData);
        aData = new ArrayList<>();aData.add(47.9);aData.add(50.8);ref.put("GENDER_FEMALE KEPALA 48", aData);
        aData = new ArrayList<>();aData.add(12.6);aData.add(18.9);ref.put("GENDER_FEMALE BERAT 49", aData);
        aData = new ArrayList<>();aData.add(94.6);aData.add(116.4);ref.put("GENDER_FEMALE TINGGI 49", aData);
        aData = new ArrayList<>();aData.add(47.9);aData.add(50.9);ref.put("GENDER_FEMALE KEPALA 49", aData);
        aData = new ArrayList<>();aData.add(12.8);aData.add(19.1);ref.put("GENDER_FEMALE BERAT 50", aData);
        aData = new ArrayList<>();aData.add(95.1);aData.add(117.1);ref.put("GENDER_FEMALE TINGGI 50", aData);
        aData = new ArrayList<>();aData.add(48.0);aData.add(50.9);ref.put("GENDER_FEMALE KEPALA 50", aData);
        aData = new ArrayList<>();aData.add(12.9);aData.add(19.3);ref.put("GENDER_FEMALE BERAT 51", aData);
        aData = new ArrayList<>();aData.add(95.6);aData.add(117.7);ref.put("GENDER_FEMALE TINGGI 51", aData);
        aData = new ArrayList<>();aData.add(48.0);aData.add(51.0);ref.put("GENDER_FEMALE KEPALA 51", aData);
        aData = new ArrayList<>();aData.add(13.0);aData.add(19.5);ref.put("GENDER_FEMALE BERAT 52", aData);
        aData = new ArrayList<>();aData.add(96.1);aData.add(118.4);ref.put("GENDER_FEMALE TINGGI 52", aData);
        aData = new ArrayList<>();aData.add(48.1);aData.add(51.0);ref.put("GENDER_FEMALE KEPALA 52", aData);
        aData = new ArrayList<>();aData.add(13.1);aData.add(19.8);ref.put("GENDER_FEMALE BERAT 53", aData);
        aData = new ArrayList<>();aData.add(96.6);aData.add(119.1);ref.put("GENDER_FEMALE TINGGI 53", aData);
        aData = new ArrayList<>();aData.add(48.1);aData.add(51.1);ref.put("GENDER_FEMALE KEPALA 53", aData);
        aData = new ArrayList<>();aData.add(13.2);aData.add(20.0);ref.put("GENDER_FEMALE BERAT 54", aData);
        aData = new ArrayList<>();aData.add(97.1);aData.add(119.8);ref.put("GENDER_FEMALE TINGGI 54", aData);
        aData = new ArrayList<>();aData.add(48.2);aData.add(51.1);ref.put("GENDER_FEMALE KEPALA 54", aData);
        aData = new ArrayList<>();aData.add(13.4);aData.add(20.2);ref.put("GENDER_FEMALE BERAT 55", aData);
        aData = new ArrayList<>();aData.add(97.6);aData.add(120.4);ref.put("GENDER_FEMALE TINGGI 55", aData);
        aData = new ArrayList<>();aData.add(48.2);aData.add(51.2);ref.put("GENDER_FEMALE KEPALA 55", aData);
        aData = new ArrayList<>();aData.add(13.5);aData.add(20.4);ref.put("GENDER_FEMALE BERAT 56", aData);
        aData = new ArrayList<>();aData.add(98.1);aData.add(121.1);ref.put("GENDER_FEMALE TINGGI 56", aData);
        aData = new ArrayList<>();aData.add(48.3);aData.add(51.2);ref.put("GENDER_FEMALE KEPALA 56", aData);
        aData = new ArrayList<>();aData.add(13.6);aData.add(20.7);ref.put("GENDER_FEMALE BERAT 57", aData);
        aData = new ArrayList<>();aData.add(98.5);aData.add(121.8);ref.put("GENDER_FEMALE TINGGI 57", aData);
        aData = new ArrayList<>();aData.add(48.3);aData.add(51.3);ref.put("GENDER_FEMALE KEPALA 57", aData);
        aData = new ArrayList<>();aData.add(13.7);aData.add(20.9);ref.put("GENDER_FEMALE BERAT 58", aData);
        aData = new ArrayList<>();aData.add(99.0);aData.add(122.4);ref.put("GENDER_FEMALE TINGGI 58", aData);
        aData = new ArrayList<>();aData.add(48.4);aData.add(51.3);ref.put("GENDER_FEMALE KEPALA 58", aData);
        aData = new ArrayList<>();aData.add(13.8);aData.add(21.1);ref.put("GENDER_FEMALE BERAT 59", aData);
        aData = new ArrayList<>();aData.add(99.5);aData.add(123.1);ref.put("GENDER_FEMALE TINGGI 59", aData);
        aData = new ArrayList<>();aData.add(48.4);aData.add(51.4);ref.put("GENDER_FEMALE KEPALA 59", aData);
        aData = new ArrayList<>();aData.add(14.0);aData.add(21.3);ref.put("GENDER_FEMALE BERAT 60", aData);
        aData = new ArrayList<>();aData.add(99.9);aData.add(123.7);ref.put("GENDER_FEMALE TINGGI 60", aData);
        aData = new ArrayList<>();aData.add(48.4);aData.add(51.4);ref.put("GENDER_FEMALE KEPALA 60", aData);

        key = key.replace(".0", "");
        Log.d(DEBUG_TAG, "getBalitaStatus: " + key);
        ArrayList<Double> cek = ref.get(key);
        String r = "";
        if(value != null) {
            r = "Normal";
            if (value > cek.get(1)) {
                r = "Berlebih";
            }
            if (value < cek.get(0)) {
                r = "Kurang";
            }
        }

        ret.add(r);
        ret.add("" + cek.get(0) + " " + cek.get(1));
        return ret;
    }


    public static void browserIntent(Activity context, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

}
