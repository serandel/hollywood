package org.granchi.whatnow;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Actor;
import org.granchi.hollywood.Model;
import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;

/**
 * @author serandel
 */

public class StubActor implements Actor {
    @NotNull
    @Override
    public Observable<Action> getActions() {
        return Observable.never();
    }

    @Override
    public void subscribeTo(@NotNull Observable<Model> models) {

    }
}
