package com.wolper.formmasterhelper;

import android.support.v4.app.Fragment;

public class SecondScr extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new SetUpServerFragment();
    }
}


