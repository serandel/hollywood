package org.granchi.whatnow.framework.dagger;

import org.granchi.whatnow.DebugWhatNowApplication;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component for the debug build variant of the app.
 *
 * @author serandel
 */
@Singleton
@Component(modules = {DebugWhatNowModule.class})
public interface DebugWhatNowComponent extends WhatNowComponent {
    void inject(DebugWhatNowApplication app);
}
