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
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haarman.supertooltips.R;

public class ToolTipView extends LinearLayout implements ViewTreeObserver.OnPreDrawListener, View.OnClickListener {

    private ImageView mTopPointerIV;
    private ImageView mBottomPointerIV;
    private ViewGroup mContentHolder;
    private TextView mToolTipTV;
    //
    private ToolTip mToolTip;
    private View mView;
    //
    private boolean mDimensionsKnown;
    //
    private int mParentsPaddingTop;
    private int mParentsPaddingLeft;
    private int mParentTop;
    private int mRelativeMasterViewX;
    private int mRelativeMasterViewY;

    public ToolTipView(Context context) {
        super(context);
        init();
    }

    public ToolTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToolTipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.tooltip, this, true);

        mTopPointerIV = (ImageView) findViewById(R.id.tooltip_pointer_up_iv);
        mBottomPointerIV = (ImageView) findViewById(R.id.tooltip_pointer_down_iv);
        mContentHolder = (ViewGroup) findViewById(R.id.tooltip_contentholder);
        mToolTipTV = (TextView) findViewById(R.id.tooltip_tv);

        setOnClickListener(this);
        getViewTreeObserver().addOnPreDrawListener(this);
    }

    @Override
    public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        mDimensionsKnown = true;
        findPaddings();

        mParentTop = ((ViewGroup) getParent()).getTop();

        if (mToolTip != null) {
            applyToolTipPosition();
        }
        return true;
    }

    private void findPaddings() {
        mParentsPaddingTop = 0;
        mParentsPaddingLeft = 0;

        ViewParent parent = getParent();
        while (parent instanceof ViewGroup) {
            mParentsPaddingTop += ((ViewGroup) parent).getPaddingTop();
            mParentsPaddingLeft += ((ViewGroup) parent).getPaddingLeft();

            parent = parent.getParent();
        }
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


        if (mDimensionsKnown) {
            applyToolTipPosition();
        }
    }

    private void applyToolTipPosition() {
        final int[] screenPosition = new int[2];
        final Rect viewDisplayFrame = new Rect(); // includes decorations (e.g. status bar)
        mView.getLocationOnScreen(screenPosition);
        mView.getWindowVisibleDisplayFrame(viewDisplayFrame);

        final int masterViewWidth = mView.getWidth();
        final int masterViewHeight = mView.getHeight();

        mRelativeMasterViewX = screenPosition[0] - mParentsPaddingLeft;
        mRelativeMasterViewY = screenPosition[1] - viewDisplayFrame.top - mParentTop - mParentsPaddingTop;
        final int relativeMasterViewCenterX = mRelativeMasterViewX + masterViewWidth / 2;

        float toolTipViewAboveY = mRelativeMasterViewY - getHeight();
        float toolTipViewBelowY = mRelativeMasterViewY + masterViewHeight;
        float toolTipViewY;


        setX(Math.max(0, relativeMasterViewCenterX - getWidth() / 2));
        setPointerCenterX(relativeMasterViewCenterX);

        boolean showBelow = toolTipViewAboveY < 0;
        mTopPointerIV.setVisibility(showBelow ? VISIBLE : GONE);
        mBottomPointerIV.setVisibility(showBelow ? GONE : VISIBLE);
        if (showBelow) {
            toolTipViewY = toolTipViewBelowY;
        } else {
            toolTipViewY = toolTipViewAboveY;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "translationY", mRelativeMasterViewY, toolTipViewY);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 0, 1);
        ValueAnimator heightAnimator = ValueAnimator.ofInt(0, getHeight());
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height = (Integer) valueAnimator.getAnimatedValue();
                setLayoutParams(layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, heightAnimator, alphaAnimator);
        animatorSet.start();
    }

    public void setPointerCenterX(int pointerCenterX) {
        int pointerWidth = Math.max(mTopPointerIV.getMeasuredWidth(), mBottomPointerIV.getMeasuredWidth());

        mTopPointerIV.setX(pointerCenterX - pointerWidth / 2 - getX());
        mBottomPointerIV.setX(pointerCenterX - pointerWidth / 2 - getX());
    }

    public void setColor(int color) {
        BitmapDrawable topPointer = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.bg_tooltip_pointer_up));
        topPointer.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mTopPointerIV.setImageDrawable(topPointer);

        Drawable bottomPointer = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.bg_tooltip_pointer_down));
        bottomPointer.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mBottomPointerIV.setImageDrawable(bottomPointer);

        Drawable textViewBackground = getResources().getDrawable(R.drawable.bg_tooltip);
        textViewBackground.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        int paddingLeft = mToolTipTV.getPaddingLeft();
        int paddingRight = mToolTipTV.getPaddingRight();
        int paddingTop = mToolTipTV.getPaddingTop();
        int paddingBottom = mToolTipTV.getPaddingBottom();
        mToolTipTV.setBackgroundDrawable(textViewBackground);

        mToolTipTV.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private void setContentView(View view) {
        mContentHolder.removeAllViews();
        mContentHolder.addView(view);
    }

    @Override
    public void onClick(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "translationY", getY(), mRelativeMasterViewY);
        ValueAnimator heightAnimator = ValueAnimator.ofInt(getHeight(), 0);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height = (Integer) valueAnimator.getAnimatedValue();
                setLayoutParams(layoutParams);
            }
        });
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 1, 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, heightAnimator, alphaAnimator);
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
}
