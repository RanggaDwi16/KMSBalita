package com.example.kmsapp.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.example.kmsapp.LoginPage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

}
