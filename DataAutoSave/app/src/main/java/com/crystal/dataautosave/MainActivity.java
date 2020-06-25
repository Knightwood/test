package com.crystal.dataautosave;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.crystal.annotationlib.AutoSave;
import com.crystal.dastools.DASinterface;

public class MainActivity extends AppCompatActivity {

    @AutoSave
    public String testField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testField = "123";
    }
}
