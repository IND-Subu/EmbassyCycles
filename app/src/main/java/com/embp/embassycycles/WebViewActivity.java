package com.embp.embassycycles;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.webkit.JavascriptInterface;
import android.content.Context;
import android.webkit.WebChromeClient;


public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webView.setWebContentsDebuggingEnabled(true);


        // Add a JavaScript interface to interact with the WebView
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");


        String url = getIntent().getStringExtra("url");
         if (url != null) {
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        } else {
            Toast.makeText(this, "URL not provided", Toast.LENGTH_SHORT).show();
        }
    }

    // Create a JavaScript interface
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        // This method can be called from JavaScript
        @JavascriptInterface
        public void showToast(String message) {
            if (message != null){
                Toast.makeText(mContext, "Check "+message+" Successfull", Toast.LENGTH_SHORT).show();
              finish();
            }
            else {
                Toast.makeText(mContext, "Something went wrong. Please re-open the App", Toast.LENGTH_LONG).show();
            }
        }
    }
}