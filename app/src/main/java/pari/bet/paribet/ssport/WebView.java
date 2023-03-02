package pari.bet.paribet.ssport;

import android.os.Bundle;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebView extends AppCompatActivity {

    private android.webkit.WebView myWebView;
    private String URL;

    public WebView() {
    }

    public WebView(String URL) {
        this.URL = URL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myWebView = findViewById(R.id.webview);
        setContentView(myWebView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        WebViewClient webViewClient = new WebViewClient() {
            @SuppressWarnings("deprecation") @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                return false;
            }
        };

        myWebView.setWebViewClient(webViewClient);
        myWebView.loadUrl(URL);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myWebView.goBack();
    }
}