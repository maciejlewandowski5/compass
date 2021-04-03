package com.example.ngcompass;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ngcompass.mainactivity.MainActivityView;

public class MainActivity extends AppCompatActivity implements MainActivityView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}