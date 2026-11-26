package com.liskovsoft.smartyoutubetv2.tv.presenter;

import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.ListRowPresenter;
import com.liskovsoft.smartyoutubetv2.tv.BuildConfig;
import com.liskovsoft.smartyoutubetv2.tv.util.ViewUtil;

public class CustomListRowPresenter extends ListRowPresenter {
    public CustomListRowPresenter() {
        super("dantube".equals(BuildConfig.FLAVOR) ? FocusHighlight.ZOOM_FACTOR_NONE : ViewUtil.FOCUS_ZOOM_FACTOR,
                ViewUtil.FOCUS_DIMMER_ENABLED);
        setSelectEffectEnabled(ViewUtil.ROW_SELECT_EFFECT_ENABLED);
    }
}
