package com.jawnnypoo.openmeh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jawnnypoo.openmeh.R;

public class MehActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
