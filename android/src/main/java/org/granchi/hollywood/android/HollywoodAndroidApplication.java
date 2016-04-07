package org.granchi.hollywood.android;

import android.app.Application;

import java.util.List;

import timber.log.Timber;

/**
 * Android application, powered by Hollywood.
 * <p>
 * It's not a HollywoodApplication subclass, because of the Android class hierarchy, but uses one
 * by composition.
 * <p>
 * It has to expose itself as a Singleton so the companion class can access to Android application features.
 *
 * @author serandel
 */
public abstract class HollywoodAndroidApplication extends Application {
    private static HollywoodAndroidApplication instance;
    private HollywoodAndroidApplicationCompanion hollywoodApp;

    public static HollywoodAndroidApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        initializeLogging();

        hollywoodApp = createHollywoodCompanion();

        hollywoodApp.run();
    }

    /**
     * Initializes logger via Timber.
     */
    private void initializeLogging() {
        List<Timber.Tree> trees = getLoggingTrees();

        if (trees != null && !trees.isEmpty()) {
            for (Timber.Tree tree : trees) {
                Timber.plant(tree);
            }

            Timber.i("Logging initialized");
        }
    }


    /**
     * Gets the list of Timber trees, to initialize the logging system.
     *
     * @return list of trees, null or empty to disable logging
     */
    protected List<Timber.Tree> getLoggingTrees() {
        // Nothing by default
        return null;
    }

    /**
     * Creates the HollywoodAndroidApplicationCompanion instance, tipically through whatever dependency injection framework the app is using.
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