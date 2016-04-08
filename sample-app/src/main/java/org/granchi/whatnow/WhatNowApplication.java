package org.granchi.whatnow;

import org.granchi.hollywood.android.HollywoodAndroidApplication;
import org.granchi.hollywood.android.HollywoodAndroidApplicationCompanion;
import org.granchi.whatnow.framework.dagger.DaggerWhatNowComponent;
import org.granchi.whatnow.framework.dagger.WhatNowModule;

import javax.inject.Inject;

/**
 * Custom Application class for the app.
 *
 * @author serandel
 */
public class WhatNowApplication extends HollywoodAndroidApplication {
    @Inject
    HollywoodAndroidApplicationCompanion companion;

    @Override
    public void onCreate() {
        // Can't inject directly in onCreate, because DebugWhatNowApplication would call
        // super.onCreate and inject twice
        injectDependencies();

        super.onCreate();
    }

    /**
     * Inject the instance with all the dependencies from a Dagger 2 component
     */
    protected void injectDependencies() {
        DaggerWhatNowComponent.builder()
                              .whatNowModule(new WhatNowModule(this))
                              .build()
                              .inject(this);
    }

    @Override
    protected HollywoodAndroidApplicationCompanion createHollywoodCompanion() {
        return companion;
    }

    @Override
    public void showError(String msg) {
        // TODO snackbar, toast or whatever
    }
}