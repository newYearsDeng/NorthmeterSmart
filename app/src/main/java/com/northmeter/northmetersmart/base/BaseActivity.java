package com.northmeter.northmetersmart.base;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by dyd on 2018/12/5.
 */

public class BaseActivity extends AppCompatActivity implements IBaseListener{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        BaseAppManager.getAppManager().addActivity(this);

    }


    @Override
    public void startView() {
        initData();
        initView();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

    }


}
