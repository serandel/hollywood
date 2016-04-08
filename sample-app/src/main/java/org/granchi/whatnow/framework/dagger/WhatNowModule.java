package org.granchi.whatnow.framework.dagger;

import android.app.Application;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Actor;
import org.granchi.hollywood.Cast;
import org.granchi.hollywood.Model;
import org.granchi.hollywood.ModelExceptionHandler;
import org.granchi.hollywood.SingleInstanceActorMetadata;
import org.granchi.hollywood.SingleInstanceCast;
import org.granchi.hollywood.android.HollywoodAndroidApplicationCompanion;
import org.granchi.whatnow.StubActor;
import org.granchi.whatnow.WhatNowApplication;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    WhatNowApplication provideWhatNowApplication() {
        return app;
    }

    @Provides
    Application provideApplication() {
        return app;
    }

    @Provides
    Model<SingleInstanceActorMetadata> provideInitialModel() {
        return new Model<SingleInstanceActorMetadata>() {
            @Override
            public Model<SingleInstanceActorMetadata> actUpon(Action action) {
                return this;
            }

            @Override
            public Set<SingleInstanceActorMetadata> getActors() {
                return new HashSet<>(Collections.singletonList(new SingleInstanceActorMetadata(
                        StubActor.class.getName())));
            }
        };
    }

    @Provides
    Cast.Factory<SingleInstanceActorMetadata> provideCastFactory() {
        return models -> new SingleInstanceCast(metadata -> {
            try {
                return (Actor<SingleInstanceActorMetadata>) Class.forName(metadata.getActorClass())
                                                                 .getConstructor()
                                                                 .newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, models);
    }

    @Provides
    ModelExceptionHandler<SingleInstanceActorMetadata> provideExceptionHandler(
            WhatNowApplication app) {
        return (model, action, exception) -> {
            app.showError("Exception");
            return model;
        };
    }

    @Provides
    HollywoodAndroidApplicationCompanion provideCompanion(WhatNowApplication app,
                                                          Model<SingleInstanceActorMetadata> initialModel,
                                                          Cast.Factory<SingleInstanceActorMetadata> castFactory,
                                                          ModelExceptionHandler<SingleInstanceActorMetadata> exceptionHandler) {
        return new HollywoodAndroidApplicationCompanion(app,
                                                        initialModel,
                                                        castFactory,
                                                        exceptionHandler);
    }
}
