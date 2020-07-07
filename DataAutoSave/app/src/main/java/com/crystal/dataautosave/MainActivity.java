package com.crystal.dataautosave;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.annotationlib.AutoSave;
import com.crystal.dastools.DataAutoSaveTools;
import com.crystal.dastools.PreferenceTools;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    @AutoSave
    public String testField = "";
    @AutoSave
    public Ch[] testCh;

    @AutoSave(Persistence = true)
    public String[] array=new String[]{"hvjgb","jkbh"};
    @AutoSave(Persistence = true)
    public HashMap<String, String> hashMap =new HashMap<>();
    @AutoSave
    public Set<String> stringSet;
    @AutoSave
    public List<String> stringList;
    @AutoSave
    public List<Integer> integerList;
    @AutoSave
    public List<Long> longList;

    TextView textView;
    Button button;
    Button button2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hashMap.put("kjsd","olll");
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        textView.setText(testField);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int max = 1000;
                int min = 1;
                testField = String.valueOf(Math.random() * (max - min) + min);
                textView.setText(testField);
                //若是不经onSaveInstanceState(),比如旋转屏幕之类的，先点击这里改变数据，是不会保存数据的，因为这里的bundle是null
                DataAutoSaveTools.saveData(MainActivity.this, savedInstanceState, false);
            }
        });

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击恢复数据
                DataAutoSaveTools.restoreData(MainActivity.this, savedInstanceState, false);
                textView.setText(testField);

            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        textView.setText(testField);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "原始数据是:  " + testField, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存数据，若不是在这里最先保存数据，那么必须保证传入的bundle不为null
        DataAutoSaveTools.saveData(MainActivity.this, outState, false);
    }

}
