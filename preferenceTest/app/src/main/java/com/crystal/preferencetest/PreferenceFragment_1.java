package com.crystal.preferencetest;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceFragmentCompat;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/9{TIME}
 */
public class PreferenceFragment_1 extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_1);

       /* tmp w= new tmp();
        w.start();*/

            /*CheckBoxPreference checkBox=findPreference("emmm");

            if (checkBox != null) {
                checkBox.setVisible(true);
            }*/
    }


    /*class tmp extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/
}
