package org.granchi.whatnow

import org.granchi.whatnow.framework.dagger.DaggerDebugWhatNowComponent
import org.granchi.whatnow.framework.dagger.DebugWhatNowModule

/**
 * Custom Application class for the debug build variant of the app.
 */
// TODO do we need this?
class DebugWhatNowApplication : WhatNowApplication() {
    /**
     * Inject the instance with all the dependencies from a Dagger 2 component
     */
    fun injectDependencies() {
        DaggerDebugWhatNowComponent.builder()
                .debugWhatNowModule(DebugWhatNowModule(this))
                .build()
                .inject(this)
    }
}