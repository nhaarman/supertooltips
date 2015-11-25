/*
 * Copyright 2013 Niek Haarman
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nhaarman.supertooltips;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A ViewGroup to visualize ToolTips. Use
 * ToolTipRelativeLayout.showToolTipForView() to show ToolTips.
 */
public class ToolTipView extends LinearLayout implements ViewTreeObserver.OnPreDrawListener, View.OnClickListener {

    public static final String TRANSLATION_Y_COMPAT = "translationY";
    public static final String TRANSLATION_X_COMPAT = "translationX";
    public static final String SCALE_X_COMPAT = "scaleX";
    public static final String SCALE_Y_COMPAT = "scaleY";
    public static final String ALPHA_COMPAT = "alpha";

    private ImageView mTopPointerView;
    private View mTopFrame;
    private ViewGroup mContentHolder;
    private TextView mToolTipTV;
    private View mBottomFrame;
    private ImageView mBottomPointerView;
    private View mShadowView;

    private ToolTip mToolTip;
    private View mView;

    private boolean mDimensionsKnown;
    private int mRelativeMasterViewY;

    private int mRelativeMasterViewX;
    private int mWidth;

    private OnToolTipViewClickedListener mListener;

    public ToolTipView(final Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.tooltip, this, true);

        mTopPointerView = (ImageView) findViewById(R.id.tooltip_pointer_up);
        mTopFrame = findViewById(R.id.tooltip_topframe);
        mContentHolder = (ViewGroup) findViewById(R.id.tooltip_contentholder);
        mToolTipTV = (TextView) findViewById(R.id.tooltip_contenttv);
        mBottomFrame = findViewById(R.id.tooltip_bottomframe);
        mBottomPointerView = (ImageView) findViewById(R.id.tooltip_pointer_down);
        mShadowView = findViewById(R.id.tooltip_shadow);

