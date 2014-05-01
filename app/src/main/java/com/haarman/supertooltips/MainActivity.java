package com.haarman.supertooltips;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

public class MainActivity extends Activity implements View.OnClickListener, ToolTipView.OnToolTipViewClickedListener {

    private ToolTipView mRedToolTipView;
    private ToolTipView mGreenToolTipView;
    private ToolTipView mBlueToolTipView;
    private ToolTipView mPurpleToolTipView;
    private ToolTipView mOrangeToolTipView;
    private ToolTipRelativeLayout mToolTipFrameLayout;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolTipFrameLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipframelayout);
        findViewById(R.id.activity_main_redtv).setOnClickListener(this);
        findViewById(R.id.activity_main_greentv).setOnClickListener(this);
        findViewById(R.id.activity_main_bluetv).setOnClickListener(this);
        findViewById(R.id.activity_main_purpletv).setOnClickListener(this);
        findViewById(R.id.activity_main_orangetv).setOnClickListener(this);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addRedToolTipView();
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addGreenToolTipView();
            }
        }, 700);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addOrangeToolTipView();
            }
        }, 900);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addBlueToolTipView();
            }
        }, 1100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addPurpleToolTipView();
            }
        }, 1300);

    }

    private void addRedToolTipView() {
        ToolTip toolTip = new ToolTip()
                .withText("A beautiful Button")
                .withColor(getResources().getColor(R.color.holo_red))
                .withShadow(true);

        mRedToolTipView = mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_redtv));
        mRedToolTipView.setOnToolTipViewClickedListener(this);
    }

    private void addGreenToolTipView() {
        ToolTip toolTip = new ToolTip()
                .withText("Another beautiful Button!")
                .withColor(getResources().getColor(R.color.holo_green));

        mGreenToolTipView = mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_greentv));
        mGreenToolTipView.setOnToolTipViewClickedListener(this);
    }

    private void addBlueToolTipView() {
        ToolTip toolTip = new ToolTip()
                .withText("Moarrrr buttons!")
                .withColor(getResources().getColor(R.color.holo_blue))
                .withAnimationType(ToolTip.ANIMATIONTYPE_FROMTOP);

        mBlueToolTipView = mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_bluetv));
        mBlueToolTipView.setOnToolTipViewClickedListener(this);
    }

    private void addPurpleToolTipView() {
        ToolTip toolTip = new ToolTip()
                .withContentView(LayoutInflater.from(this).inflate(R.layout.custom_tooltip, null))
                .withColor(getResources().getColor(R.color.holo_purple));

        mPurpleToolTipView = mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_purpletv));
        mPurpleToolTipView.setOnToolTipViewClickedListener(this);
    }

    private void addOrangeToolTipView() {
        ToolTip toolTip = new ToolTip()
                .withText("Tap me!")
                .withColor(getResources().getColor(R.color.holo_orange));

        mOrangeToolTipView = mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_orangetv));
        mOrangeToolTipView.setOnToolTipViewClickedListener(this);
    }

    @Override
    public void onClick(final View view) {
        int id = view.getId();
        if (id == R.id.activity_main_redtv) {
            if (mRedToolTipView == null) {
                addRedToolTipView();
            } else {
                mRedToolTipView.remove();
                mRedToolTipView = null;
            }

        } else if (id == R.id.activity_main_greentv) {
            if (mGreenToolTipView == null) {
                addGreenToolTipView();
            } else {
                mGreenToolTipView.remove();
                mGreenToolTipView = null;
            }

        } else if (id == R.id.activity_main_bluetv) {
            if (mBlueToolTipView == null) {
                addBlueToolTipView();
            } else {
                mBlueToolTipView.remove();
                mBlueToolTipView = null;
            }

        } else if (id == R.id.activity_main_purpletv) {
            if (mPurpleToolTipView == null) {
                addPurpleToolTipView();
            } else {
                mPurpleToolTipView.remove();
                mPurpleToolTipView = null;
            }

        } else if (id == R.id.activity_main_orangetv) {
            if (mOrangeToolTipView == null) {
                addOrangeToolTipView();
            } else {
                mOrangeToolTipView.remove();
                mOrangeToolTipView = null;
            }

        }
    }

    @Override
    public void onToolTipViewClicked(final ToolTipView toolTipView) {
        if (mRedToolTipView == toolTipView) {
            mRedToolTipView = null;
        } else if (mGreenToolTipView == toolTipView) {
            mGreenToolTipView = null;
        } else if (mBlueToolTipView == toolTipView) {
            mBlueToolTipView = null;
        } else if (mPurpleToolTipView == toolTipView) {
            mPurpleToolTipView = null;
        } else if (mOrangeToolTipView == toolTipView) {
            mOrangeToolTipView = null;
        }
    }
}

