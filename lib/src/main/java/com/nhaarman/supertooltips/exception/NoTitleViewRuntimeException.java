package com.nhaarman.supertooltips.exception;

/**
 * A {@link RuntimeException} that is thrown if the title view in the
 * {@link android.app.ActionBar} is not found. You can choose to ignore this by catching
 * the NoTitleViewRuntimeException.
 */
public class NoTitleViewRuntimeException extends RuntimeException {

    public NoTitleViewRuntimeException() {
        super("No title View found. Are you sure it exists?");
    }
}
