package org.granchi.whatnow;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

/**
 * Custom Application class for the debug build variant of the app, with logging enabled.
 *
 * @author serandel
 */
public class DebugWhatNowApplication extends WhatNowApplication {
    @Override
    protected List<Timber.Tree> getLoggingTrees() {
        // TODO get it from dagger
        return Collections.singletonList(new Timber.DebugTree());
    }
}