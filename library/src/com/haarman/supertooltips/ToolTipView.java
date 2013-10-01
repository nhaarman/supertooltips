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

package com.haarman.supertooltips;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A ViewGroup to visualize ToolTips. Use ToolTipRelativeLayout.showToolTipForView() to show ToolTips.
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
    private ToolTipOptions mToolTipOptions;
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
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!(getParent() instanceof FrameLayout)) {
             throw new IllegalStateException("ToolTipView must be the child of a FrameLayout");
        }
    }

    @Override
    public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        mDimensionsKnown = true;

        mWidth = mContentHolder.getWidth();

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = mWidth;
        setLayoutParams(layoutParams);

        if (mToolTipOptions != null) {
            applyToolTipPosition();
        }
        return true;
    }

    public void setToolTipOptions(ToolTipOptions toolTipOptions) {
        mToolTipOptions = toolTipOptions;

        if (mToolTipOptions.getText() != null) {
            mToolTipTV.setText(mToolTipOptions.getText());
        } else if (mToolTipOptions.getTextResId() != 0) {
            mToolTipTV.setText(mToolTipOptions.getTextResId());
        }

        if (mToolTipOptions.getColor() != 0) {
            setColor(mToolTipOptions.getColor());
        }

        mToolTipTV.setTextColor(mToolTipOptions.getTextColor());

        if (mToolTipOptions.getContentView() != null) {
            setContentView(mToolTipOptions.getContentView());
        }

        if (!mToolTipOptions.getShadow()) {
            mShadowView.setVisibility(View.GONE);
        }

        if (mDimensionsKnown) {
            applyToolTipPosition();
        }
    }

    public void showAt(View target) {
        mView = target;
        Activity activity = (Activity) getContext();
        FrameLayout root = (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        root.addView(this);
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

        float toolTipViewX = Math.max(0, relativeMasterViewCenterX - mWidth / 2);
        if (toolTipViewX + mWidth > viewDisplayFrame.right) {
            toolTipViewX = viewDisplayFrame.right - mWidth;
        }

        setX(toolTipViewX);
        setPointerCenterX(relativeMasterViewCenterX);

        final boolean showBelow = toolTipViewAboveY < 0;

        if (Build.VERSION.SDK_INT < 11) {
            ViewHelper.setAlpha(mTopPointerView, showBelow ? 1 : 0);
            ViewHelper.setAlpha(mBottomPointerView, showBelow ? 0 : 1);
        } else {
            mTopPointerView.setVisibility(showBelow ? VISIBLE : GONE);
            mBottomPointerView.setVisibility(showBelow ? GONE : VISIBLE);
        }

        if (showBelow) {
            toolTipViewY = toolTipViewBelowY;
        } else {
            toolTipViewY = toolTipViewAboveY;
        }

        List<Animator> animators = new ArrayList<Animator>();

        if (mToolTipOptions.getAnimationType() == ToolTipOptions.ANIMATIONTYPE_FROMMASTERVIEW) {
            animators.add(ObjectAnimator.ofFloat(this, "translationY", mRelativeMasterViewY + mView.getHeight() / 2 - getHeight() / 2, toolTipViewY));
            animators.add(ObjectAnimator.ofFloat(this, "translationX", mRelativeMasterViewX + mView.getWidth() / 2 - mWidth / 2, toolTipViewX));
        } else if (mToolTipOptions.getAnimationType() == ToolTipOptions.ANIMATIONTYPE_FROMTOP) {
            animators.add(ObjectAnimator.ofFloat(this, "translationY", 0, toolTipViewY));
        }

        animators.add(ObjectAnimator.ofFloat(this, "scaleX", 0, 1));
        animators.add(ObjectAnimator.ofFloat(this, "scaleY", 0, 1));

        animators.add(ObjectAnimator.ofFloat(this, "alpha", 0, 1));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);

        if (Build.VERSION.SDK_INT < 11) {
            final float fToolTipViewX = toolTipViewX;
            final float fToolTipViewY = toolTipViewY;
            animatorSet.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
                    params.leftMargin = (int) fToolTipViewX;
                    params.topMargin = (int) fToolTipViewY;
                    setX(0);
                    setY(0);
                    setLayoutParams(params);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }

        animatorSet.start();
    }

    public void setPointerCenterX(int pointerCenterX) {
        int pointerWidth = Math.max(mTopPointerView.getMeasuredWidth(), mBottomPointerView.getMeasuredWidth());

        ViewHelper.setX(mTopPointerView, pointerCenterX - pointerWidth / 2 - getX());
        ViewHelper.setX(mBottomPointerView, pointerCenterX - pointerWidth / 2 - getX());
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
        if (Build.VERSION.SDK_INT < 11) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
            setX(params.leftMargin);
            setY(params.topMargin);
            params.leftMargin = 0;
            params.topMargin = 0;
            setLayoutParams(params);
        }

        List<Animator> animators = new ArrayList<Animator>();
        if (mToolTipOptions.getAnimationType() == ToolTipOptions.ANIMATIONTYPE_FROMMASTERVIEW) {
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
                if (getParent() != null) {
                    ((ViewGroup) getParent()).removeView(ToolTipView.this);
                }
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

    /**
     * Convenience method for getting X.
     */
    @Override
    public float getX() {
        if (Build.VERSION.SDK_INT >= 11) {
            return super.getX();
        } else {
            return ViewHelper.getX(this);
        }
    }

    /**
     * Convenience method for setting X.
     */
    @Override
    public void setX(float x) {
        if (Build.VERSION.SDK_INT >= 11) {
            super.setX(x);
        } else {
            ViewHelper.setX(this, x);
        }
    }

    /**
     * Convenience method for getting Y.
     */
    @Override
    public float getY() {
        if (Build.VERSION.SDK_INT >= 11) {
            return super.getY();
        } else {
            return ViewHelper.getY(this);
        }
    }

    /**
     * Convenience method for setting Y.
     */
    @Override
    public void setY(float y) {
        if (Build.VERSION.SDK_INT >= 11) {
            super.setY(y);
        } else {
            ViewHelper.setY(this, y);
        }
    }

    public interface OnToolTipViewClickedListener {
        public void onToolTipViewClicked(ToolTipView toolTipView);
    }
}
