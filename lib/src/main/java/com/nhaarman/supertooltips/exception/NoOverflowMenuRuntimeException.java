package com.nhaarman.supertooltips.exception;

/**
 * A {@link RuntimeException} that is thrown if the overflow menu is not
 * found. This happens when there simply is no overflow menu button, or the
 * menu isn't initialised yet. You can choose to ignore this by catching the
 * NoOverflowMenuRuntimeException.
 */
public class NoOverflowMenuRuntimeException extends RuntimeException {

    public NoOverflowMenuRuntimeException() {
        super("No overflow menu found. Are you sure the overflow menu button is visible? Check the docs for showToolTipForActionBarOverflowMenu(Activity, ToolTip) again!");
    }
}