package org.granchi.whatnow

import org.granchi.hollywood.HollywoodApplication
import org.granchi.hollywood.android.HollywoodAndroidApplication
import org.granchi.whatnow.framework.dagger.DaggerWhatNowComponent
import org.granchi.whatnow.framework.dagger.WhatNowModule

import javax.inject.Inject

/**
 * Custom [Application] class for the app.
 *
 * It's open to allow a debug Application inherited from this one.
 */
open class WhatNowApplication : HollywoodAndroidApplication() {
    @Inject
    lateinit var hollywood: HollywoodApplication

    override fun onCreate() {
        // Can't inject directly in onCreate, because DebugWhatNowApplication would call
        // super.onCreate and inject twice
        injectDependencies()

        super.onCreate()
    }

    /**
     * Inject the instance with all the dependencies from a Dagger 2 component.
     */
    private fun injectDependencies() {
        // TODO take this to an extension init or something?
        DaggerWhatNowComponent.builder()
                .whatNowModule(WhatNowModule(this))
                .build()
                .inject(this)
    }

    override fun provideHollywoodApp(): HollywoodApplication {
        return hollywood
    }

    override fun onHollywoodError(th: Throwable) {
        // TODO show a Dialog, Snack, Toast or whatever
    }

    override fun onHollywoodFinished() {
        // TODO show a Dialog, Snack, Toast or whatever
    }
}
