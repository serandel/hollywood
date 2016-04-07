package org.granchi.whatnow;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Actor;
import org.granchi.hollywood.Cast;
import org.granchi.hollywood.Model;
import org.granchi.hollywood.ModelExceptionHandler;
import org.granchi.hollywood.SingleInstanceActorMetadata;
import org.granchi.hollywood.SingleInstanceCast;
import org.granchi.hollywood.android.HollywoodAndroidApplication;
import org.granchi.hollywood.android.HollywoodAndroidApplicationCompanion;
import org.granchi.whatnow.framework.dagger.DaggerWhatNowComponent;
import org.granchi.whatnow.framework.dagger.WhatNowComponent;
import org.granchi.whatnow.framework.dagger.WhatNowModule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom Application class for the app.
 *
 * @author serandel
 */
public class WhatNowApplication extends HollywoodAndroidApplication {
    // Dagger 2 component
    private WhatNowComponent component;

    @Override
    public void onCreate() {
        component = DaggerWhatNowComponent.builder()
                .whatNowModule(new WhatNowModule(this))
                .build();
        component.inject(this);

        super.onCreate();
    }

    @Override
    protected HollywoodAndroidApplicationCompanion createHollywoodCompanion() {
        Model<SingleInstanceActorMetadata> initialModel = new Model<SingleInstanceActorMetadata>() {
            @Override
            public Model<SingleInstanceActorMetadata> actUpon(Action action) {
                return this;
            }

            @Override
            public Set<SingleInstanceActorMetadata> getActors() {
                return new HashSet<>(Collections.singletonList(new SingleInstanceActorMetadata(StubActor.class.getName())));
            }
        };

        Cast.Factory<SingleInstanceActorMetadata> castFactory = models -> new SingleInstanceCast(metadata -> {
            try {
                return (Actor<SingleInstanceActorMetadata>) Class.forName(metadata.getActorClass()).getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, models);

        ModelExceptionHandler<SingleInstanceActorMetadata> exceptionHandler = (model, action, exception) -> {
            showError("Exception");
            return model;
        };

        return new HollywoodAndroidApplicationCompanion(this, initialModel, castFactory, exceptionHandler);
    }

    @Override
    public void showError(String msg) {
        // TODO snackbar, toast or whatever
    }
}