        setOnClickListener(this);
        getViewTreeObserver().addOnPreDrawListener(this);
    }

    @Override
    public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        mDimensionsKnown = true;

        mWidth = mContentHolder.getWidth();

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.width = mWidth;
        setLayoutParams(layoutParams);

        if (mToolTip != null) {
            applyToolTipPosition();
        }
        return true;
    }

    private void setToolTipPointer(ToolTip.PointerState state) {
        float alphaTop = 0, alphaBot  = 0;
        int visibilityTop = GONE, visibilityBot = GONE;
        switch (state) {
            case UP:
                alphaTop = 1;
                visibilityTop = VISIBLE;
                break;
            case DOWN:
                alphaBot = 1;
                visibilityBot = VISIBLE;
                break;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ViewHelper.setAlpha(mTopPointerView, alphaTop);
            ViewHelper.setAlpha(mBottomPointerView, alphaBot);
        } else {
            mTopPointerView.setVisibility(visibilityTop);
            mBottomPointerView.setVisibility(visibilityBot);
        }
    }

    public void setToolTip(final ToolTip toolTip, final View view) {
        mToolTip = toolTip;
        mView = view;

        if (mToolTip.getText() != null) {
            mToolTipTV.setText(mToolTip.getText());
        } else if (mToolTip.getTextResId() != 0) {
            mToolTipTV.setText(mToolTip.getTextResId());
        }

        if (mToolTip.getTypeface() != null) {
            mToolTipTV.setTypeface(mToolTip.getTypeface());
        }

        if (mToolTip.getTextColor() != 0) {
            mToolTipTV.setTextColor(mToolTip.getTextColor());
        }

        if (mToolTip.getColor() != 0) {
            setColor(mToolTip.getColor());
        }

        if (mToolTip.getPointerState() != null) {
            setToolTipPointer(mToolTip.getPointerState());
        }

        if (mToolTip.getContentView() != null) {
            setContentView(mToolTip.getContentView());
        }

        if (!mToolTip.shouldShowShadow()) {
            mShadowView.setVisibility(View.GONE);
        }

        if (mDimensionsKnown) {
            applyToolTipPosition();
        }
    }

    private void applyToolTipPosition() {
        final int[] masterViewScreenPosition = new int[2];
        mView.getLocationOnScreen(masterViewScreenPosition);

        final Rect viewDisplayFrame = new Rect();
        mView.getWindowVisibleDisplayFrame(viewDisplayFrame);

        final int[] parentViewScreenPosition = new int[2];
        ((View) getParent()).getLocationOnScreen(parentViewScreenPosition);

        final int masterViewWidth = mView.getWidth();

        mRelativeMasterViewX = masterViewScreenPosition[0] - parentViewScreenPosition[0];
        mRelativeMasterViewY = masterViewScreenPosition[1] - parentViewScreenPosition[1];
        final int relativeMasterViewCenterX = mRelativeMasterViewX + masterViewWidth / 2;

        int toolTipViewX = Math.max(0, relativeMasterViewCenterX - mWidth / 2);
        if (toolTipViewX + mWidth > viewDisplayFrame.right) {
            toolTipViewX = viewDisplayFrame.right - mWidth;
        }

        setX(toolTipViewX);
        setPointerCenterX(relativeMasterViewCenterX);

        setToolTipPointer(mToolTip.getPointerState());

        int toolTipViewY = mRelativeMasterViewY;

        if (mToolTip.getAnimationType() == ToolTip.AnimationType.NONE) {
            ViewHelper.setTranslationY(this, toolTipViewY);
            ViewHelper.setTranslationX(this, toolTipViewX);
        } else {
            Collection<Animator> animators = new ArrayList<>(5);

            if (mToolTip.getAnimationType() == ToolTip.AnimationType.FROM_MASTER_VIEW) {
                animators.add(ObjectAnimator.ofInt(this, TRANSLATION_Y_COMPAT, mRelativeMasterViewY + mView.getHeight() / 2 - getHeight() / 2, toolTipViewY));
                animators.add(ObjectAnimator.ofInt(this, TRANSLATION_X_COMPAT, mRelativeMasterViewX + mView.getWidth() / 2 - mWidth / 2, toolTipViewX));
            } else if (mToolTip.getAnimationType() == ToolTip.AnimationType.FROM_TOP) {
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, 0, toolTipViewY));
            }

            animators.add(ObjectAnimator.ofFloat(this, SCALE_X_COMPAT, 0, 1));
            animators.add(ObjectAnimator.ofFloat(this, SCALE_Y_COMPAT, 0, 1));

            animators.add(ObjectAnimator.ofFloat(this, ALPHA_COMPAT, 0, 1));

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                animatorSet.addListener(new AppearanceAnimatorListener(toolTipViewX, toolTipViewY));
            }

            animatorSet.start();
        }
    }

    public void setPointerCenterX(final int pointerCenterX) {
        int pointerWidth = Math.max(mTopPointerView.getMeasuredWidth(), mBottomPointerView.getMeasuredWidth());

        ViewHelper.setX(mTopPointerView, pointerCenterX - pointerWidth / 2 - (int) getX());
        ViewHelper.setX(mBottomPointerView, pointerCenterX - pointerWidth / 2 - (int) getX());
    }

    public void setOnToolTipViewClickedListener(final OnToolTipViewClickedListener listener) {
        mListener = listener;
    }

    public void setColor(final int color) {
        mTopPointerView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mTopFrame.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mBottomPointerView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mBottomFrame.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mContentHolder.setBackgroundColor(color);
    }

    private void setContentView(final View view) {
        mContentHolder.removeAllViews();
        mContentHolder.addView(view);
    }

    public void remove() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
            setX(params.leftMargin);
            setY(params.topMargin);
            params.leftMargin = 0;
            params.topMargin = 0;
            setLayoutParams(params);
        }

        if (mToolTip.getAnimationType() == ToolTip.AnimationType.NONE) {
            if (getParent() != null) {
                ((ViewManager) getParent()).removeView(this);
            }
        } else {
            Collection<Animator> animators = new ArrayList<>(5);
            if (mToolTip.getAnimationType() == ToolTip.AnimationType.FROM_MASTER_VIEW) {
                animators.add(ObjectAnimator.ofInt(this, TRANSLATION_Y_COMPAT, (int) getY(), mRelativeMasterViewY + mView.getHeight() / 2 - getHeight() / 2));
                animators.add(ObjectAnimator.ofInt(this, TRANSLATION_X_COMPAT, (int) getX(), mRelativeMasterViewX + mView.getWidth() / 2 - mWidth / 2));
            } else {
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, getY(), 0));
            }

            animators.add(ObjectAnimator.ofFloat(this, SCALE_X_COMPAT, 1, 0));
            animators.add(ObjectAnimator.ofFloat(this, SCALE_Y_COMPAT, 1, 0));

            animators.add(ObjectAnimator.ofFloat(this, ALPHA_COMPAT, 1, 0));

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);
            animatorSet.addListener(new DisappearanceAnimatorListener());
            animatorSet.start();
        }
    }

    @Override
    public void onClick(final View view) {
        remove();

        if (mListener != null) {
            mListener.onToolTipViewClicked(this);
        }
    }

    /**
     * Convenience method for getting X.
     */
    @SuppressLint("NewApi")
    @Override
    public float getX() {
        float result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            result = super.getX();
        } else {
            result = ViewHelper.getX(this);
        }
        return result;
    }

    /**
     * Convenience method for setting X.
     */
    @SuppressLint("NewApi")
    @Override
    public void setX(final float x) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setX(x);
        } else {
            ViewHelper.setX(this, x);
        }
    }

    /**
     * Convenience method for getting Y.
     */
    @SuppressLint("NewApi")
    @Override
    public float getY() {
        float result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            result = super.getY();
        } else {
            result = ViewHelper.getY(this);
        }
        return result;
    }

    /**
     * Convenience method for setting Y.
     */
    @SuppressLint("NewApi")
    @Override
    public void setY(final float y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setY(y);
        } else {
            ViewHelper.setY(this, y);
        }
    }

    public interface OnToolTipViewClickedListener {
        void onToolTipViewClicked(ToolTipView toolTipView);
    }

    private class AppearanceAnimatorListener extends AnimatorListenerAdapter {

        private final float mToolTipViewX;
        private final float mToolTipViewY;

        AppearanceAnimatorListener(final float fToolTipViewX, final float fToolTipViewY) {
            mToolTipViewX = fToolTipViewX;
            mToolTipViewY = fToolTipViewY;
        }

        @Override
        public void onAnimationStart(final Animator animation) {
        }

        @Override
        @SuppressLint("NewApi")
        public void onAnimationEnd(final Animator animation) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
            params.leftMargin = (int) mToolTipViewX;
            params.topMargin = (int) mToolTipViewY;
            setX(0);
            setY(0);
            setLayoutParams(params);
        }

        @Override
        public void onAnimationCancel(final Animator animation) {
        }

        @Override
        public void onAnimationRepeat(final Animator animation) {
        }
    }

    private class DisappearanceAnimatorListener extends AnimatorListenerAdapter {

        @Override
        public void onAnimationStart(final Animator animation) {
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            if (getParent() != null) {
                ((ViewManager) getParent()).removeView(ToolTipView.this);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) {
        }

        @Override
        public void onAnimationRepeat(final Animator animation) {
        }
    }
}
