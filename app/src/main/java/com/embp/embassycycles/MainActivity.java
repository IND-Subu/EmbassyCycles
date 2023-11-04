package com.embp.embassycycles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    // WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // webView = findViewById(R.id.webView);
        // WebSettings webSettings = webView.getSettings();
        // webSettings.setJavaScriptEnabled(true);
        // webSettings.setDatabaseEnabled(true);
        // webSettings.setDomStorageEnabled(true);
        // Set a WebViewClient to handle page navigation
        // webView.setWebViewClient(new WebViewClient());
    }

    public void scanQRCode(android.view.View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String qrCodeData = result.getContents();
            String customUrl = "https://frand.000webhostapp.com/ecycle";
            if (qrCodeData != null) {
                // Load the custom URL in a new activity
                Intent webViewIntent = new Intent(this, WebViewActivity.class);
                webViewIntent.putExtra("url", customUrl);
                startActivity(webViewIntent);
            } else {
                Toast.makeText(this, "QR code data is empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}


