package com.embp.embassycycles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.embp.embassycycles.R;


public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        WebSettings webSettings = webView.getSettings();
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setDomStorageEnabled(true);


        // Add a JavaScript interface to interact with the WebView
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");

        // Add a WebViewClient to Handle SSL errors
        webView.setWebViewClient(new WebViewClient());
        //webView.setWebViewClient(new SSLTolerantWebViewClient());

        // Load the url into yhe WebView
        String url = getIntent().getStringExtra("url");
        String location = getIntent().getStringExtra("location");
        if (url != null) {
            webView.setWebChromeClient(new WebChromeClient());
            webView.loadUrl(url);
        } else {
            Toast.makeText(this, "URL not provided", Toast.LENGTH_SHORT).show();
        }
    }

    // WebViewClient to handle ssl errors and proceed with loading content
    /*private static class SSLTolerantWebViewClient extends WebViewClient{
        @SuppressLint("WebViewClientOnReceivedSslError")
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }*/

    // Create a JavaScript interface
    public class WebAppInterface {
        Context context;
        WebAppInterface(Context c) {
            context = c;
        }

        // This method can be called from JavaScript
        @JavascriptInterface
        public String location(){
            return getIntent().getStringExtra("location");
        }

        @JavascriptInterface
        public void goToMainActivity(String response, String empName, String empId, String empBlock, String empCompany, String empStatus) {
        //public void goToMainActivity(String... response) {
            if (response != null) {
                Log.d("response", "MSG: "+response);
                if (response.contains("Checked in") || response.contains("Checked out")){
                    String[] intentKeys = {"response", "empName", "empId", "empBlock", "empCompany", "empStatus"};
                    String[] intentValues = {response, empName, empId, empBlock, empCompany, empStatus};

                    SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Start the MainActivity
                    Intent intent = new Intent(context, MainActivity.class);
                    for (int i = 0; i< intentKeys.length; i++){
                        intent.putExtra(intentKeys[0], intentValues[0]);
                        editor.putString(intentKeys[i], intentValues[i]);
                    }
                    editor.apply();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
            else {
                try {
                    Log.d("Error", "Error: " + response);
                    Toast.makeText(context, "Error: " + response, Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    Toast.makeText(context, "Error: "+e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }
}
