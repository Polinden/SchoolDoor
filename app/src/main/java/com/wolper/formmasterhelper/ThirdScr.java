package com.wolper.formmasterhelper;

import android.support.v4.app.Fragment;

public class ThirdScr extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new SetUpSecurityFragment();
    }
}
