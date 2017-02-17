package org.granchi.whatnow.framework.dagger;

import org.granchi.whatnow.DebugWhatNowApplication;

import dagger.Module;

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
}
