package com.crystal.android.nlauncher;


import android.support.v4.app.Fragment;

public class NerdLauncherActivity extends com.base.android.nerdlauncher.SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return com.base.android.nerdlauncher.NerdLauncherFeagment.newInstance();
    }

}
