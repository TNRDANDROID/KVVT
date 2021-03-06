package com.nic.KVVTSurvey.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.nic.KVVTSurvey.BuildConfig;
import com.nic.KVVTSurvey.R;
import com.nic.KVVTSurvey.databinding.SplashScreenBinding;
import com.nic.KVVTSurvey.helper.AppVersionHelper;
import com.nic.KVVTSurvey.session.PrefManager;
import com.nic.KVVTSurvey.utils.Utils;


public class SplashScreen extends AppCompatActivity implements
        AppVersionHelper.myAppVersionInterface {
    private TextView textView;
    private Button button;
    private static int SPLASH_TIME_OUT = 2000;
    private PrefManager prefManager;
    public SplashScreenBinding splashScreenBinding;
    String localLanguage ="ta";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashScreenBinding = DataBindingUtil.setContentView(this, R.layout.splash_screen);
        splashScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("production")) {
            if (Utils.isOnline()) {
                checkAppVersion();
                //showSignInScreen();
            } else {
                showSignInScreen();

            }
        } else {
            //showSignInScreen();
            checkAppVersion();
        }
    }


    private void showSignInScreen() {
        Utils.setLocale(localLanguage,this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(SplashScreen.this, LoginScreen.class);

                startActivity(i);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }, SPLASH_TIME_OUT);
    }

    private void checkAppVersion() {
        new AppVersionHelper(this, SplashScreen.this).callAppVersionCheckApi();
    }

    @Override
    public void onAppVersionCallback(String value) {
        if (value.length() > 0 && "Update".equalsIgnoreCase(value)) {
            startActivity(new Intent(this, AppUpdateDialog.class));
            finish();
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else {
            showSignInScreen();
        }

    }

}
