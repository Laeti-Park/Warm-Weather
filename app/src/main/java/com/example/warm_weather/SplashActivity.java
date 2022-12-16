package com.example.warm_weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.warm_weather.databinding.ActivitySplashBinding;

/**
 * SplashActivity : 앱 시작 시 띄우는 화면
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends Activity {
    private ActivitySplashBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler hand = new Handler(); // 시간 지연으로 처리 천천히 화면 전환

        hand.postDelayed(() -> { // 인텐드로 화면 전환
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // 화면 부드럽게 전환
            finish();

        }, 2000);
    }
}