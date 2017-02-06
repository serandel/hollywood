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

        hollywood.getExceptions().subscribe();

        hollywood.run();
    }

    /**
     * Provides the HollywoodApplication instance, tipically through whatever dependency injection
     * framework the app is using or just creating it.
     *
     * @return HollywoodApplication
     */
    protected abstract HollywoodApplication provideHollywoodApp();

    /**
     * Shows an error to the user.
     *
     * @param exception exception
     */
    public abstract void showError(Exception exception);
}