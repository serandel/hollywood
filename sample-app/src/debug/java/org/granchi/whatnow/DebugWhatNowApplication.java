package org.granchi.whatnow;

import timber.log.Timber;

/**
 * Custom Application class for the app, with logging enabled.
 *
 * @author serandel
 */
public class DebugWhatNowApplication extends WhatNowApplication {
    @Override
    public void onCreate() {
        // TODO just change for a custom Dagger module and, if it returns Trees, initializes Timber
        Timber.plant(new Timber.DebugTree());
        Timber.i("Logging enabled");

        super.onCreate();
    }
}