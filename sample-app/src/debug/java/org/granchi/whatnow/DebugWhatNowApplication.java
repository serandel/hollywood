package org.granchi.whatnow;

import org.granchi.whatnow.framework.dagger.DaggerDebugWhatNowComponent;
import org.granchi.whatnow.framework.dagger.DebugWhatNowModule;

/**
 * Custom Application class for the debug build variant of the app, with logging enabled.
 *
 * @author serandel
 */
public class DebugWhatNowApplication extends WhatNowApplication {
    /**
     * Inject the instance with all the dependencies from a Dagger 2 component
     */
    protected void injectDependencies() {
        DaggerDebugWhatNowComponent.builder()
                .debugWhatNowModule(new DebugWhatNowModule(this))
                .build()
                .inject(this);
    }
}