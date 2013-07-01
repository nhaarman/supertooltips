/*
 * Copyright 2013 Niek Haarman
 *
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

import android.view.View;

public class ToolTip {

    public static final int ANIMATIONTYPE_FROMMASTERVIEW = 101;
    public static final int ANIMATIONTYPE_FROMTOP = 102;
    //
    private CharSequence text;
    private int textResId;
    private int color;
    private View contentView;
    private int animationType;
    private boolean shadow;

    public ToolTip() {
        text = null;
        textResId = 0;
        color = 0;
        contentView = null;
        animationType = ANIMATIONTYPE_FROMMASTERVIEW;
    }

    public ToolTip withText(CharSequence text) {
        this.text = text;
        this.textResId = 0;
        return this;
    }

    public ToolTip withText(int resId) {
        this.textResId = resId;
        this.text = null;
        return this;
    }

    public ToolTip withColor(int color) {
        this.color = color;
        return this;
    }

    public ToolTip withContentView(View view) {
        this.contentView = view;
        return this;
    }

    public ToolTip withAnimationType(int animationType) {
        this.animationType = animationType;
        return this;
    }

    public ToolTip withShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public CharSequence getText() {
        return text;
    }

    public int getTextResId() {
        return textResId;
    }

    public int getColor() {
        return color;
    }

    public View getContentView() {
        return contentView;
    }

    public int getAnimationType() {
        return animationType;
    }

    public boolean getShadow() {
        return shadow;
    }
}