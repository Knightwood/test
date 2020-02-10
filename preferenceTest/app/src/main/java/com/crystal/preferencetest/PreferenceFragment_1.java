package com.crystal.preferencetest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/9{TIME}
 */
public class PreferenceFragment_1 extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_1);

        checkbox_set_value();
        edit2();
        edit3();
        startactivity();
        openWeb();
        clickmethod();
        readData();
    }

    private void readData() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name = sharedPreferences.getString("data1", "");

        Log.d("datasave",name);
    }

    /**
     * 这个利用了PreferenceDataStore
     */
    private void readData2(){
        DataStore store=new DataStore();
        EditTextPreference edit3=findPreference("data2");
        if (edit3!=null){
            edit3.setPreferenceDataStore(store);
        }
    }

    private void clickmethod() {
        Preference clickm=findPreference("click");
        clickm.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity(),"点击了",Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    /**
     * 第二种设置启动activity的方法
     */
    private void startactivity() {
        Preference startActivity=findPreference("startActivity2");
        Intent intent=new Intent(getContext(),secondActivity.class);
        startActivity.setIntent(intent);
    }
private void openWeb(){
        Preference web=findPreference("webview2");

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse("http://www.bing.com"));
    web.setIntent(intent);

}
    private void checkbox_set_value() {
        ListPreference list1=findPreference("列表");
        list1.setValueIndex(1);//设置entries的值
        Toast.makeText(getActivity(),list1.getValue(),Toast.LENGTH_LONG).show();

        if (list1.getValue().contains("22")){
            //getValue,获取当前选中的entries的值（entryValues），返回的是一个String类型
            CheckBoxPreference checkBox=findPreference("switch");

            if (checkBox != null) {
                checkBox.setVisible(true);
            }
        }
    }

    private void edit2() {
        //第二种方法启用summaryProvider
//自己控制summaryProvider
        EditTextPreference edit2=findPreference("bmji2");
        if (edit2!=null){
            //这个是默认的设置summaryProvider
            //edit2.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

            //这个是自己重写方法控制summaryProvider
            edit2.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                @Override
                public CharSequence provideSummary(EditTextPreference preference) {
                 String text=preference.getText();
                 if (text.isEmpty()){
                     //神奇，string竟然和c里一样可以看做字符数组
                     return "没有字符串";
                 }else
                    return "字符串长度"+text.length();
                }
            });
        }
    }

    private void edit3() {
        //文本编辑偏好，控制输入类型
        EditTextPreference edit3=findPreference("bmji3");
        if (edit3!=null){
            edit3.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                }
            });
        }
    }

    /**
     * 多线程，没啥用，只是随便写的
     * 注：调用start方法方可启动线程，而run方法只是thread类中的一个普通方法调用，还是在主线程里执行。
     */
    private void hehe() {
        //1
        tmp w= new tmp();
        w.start();

        //2
        tmp2 ww=new tmp2();
        Thread rr=new Thread(ww);
        rr.run();
    }


    class tmp extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    class tmp2 implements Runnable{

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class DataStore extends PreferenceDataStore{
        @Override
        public void putString(String key, @Nullable String value) {
            super.putString(key, value);
        }

        @Nullable
        @Override
        public String getString(String key, @Nullable String defValue) {
            return super.getString(key, defValue);
        }
    }


}
