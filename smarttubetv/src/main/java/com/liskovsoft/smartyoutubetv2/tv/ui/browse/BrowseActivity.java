package com.liskovsoft.smartyoutubetv2.tv.ui.browse;

import android.os.Bundle;

import com.liskovsoft.smartyoutubetv2.tv.BuildConfig;

import com.liskovsoft.sharedutils.helpers.MessageHelpers;
import com.liskovsoft.smartyoutubetv2.common.prefs.MainUIData;
import com.liskovsoft.smartyoutubetv2.tv.R;
import com.liskovsoft.smartyoutubetv2.tv.ui.common.LeanbackActivity;

public class BrowseActivity extends LeanbackActivity {
    private DanTubeStartup mDanTubeStartup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.fragment_main);
            if ("dantube".equals(BuildConfig.FLAVOR)) {
                mDanTubeStartup = new DanTubeStartup(this);
            }
        } catch (NoClassDefFoundError e) {
            // Failed resolution of: Landroidx/lifecycle/ViewTreeLifecycleOwner;
            MessageHelpers.showMessage(this, e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanTubeStartup != null) {
            mDanTubeStartup.resume();
        }
    }

    @Override
    protected void onPause() {
        if (mDanTubeStartup != null) {
            mDanTubeStartup.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mDanTubeStartup != null) {
            mDanTubeStartup.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void initTheme() {
        int browseThemeResId = MainUIData.instance(this).getColorScheme().browseThemeResId;
        if (browseThemeResId > 0) {
            setTheme(browseThemeResId);
        }
    }
}
