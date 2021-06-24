package com.igordutrasanches.perfectscan;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       try{
           setContentView(R.layout.activity_main);
       }catch (Exception x){
        Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
       }
    }
}