package com.embp.embassycycles;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.embp.embassycycles.databinding.*;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    private static final String ADMOB_APP_ID = "ca-app-pub-1060037193333326~3866249275";
    ActionBarDrawerToggle actionBarDrawerToggle;
    ActivityMainBinding mainBinding;
    Toolbar toolbar;
    AdView adView;
    ImageView warningSign, checkMark;
    TextView appDesc, showStatusString, userName, empId, empBlock, empCompany, empStatus;
    String [] loginStatus;
    String response;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        // Inflating HeaderView
        NavigationView navigationView = mainBinding.navigationView;
        View headerView = navigationView.getHeaderView(0);
        NavHeaderLayoutBinding headerBinding = NavHeaderLayoutBinding.bind(headerView);

        // Initialize AdView
        MobileAds.initialize(this);
        adView = mainBinding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        toolbar = mainBinding.toolbar;
        setSupportActionBar(toolbar);

        drawerLayout = mainBinding.drawerLayout;
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        warningSign = mainBinding.warningSign;
        checkMark = mainBinding.checkMark;
        appDesc = mainBinding.appDesc;
        showStatusString = mainBinding.showStatusString;
        userName = headerBinding.userName;
        empId = headerBinding.empId;
        empBlock = headerBinding.empBlock;
        empCompany = headerBinding.empCompany;
        empStatus = headerBinding.empStatus;

        Intent intent = getIntent();
        response = intent.getStringExtra("response");
        if (response != null) {
            if (response.contains("checkout")) {
                response = "Checked Out";
            } else if (response.contains("checkin") || response.contains("check in")) {
                response = "Checked In";
            }
        }
        loginStatus = getIntentDataFromWebView();
        showTextPrompts();

    }

    private void showTextPrompts(){
        if (loginStatus[4].isEmpty()){
            appDesc.setText(R.string.app_desc);
        }
        else if(loginStatus[4].contains("Checked Out")){
            showStatusString.setText(R.string.already_checkedIn_alert);
            showStatusString.setTextColor(Color.RED);
            warningSign.setVisibility(View.VISIBLE);
        } else if (response != null && !response.isEmpty()) {
            showStatusString.setText(response);
            checkMark.setVisibility(View.VISIBLE);
        }
        else {
            appDesc.setText(R.string.app_desc);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        showTextPrompts();
        // Override specific behavior for onResume()
        if (response != null && !response.isEmpty()) {
            showStatusString.setText(response);
            showStatusString.setTextColor(Color.GREEN);
            checkMark.setVisibility(View.VISIBLE);
        }
        String[] intentArr = getIntentDataFromWebView();
        TextView [] headerTextViews = {userName, empId, empBlock, empCompany, empStatus};

        for (int i = 0; i < headerTextViews.length; i++) {
            headerTextViews[i].setText(intentArr[i]);
        }
    }

    private String[] getIntentDataFromWebView(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return new String[]{sharedPreferences.getString("empName", ""), sharedPreferences.getString("empId", ""), sharedPreferences.getString("empBlock", ""), sharedPreferences.getString("empCompany", ""), sharedPreferences.getString("empStatus", "")};
    }

    public void scanQRCode(View view) {
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
            if (qrCodeData != null) {
                if (validateQRCodeData(qrCodeData)) {
                    // Load the custom URL in a new activity
                    Intent webViewIntent = new Intent(this, WebViewActivity.class);
                    webViewIntent.putExtra("url", "https://embp1.000webhostapp.com/app");
                    if (qrCodeData.contains("anzjunction")){
                        webViewIntent.putExtra("location", "ANZ Junction");
                    } else if (qrCodeData.contains("PlazaArea")) {
                        webViewIntent.putExtra("location", "Plaza Area");
                    }
                    else if (qrCodeData.contains("centralpark")) {
                        webViewIntent.putExtra("location", "Central Park");
                    }
                    else if (qrCodeData.contains("l5junction")) {
                        webViewIntent.putExtra("location", "L5 Junction");
                    }
                    else {
                        webViewIntent.putExtra("location", "null");
                    }
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
        return qrCodeData != null && qrCodeData.startsWith("https://embp1");
    }
}


