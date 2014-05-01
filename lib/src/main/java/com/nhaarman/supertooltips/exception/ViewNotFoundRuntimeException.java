package com.nhaarman.supertooltips.exception;

/**
 * A {@link RuntimeException} that is thrown if there is no {@link android.view.View}
 * found with given resource id. You can choose to ignore this by catching
 * the ViewNotFoundRuntimeException.
 */
public class ViewNotFoundRuntimeException extends RuntimeException {

    public ViewNotFoundRuntimeException() {
        super("View not found for this resource id. Are you sure it exists?");
    }
}
