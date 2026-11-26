package com.liskovsoft.smartyoutubetv2.tv.ui.browse;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.liskovsoft.smartyoutubetv2.common.app.presenters.YTSignInPresenter;
import com.liskovsoft.smartyoutubetv2.tv.R;
import com.liskovsoft.youtubeapi.service.YouTubeServiceManager;

final class DanTubeStartup {
    private static final long MAX_OPENING_MS = 1900;
    private static final String PREFERENCES = "dantube_startup";
    private static final String CONTINUE_AS_GUEST = "continue_as_guest";
    private static boolean sOpeningPlayed;

    private final BrowseActivity mActivity;
    private final FrameLayout mContent;
    private final SharedPreferences mPreferences;
    private final Runnable mEndOpening = this::endOpening;
    private VideoView mOpening;
    private FrameLayout mOpeningOverlay;
    private AlertDialog mWelcome;
    private boolean mResumed;

    DanTubeStartup(BrowseActivity activity) {
        mActivity = activity;
        mContent = activity.findViewById(android.R.id.content);
        mPreferences = activity.getSharedPreferences(PREFERENCES, BrowseActivity.MODE_PRIVATE);
    }

    void resume() {
        mResumed = true;
        if (sOpeningPlayed) {
            showWelcomeIfNeeded();
            return;
        }

        sOpeningPlayed = true;
        int opening = mActivity.getResources().getIdentifier("dantube_opening", "raw", mActivity.getPackageName());
        if (opening == 0) {
            showWelcomeIfNeeded();
            return;
        }

        mOpening = new VideoView(mActivity);
        mOpening.setFocusable(false);
        mOpening.setOnCompletionListener(player -> endOpening());
        mOpening.setOnErrorListener((player, what, extra) -> {
            endOpening();
            return true;
        });

        mOpeningOverlay = new FrameLayout(mActivity);
        mOpeningOverlay.setBackgroundColor(Color.rgb(11, 16, 28));
        mOpeningOverlay.setClickable(true);
        FrameLayout.LayoutParams openingParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mOpeningOverlay.addView(mOpening, openingParams);
        mContent.addView(mOpeningOverlay);
        mContent.postDelayed(mEndOpening, MAX_OPENING_MS);
        mOpening.setVideoURI(Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + opening));
        mOpening.start();
    }

    void pause() {
        mResumed = false;
        endOpening();
        if (mWelcome != null) {
            mWelcome.dismiss();
            mWelcome = null;
        }
    }

    void destroy() {
        pause();
    }

    private void endOpening() {
        mContent.removeCallbacks(mEndOpening);
        if (mOpeningOverlay != null) {
            mOpening.stopPlayback();
            mContent.removeView(mOpeningOverlay);
            mOpening = null;
            mOpeningOverlay = null;
        }
        showWelcomeIfNeeded();
    }

    private void showWelcomeIfNeeded() {
        if (!mResumed || mActivity.isFinishing() || mWelcome != null
                || mPreferences.getBoolean(CONTINUE_AS_GUEST, false)
                || YouTubeServiceManager.instance().getSignInService().isSigned()) {
            return;
        }

        CharSequence[] choices = {
                mActivity.getText(R.string.dantube_connect_youtube),
                mActivity.getText(R.string.dantube_continue_as_guest)
        };
        mWelcome = new AlertDialog.Builder(mActivity, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setTitle(R.string.dantube_welcome)
                .setItems(choices, (dialog, index) -> {
                    if (index == 0) {
                        YTSignInPresenter.instance(mActivity).start();
                    } else {
                        mPreferences.edit().putBoolean(CONTINUE_AS_GUEST, true).apply();
                    }
                })
                .create();
        mWelcome.setOnCancelListener(dialog -> mPreferences.edit().putBoolean(CONTINUE_AS_GUEST, true).apply());
        mWelcome.setOnDismissListener(dialog -> mWelcome = null);
        mWelcome.show();
    }
}
