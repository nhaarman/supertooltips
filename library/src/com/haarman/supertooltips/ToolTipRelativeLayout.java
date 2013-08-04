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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class ToolTipRelativeLayout extends RelativeLayout {

    public ToolTipRelativeLayout(Context context) {
        super(context);
    }

    public ToolTipRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolTipRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Shows a ToolTipView based on gived ToolTip at the proper location relative to given View.
     *
     * @return the ToolTipView that was created.
     */
    public ToolTipView showToolTipForView(ToolTip toolTip, final View view) {
        final ToolTipView toolTipView = new ToolTipView(getContext());
        toolTipView.setToolTip(toolTip, view);
        addView(toolTipView);
        return toolTipView;
    }

}
