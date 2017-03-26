package org.granchi.whatnow.framework.dagger

import dagger.Module
import org.granchi.whatnow.DebugWhatNowApplication

/**
 * Dagger module for the debug build variant of the app.
 */
@Module
class DebugWhatNowModule(app: DebugWhatNowApplication) : WhatNowModule(app)
