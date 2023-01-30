package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private long PauseOffSet = 0;
    private boolean isPlaying = false;
    private ToggleButton toggleButton;
    private Button reset_btn;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
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

        FirebaseRemoteConfig fire = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        fire.setConfigSettingsAsync(configSettings);

        fire.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            String ope = fire.getString("url");
                            String brand = Build.BRAND;
                            url = ope;
                        }
                    }
                });

        WebView myWebView = findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.loadUrl(url);

        WebViewClient webViewClient = new WebViewClient() {
            @SuppressWarnings("deprecation") @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N) @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        };

        myWebView.setWebViewClient(webViewClient);
    }
}