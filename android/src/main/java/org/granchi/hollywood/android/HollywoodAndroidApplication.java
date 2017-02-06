package org.granchi.hollywood.android;

import android.app.Application;

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
    private HollywoodAndroidApplicationCompanion hollywoodApp;

    @Override
    public void onCreate() {
        super.onCreate();

        hollywoodApp = createHollywoodCompanion();

        hollywoodApp.run();
    }



    /**
     * Creates the HollywoodAndroidApplicationCompanion instance, tipically through whatever
     * dependency injection framework the app is using.
     *
     * @return HollywoodAndroidApplicationCompanion
     */
    protected abstract HollywoodAndroidApplicationCompanion createHollywoodCompanion();

    /**
     * Shows an error to the user.
     *
     * @param msg message
     */
    public abstract void showError(String msg);
}