package org.granchi.whatnow;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Actor;
import org.granchi.hollywood.Model;
import org.granchi.hollywood.SingleInstanceActorMetadata;
import org.granchi.hollywood.SingleInstanceCast;
import org.granchi.hollywood.android.HollywoodAndroidApplication;
import org.granchi.hollywood.android.HollywoodAndroidApplicationCompanion;

import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

/**
 * Custom Application class for the app.
 *
 * @author serandel
 */
public class WhatNowApplication extends HollywoodAndroidApplication {
    @Override
    protected HollywoodAndroidApplicationCompanion createHollywoodCompanion() {
        return new HollywoodAndroidApplicationCompanion(new Model<SingleInstanceActorMetadata>() {
            @Override
            public Model<SingleInstanceActorMetadata> actUpon(Action action) {
                return this;
            }

            @Override
            public Set<SingleInstanceActorMetadata> getActors() {
                return new HashSet<>(Collections.singletonList(new SingleInstanceActorMetadata("org.granchi.whatnow.MockActor")));
            }
        }, metadata -> {
            return new SingleInstanceCast((Actor.Factory<SingleInstanceActorMetadata>) metadata1 -> {
                try {
                    return (Actor<SingleInstanceActorMetadata>) Class.forName(metadata1.getActorClass()).getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, Observable < Model < SingleInstanceActorMetadata >>.empty());
        }, (model, action, exception) -> {
            showError("Exception");
            return model;
        });
    }

    @Override
    public void showError(String msg) {
        // TODO snackbar, toast or whatever
    }
}