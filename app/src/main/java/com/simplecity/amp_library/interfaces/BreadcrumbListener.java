package com.simplecity.amp_library.interfaces;

import com.simplecity.amp_library.ui.views.BreadcrumbItem;

/**
 * Interface with events from a breadcrumb.
 */
public interface BreadcrumbListener {

    /**
     * Called when a breadcrumb item is clicked.
     *
     * @param item The breadcrumb item that was clicked.
     */
    void onBreadcrumbItemClick(BreadcrumbItem item);
}
