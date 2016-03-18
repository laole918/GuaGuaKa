package com.laole918.guaguaka.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.laole918.guaguaka.widget.GuaGuaKaFrameLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GuaGuaKaFrameLayout.OnWipeListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private GuaGuaKaFrameLayout ggkFrameLayout;
    private Button btnViewDesc;
    private Button btnWipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ggkFrameLayout = (GuaGuaKaFrameLayout) findViewById(R.id.ggk_frameLayout);
        ggkFrameLayout.setOnWipeListener(this);
        btnViewDesc = (Button) findViewById(R.id.btn_view_desc);
        btnViewDesc.setOnClickListener(this);
        btnWipe = (Button) findViewById(R.id.btn_wipe);
        btnWipe.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnViewDesc)) {
            Log.e(TAG, "你点击了查看详情按钮");
        } else if(v.equals(btnWipe)) {
            ggkFrameLayout.wipeGgkForeground();
        }
    }

    @Override
    public void onWipeClean() {
        Log.e(TAG, "清除干净了");
    }
}
