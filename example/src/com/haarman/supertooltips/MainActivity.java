package com.haarman.supertooltips;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.haarman.supertoasts.ToolTip;
import com.haarman.supertoasts.ToolTipFrameLayout;
import com.haarman.supertoasts.ToolTipView;

public class MainActivity extends Activity implements View.OnClickListener, ToolTipView.OnToolTipViewClickedListener {

    private ToolTipView mRedToolTipView;
    private ToolTipView mGreenToolTipView;
    private ToolTipView mBlueToolTipView;
    private ToolTipView mCyanToolTipView;
    private ToolTipFrameLayout mToolTipFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolTipFrameLayout = (ToolTipFrameLayout) findViewById(R.id.activity_main_tooltipframelayout);
        findViewById(R.id.activity_main_redtv).setOnClickListener(MainActivity.this);
        findViewById(R.id.activity_main_greentv).setOnClickListener(MainActivity.this);
        findViewById(R.id.activity_main_bluetv).setOnClickListener(MainActivity.this);
        findViewById(R.id.activity_main_cyantv).setOnClickListener(MainActivity.this);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addRedToolTipView();
                addGreenToolTipView();
                addBlueToolTipView();
                addCyanToolTipView();
            }
        }, 1000);
    }

    private void addRedToolTipView() {
        mRedToolTipView = mToolTipFrameLayout.showToolTipForView(new ToolTip().withText("A beautiful Button").withColor(Color.RED), findViewById(R.id.activity_main_redtv));
        mRedToolTipView.setOnToolTipViewClickedListener(MainActivity.this);
    }

    private void addGreenToolTipView() {
        mGreenToolTipView = mToolTipFrameLayout.showToolTipForView(new ToolTip().withText("A beautiful Button").withColor(Color.GREEN), findViewById(R.id.activity_main_greentv));
        mGreenToolTipView.setOnToolTipViewClickedListener(MainActivity.this);
    }

    private void addBlueToolTipView() {
        mBlueToolTipView = mToolTipFrameLayout.showToolTipForView(new ToolTip().withText("A beautiful Button").withColor(Color.BLUE), findViewById(R.id.activity_main_bluetv));
        mBlueToolTipView.setOnToolTipViewClickedListener(MainActivity.this);
    }

    private void addCyanToolTipView() {
        mCyanToolTipView = mToolTipFrameLayout.showToolTipForView(new ToolTip().withText("A beautiful Button").withColor(Color.CYAN), findViewById(R.id.activity_main_cyantv));
        mCyanToolTipView.setOnToolTipViewClickedListener(MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_redtv:
                if (mRedToolTipView == null) {
                    addRedToolTipView();
                }
                break;
            case R.id.activity_main_greentv:
                if (mGreenToolTipView == null) {
                    addGreenToolTipView();
                }
                break;
            case R.id.activity_main_bluetv:
                if (mBlueToolTipView == null) {
                    addBlueToolTipView();
                }
                break;
            case R.id.activity_main_cyantv:
                if (mCyanToolTipView == null) {
                    addCyanToolTipView();
                }
                break;
        }
    }

    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {
        if (mRedToolTipView == toolTipView) {
            mRedToolTipView = null;
        } else if (mGreenToolTipView == toolTipView) {
            mGreenToolTipView = null;
        } else if (mBlueToolTipView == toolTipView) {
            mBlueToolTipView = null;
        } else if (mCyanToolTipView == toolTipView) {
            mCyanToolTipView = null;
        }
    }
}

