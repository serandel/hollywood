package org.granchi.whatnow.framework.dagger;

import android.app.Application;

import org.granchi.hollywood.Actor;
import org.granchi.hollywood.CompositeModel;
import org.granchi.hollywood.HollywoodApplication;
import org.granchi.hollywood.Model;
import org.granchi.hollywood.ModelExceptionHandler;
import org.granchi.hollywood.ReinstateModelExceptionHandler;
import org.granchi.whatnow.StubActor;
import org.granchi.whatnow.TasksModel;
import org.granchi.whatnow.WhatNowApplication;
import org.granchi.whatnow.WhatNowPreferencesModel;

import java.util.Arrays;
import java.util.Collection;

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
    Model provideInitialModel(TasksModel tasksModel,
                              WhatNowPreferencesModel preferencesModel) {
        return new CompositeModel(tasksModel, preferencesModel);
    }

    @Provides
    HollywoodApplication provideHollywoodApp(Model initialModel,
                                             Collection<Actor> actors,
                                             ModelExceptionHandler exceptionHandler) {
        return new HollywoodApplication(initialModel, actors, exceptionHandler);
    }

//
//    @Provides
//    Crew.Factory<SingleInstanceActorMetadata> provideCrewFactory() {
//        return models -> new SingleInstanceCrew(metadata -> {
//            try {
//                return (Actor<SingleInstanceActorMetadata>) Class.forName(metadata
// .getActorClass())
//                                                                 .getConstructor()
//                                                                 .newInstance();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }, models);
//    }
//
//    @Provides
//    ModelExceptionHandler provideExceptionHandler(
//            WhatNowApplication app) {
//        return (model, action, exception) -> {
//            app.showError("Exception");
//            return model;
//        };
//    }

    @Provides
    Collection<Actor> provideActors() {
        return Arrays.asList(new StubActor());
    }

    @Provides
    ModelExceptionHandler provideModelExceptionHandler() {
        return new ReinstateModelExceptionHandler();
    }
}
