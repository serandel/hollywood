package org.granchi.whatnow.framework.dagger;

import android.app.Application;

import org.granchi.hollywood.Actor;
import org.granchi.hollywood.CompositeModel;
import org.granchi.hollywood.Crew;
import org.granchi.hollywood.Model;
import org.granchi.hollywood.ModelExceptionHandler;
import org.granchi.hollywood.SingleInstanceActorMetadata;
import org.granchi.hollywood.SingleInstanceCrew;
import org.granchi.hollywood.android.HollywoodAndroidApplicationCompanion;
import org.granchi.whatnow.TasksModel;
import org.granchi.whatnow.WhatNowApplication;
import org.granchi.whatnow.WhatNowPreferencesModel;

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
    Model<SingleInstanceActorMetadata> provideInitialModel(TasksModel tasksModel,
                                                           WhatNowPreferencesModel preferencesModel) {
        return new CompositeModel<>(tasksModel, preferencesModel);
    }

    @Provides
    Crew.Factory<SingleInstanceActorMetadata> provideCrewFactory() {
        return models -> new SingleInstanceCrew(metadata -> {
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
                                                          Crew.Factory<SingleInstanceActorMetadata> crewFactory,
                                                          ModelExceptionHandler<SingleInstanceActorMetadata> exceptionHandler) {
        return new HollywoodAndroidApplicationCompanion(app,
                                                        initialModel,
                                                        crewFactory,
                                                        exceptionHandler);
    }
}
