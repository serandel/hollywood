package org.granchi.hollywood.android;

import android.app.Application;

import org.granchi.hollywood.HollywoodApplication;

/**
 * Android application, powered by Hollywood.
 * <p>
 * It's not a HollywoodApplication subclass, because of the Android class hierarchy, but uses one
 * by composition.
 * <p>
 * It has to expose itself as a Singleton so the companion class can access to Android application
 * features.
 *
 * @author serandel
 */
public abstract class HollywoodAndroidApplication extends Application {
    private HollywoodApplication hollywood;

    @Override
    public void onCreate() {
        super.onCreate();

        hollywood = provideHollywoodApp();

        hollywood.run()
                 .subscribe(this::onHollywoodError,
                            this::onHollywoodError,
                            this::onHollywoodFinished);
    }

    /**
     * Provides the HollywoodApplication instance, tipically through whatever dependency injection
     * framework the app is using or just creating it.
     *
     * @return HollywoodApplication
     */
    protected abstract HollywoodApplication provideHollywoodApp();

    /**
     * Gets called when the HollywoodApplication throws an error in its execution cycle.
     * <p>
     * The parameter is a Throwable, not an Exception, because the callback is used for the onError
     * of the Exception Observable too.
     *
     * @param th throwable
     */
    protected abstract void onHollywoodError(Throwable th);

    /**
     * Gets called when the HollywoodApplication finishes its execution cycle without error, what,
     * ironically, it's an error itself, because the model becomes frozen forever.
     */
    protected abstract void onHollywoodFinished();
}