package org.granchi.whatnow;

import org.granchi.whatnow.framework.dagger.DaggerDebugWhatNowComponent;
import org.granchi.whatnow.framework.dagger.DebugWhatNowComponent;
import org.granchi.whatnow.framework.dagger.DebugWhatNowModule;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Custom Application class for the debug build variant of the app, with logging enabled.
 *
 * @author serandel
 */
public class DebugWhatNowApplication extends WhatNowApplication {
    @Inject
    List<Timber.Tree> loggingTrees;
    // Dagger 2 component
    private DebugWhatNowComponent component;

    @Override
    public void onCreate() {
        component = DaggerDebugWhatNowComponent.builder()
                .debugWhatNowModule(new DebugWhatNowModule(this))
                .build();
        component.inject(this);

        super.onCreate();
    }

    @Override
    protected List<Timber.Tree> getLoggingTrees() {
        return loggingTrees;
    }
}