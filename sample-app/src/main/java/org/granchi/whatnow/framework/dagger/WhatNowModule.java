package org.granchi.whatnow.framework.dagger;

import android.app.Application;

import org.granchi.whatnow.WhatNowApplication;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the app.
 *
 * @author serandel
 */
@Module
public class WhatNowModule {
    private final WhatNowApplication app;

    public WhatNowModule(WhatNowApplication app) {
        this.app = app;
    }

    @Provides
    WhatNowApplication getWhatNowApplication() {
        return app;
    }

    @Provides
    Application getApplication() {
        return app;
    }
}
