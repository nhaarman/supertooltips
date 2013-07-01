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

package com.haarman.supertoasts;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.haarman.supertooltips.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A View to visualize ToolTips. Use ToolTipFrameLayout.showToolTipForView() to show ToolTips.
 */
public class ToolTipView extends LinearLayout implements ViewTreeObserver.OnPreDrawListener, View.OnClickListener {

    private ImageView mTopPointerView;
    private View mTopFrame;
    private ViewGroup mContentHolder;
    private TextView mToolTipTV;
    private View mBottomFrame;
    private ImageView mBottomPointerView;
    private View mShadowView;
    //
    private ToolTip mToolTip;
    private View mView;
    //
    private boolean mDimensionsKnown;
    private int mRelativeMasterViewY;
    //
    private int mRelativeMasterViewX;
    private int mWidth;
    //
    private OnToolTipViewClickedListener mListener;

    public ToolTipView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = mWidth;
        setLayoutParams(layoutParams);

        if (mToolTip != null) {
            applyToolTipPosition();
        }
        return true;
    }

    public void setToolTip(ToolTip toolTip, View view) {
        mToolTip = toolTip;
        mView = view;

        if (mToolTip.getText() != null) {
            mToolTipTV.setText(mToolTip.getText());
        } else if (mToolTip.getTextResId() != 0) {
            mToolTipTV.setText(mToolTip.getTextResId());
        }

        if (mToolTip.getColor() != 0) {
            setColor(mToolTip.getColor());
        }

        if (mToolTip.getContentView() != null) {
            setContentView(mToolTip.getContentView());
        }

        if (!mToolTip.getShadow()) {
            mShadowView.setVisibility(View.GONE);
        }

        if (mDimensionsKnown) {
            applyToolTipPosition();
        }
    }

    private void applyToolTipPosition() {
        final int[] masterViewScreenPosition = new int[2];
        final int[] parentViewScreenPosition = new int[2];

        final Rect viewDisplayFrame = new Rect(); // includes decorations (e.g. status bar)
        mView.getLocationOnScreen(masterViewScreenPosition);
        mView.getWindowVisibleDisplayFrame(viewDisplayFrame);
        ((View) getParent()).getLocationOnScreen(parentViewScreenPosition);

        final int masterViewWidth = mView.getWidth();
        final int masterViewHeight = mView.getHeight();

        mRelativeMasterViewX = masterViewScreenPosition[0] - parentViewScreenPosition[0];
        mRelativeMasterViewY = masterViewScreenPosition[1] - parentViewScreenPosition[1];
        final int relativeMasterViewCenterX = mRelativeMasterViewX + masterViewWidth / 2;

        float toolTipViewAboveY = mRelativeMasterViewY - getHeight();
        float toolTipViewBelowY = mRelativeMasterViewY + masterViewHeight;
        float toolTipViewY;

        int width = mWidth;
        float toolTipViewX = Math.max(0, relativeMasterViewCenterX - mWidth / 2);
        if (toolTipViewX + mWidth > viewDisplayFrame.right) {
            toolTipViewX = viewDisplayFrame.right - mWidth;
        }

        setX(toolTipViewX);
        setPointerCenterX(relativeMasterViewCenterX);

        boolean showBelow = toolTipViewAboveY < 0;
        mTopPointerView.setVisibility(showBelow ? VISIBLE : GONE);
        mBottomPointerView.setVisibility(showBelow ? GONE : VISIBLE);
        if (showBelow) {
            toolTipViewY = toolTipViewBelowY;
        } else {
            toolTipViewY = toolTipViewAboveY;
        }

        List<Animator> animators = new ArrayList<Animator>();

        if (mToolTip.getAnimationType() == ToolTip.ANIMATIONTYPE_FROMMASTERVIEW) {
            animators.add(ObjectAnimator.ofFloat(this, "translationY", mRelativeMasterViewY + mView.getHeight() / 2 - getHeight() / 2, toolTipViewY));
            animators.add(ObjectAnimator.ofFloat(this, "translationX", mRelativeMasterViewX + mView.getWidth() / 2 - mWidth / 2, toolTipViewX));
        } else if (mToolTip.getAnimationType() == ToolTip.ANIMATIONTYPE_FROMTOP) {
            animators.add(ObjectAnimator.ofFloat(this, "translationY", 0, toolTipViewY));
        }

        animators.add(ObjectAnimator.ofFloat(this, "scaleX", 0, 1));
        animators.add(ObjectAnimator.ofFloat(this, "scaleY", 0, 1));

        animators.add(ObjectAnimator.ofFloat(this, "alpha", 0, 1));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.start();
    }

    public void setPointerCenterX(int pointerCenterX) {
        int pointerWidth = Math.max(mTopPointerView.getMeasuredWidth(), mBottomPointerView.getMeasuredWidth());

        mTopPointerView.setX(pointerCenterX - pointerWidth / 2 - getX());
        mBottomPointerView.setX(pointerCenterX - pointerWidth / 2 - getX());
    }

    public void setOnToolTipViewClickedListener(OnToolTipViewClickedListener listener) {
        mListener = listener;
    }

    public void setColor(int color) {
        mTopPointerView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mTopFrame.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mBottomPointerView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mBottomFrame.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mContentHolder.setBackgroundColor(color);
    }

    private void setContentView(View view) {
        mContentHolder.removeAllViews();
        mContentHolder.addView(view);
    }

    public void remove() {
        List<Animator> animators = new ArrayList<Animator>();
        if (mToolTip.getAnimationType() == ToolTip.ANIMATIONTYPE_FROMMASTERVIEW) {
            animators.add(ObjectAnimator.ofFloat(this, "translationY", getY(), mRelativeMasterViewY + mView.getHeight() / 2 - getHeight() / 2));
            animators.add(ObjectAnimator.ofFloat(this, "translationX", getX(), mRelativeMasterViewX + mView.getWidth() / 2 - mWidth / 2));
        } else {
            animators.add(ObjectAnimator.ofFloat(this, "translationY", getY(), 0));
        }

        animators.add(ObjectAnimator.ofFloat(this, "scaleX", 1, 0));
        animators.add(ObjectAnimator.ofFloat(this, "scaleY", 1, 0));

        animators.add(ObjectAnimator.ofFloat(this, "alpha", 1, 0));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ((ViewGroup) getParent()).removeView(ToolTipView.this);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animatorSet.start();
    }

    @Override
    public void onClick(View view) {
        remove();

        if (mListener != null) {
            mListener.onToolTipViewClicked(this);
        }
    }

    public interface OnToolTipViewClickedListener {
        public void onToolTipViewClicked(ToolTipView toolTipView);
    }
}
