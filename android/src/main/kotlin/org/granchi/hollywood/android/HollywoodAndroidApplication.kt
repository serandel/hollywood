package org.granchi.hollywood.android

import android.app.Application
import org.granchi.hollywood.HollywoodApplication

/**
 * Android application, powered by Hollywood.
 *
 * It's not a [HollywoodApplication] subclass, because of the Android class hierarchy, but uses one
 * by composition.
 *
 * It has to expose itself as a Singleton so the companion class can access to Android application
 * features.
 */
abstract class HollywoodAndroidApplication : Application() {
    private val hollywood: HollywoodApplication by lazy {
        provideHollywoodApp()
    }

    override fun onCreate() {
        super.onCreate()

        hollywood
                .run()
                .subscribe(this::onHollywoodError,
                        this::onHollywoodError,
                        this::onHollywoodFinished)
    }

    /**
     * Provides the [HollywoodApplication] instance, tipically through whatever dependency injection
     * framework the app is using or just creating it.
     */
    protected abstract fun provideHollywoodApp(): HollywoodApplication

    /**
     * Gets called when the [HollywoodApplication] throws an error in its execution cycle.
     *
     * The parameter is a [Throwable], not an [Exception], because the callback is used for the onError
     * of the Exception [Observable] too.
     */
    protected abstract fun onHollywoodError(th: Throwable)

    /**
     * Gets called when the [HollywoodApplication] finishes its execution cycle without error, what,
     * ironically, it's an error itself, because the model becomes frozen forever.
     */
    protected abstract fun onHollywoodFinished()
}