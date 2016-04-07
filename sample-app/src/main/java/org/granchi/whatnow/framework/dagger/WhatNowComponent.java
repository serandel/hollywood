package org.granchi.whatnow.framework.dagger;

import org.granchi.whatnow.WhatNowApplication;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component for the app.
 *
 * @author serandel
 */
@Singleton
@Component(modules = {WhatNowModule.class})
public interface WhatNowComponent {
    void inject(WhatNowApplication app);
}
