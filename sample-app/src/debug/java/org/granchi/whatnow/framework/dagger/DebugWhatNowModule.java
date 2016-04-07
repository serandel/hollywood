package org.granchi.whatnow.framework.dagger;

import org.granchi.whatnow.DebugWhatNowApplication;

import java.util.Collections;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * Dagger module for the debug build variant of the app.
 *
 * @author serandel
 */
@Module
public class DebugWhatNowModule extends WhatNowModule {
    public DebugWhatNowModule(DebugWhatNowApplication app) {
        super(app);
    }

    @Provides
    protected List<Timber.Tree> providesLoggingTrees() {
        return Collections.singletonList(new Timber.DebugTree());
    }
}
