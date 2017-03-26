package org.granchi.whatnow.framework.dagger

import dagger.Component
import org.granchi.whatnow.DebugWhatNowApplication
import javax.inject.Singleton

/**
 * Dagger component for the debug build variant of the app.
 */
@Singleton
@Component(modules = arrayOf(DebugWhatNowModule::class))
interface DebugWhatNowComponent : WhatNowComponent {
    fun inject(app: DebugWhatNowApplication)
}
