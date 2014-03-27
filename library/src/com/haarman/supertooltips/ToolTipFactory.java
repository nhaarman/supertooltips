package com.haarman.supertooltips;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.haarman.supertooltips.ToolTipView.OnToolTipViewRemovedListener;

public class ToolTipFactory {

    public static ToolTipView createAndShow(
            final ToolTipRelativeLayout parent, final ToolTip toolTip,
            final View targetView) {

        final ToolTipView toolTipView = new ToolTipView(parent.getContext());
        toolTipView.setToolTip(toolTip, targetView);
        parent.addView(toolTipView);
        return toolTipView;
    }

    public static ToolTipView createAndShow(final Activity activity,
            final ToolTip toolTip, final View targetView) {

        final ToolTipRelativeLayout parent = createTooltipRelativeLayout(activity);
        final ToolTipView toolTipView = createAndShow(parent,
                toolTip, targetView);

        toolTipView
                .setOnToolTipViewRemovedListener(new OnToolTipViewRemovedListener() {

                    @Override
                    public void onToolTipRemoved(ToolTipView toolTipView) {
                        parent.removeView(toolTipView);
                    }
                });

        return toolTipView;
    }

    private static ToolTipRelativeLayout createTooltipRelativeLayout(
            Activity activity) {
        final ViewGroup root = ((ViewGroup) activity.getWindow().getDecorView());

        final ToolTipRelativeLayout toolTipRelativeLayout = new ToolTipRelativeLayout(
                activity);
        root.addView(toolTipRelativeLayout);
        return toolTipRelativeLayout;
    }
}
