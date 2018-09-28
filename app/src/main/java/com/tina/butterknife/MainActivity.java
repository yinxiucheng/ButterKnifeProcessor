package com.tina.butterknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.tina.annotation.BindView;

import tina.com.lib.Bind;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textId)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bind.bind(this);

        textView.setText("哈哈");
    }
}
