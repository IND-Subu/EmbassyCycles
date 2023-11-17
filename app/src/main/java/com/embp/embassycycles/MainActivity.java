package com.embp.embassycycles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            String customUrl = "https://embp.000webhostapp.com";
            if (qrCodeData != null) {
                if (validateQRCodeData(qrCodeData)) {
                    // Load the custom URL in a new activity
                    Intent webViewIntent = new Intent(this, WebViewActivity.class);
                    webViewIntent.putExtra("url", customUrl);
                    startActivity(webViewIntent);
                } else {
                    // Prompt the user to scan again
                    Toast.makeText(this, "Invalid QR code. Please scan again.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    // Define your custom validation condition
    private boolean validateQRCodeData(String qrCodeData) {
        return qrCodeData != null && qrCodeData.startsWith("https://embp.000webhostapp.com");
    }
}


