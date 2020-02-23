package com.johnyang.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        textView =(TextView)findViewById(R.id.startText);
        button = (Button)findViewById(R.id.changeButton);
        button.setOnClickListener(new changeButtonClickListener());
    }

    class changeButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }
}
