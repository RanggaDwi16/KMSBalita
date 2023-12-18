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

    public static String BASE_URL = "http://192.168.0.146:5004";

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
        if(true){
            ret.add("Normal");
            ret.add("1.1 2.2");
            return ret;
        }

        HashMap<String, ArrayList<Double>> ref = new HashMap<>();
        ArrayList<Double> aData;

        // berat
        aData = new ArrayList<>();aData.add(1.0);aData.add(2.0);ref.put("GENDER_MALE BERAT 1", aData);
        aData = new ArrayList<>();aData.add(1.0);aData.add(1.0);ref.put("GENDER_MALE BERAT 2", aData);

        ArrayList<Double> cek = ref.get(key);
        String r = "";
        if(value != null) {
            r = "Normal";
            if (value > cek.get(1)) {
                r = "Overnormal";
            }
            if (value < cek.get(0)) {
                r = "Undernormal";
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
