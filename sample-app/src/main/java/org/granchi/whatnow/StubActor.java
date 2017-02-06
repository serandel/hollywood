package org.granchi.whatnow;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Actor;
import org.granchi.hollywood.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Observable;

/**
 * Stub Actor, just for using it so the app runs while building real ones.
 *
 * @author serandel
 */
public class StubActor implements Actor {
    private static final Logger log = LoggerFactory.getLogger(StubActor.class);

    @Override
    public Observable<Action> getActions() {
        return Observable.never();
    }

    @Override
    public void subscribeTo(Observable<Model> models) {
        models.subscribe(model -> log.info("Model received: {}", model));
    }
}
