package org.granchi.whatnow.framework.dagger

import dagger.Component
import org.granchi.whatnow.WhatNowApplication
import javax.inject.Singleton

/**
 * Dagger component for the app.
 */
@Singleton
@Component(modules = arrayOf(WhatNowModule::class))
interface WhatNowComponent {
    fun inject(app: WhatNowApplication)
}
