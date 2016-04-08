package org.granchi.whatnow;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Actor;
import org.granchi.hollywood.Model;
import org.granchi.hollywood.SingleInstanceActorMetadata;

import rx.Observable;
import timber.log.Timber;

/**
 * Stub Actor, just for using it so the app runs while building real ones.
 *
 * @author serandel
 */
public class StubActor implements Actor<SingleInstanceActorMetadata> {
    @Override
    public Observable<Action> getActions() {
        return Observable.never();
    }

    @Override
    public void subscribeTo(Observable<Model<SingleInstanceActorMetadata>> models) {
        // Very well
        models.subscribe(model -> Timber.i("Model received"));
    }
}
