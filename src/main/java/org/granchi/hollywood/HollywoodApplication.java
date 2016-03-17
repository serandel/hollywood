package org.granchi.hollywood;

import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

import java.util.Collections;

/**
 * Hollywood application.
 * <p>
 * It orchestrates a model (single-threaded, non-blocking, immutable) with a set of different actors (blocking,
 * multi-threaded, mutable). It runs in a loop, where actions from an actor are applied to the model, who returns a
 * (possibly) different model that is applied to every actor.
 * <p>
 * Actions are applied in the same thread, so the model is easy to test and understand.
 *
 * @author serandel
 */

public class HollywoodApplication<M extends Model<M>> {
    private M model;
    // TODO ejem
    private final Cast<ActorMetadata, M> cast;

    private Subject<M, M> models;

    /**
     * Constructor.
     *
     * @param initialModel initial Model, can't be null
     * @param cast         Cast, can't be null
     */
    public HollywoodApplication(M initialModel, Cast cast) {
        if (initialModel == null) {
            throw new NullPointerException();
        }
        if (cast == null) {
            throw new NullPointerException();
        }

        this.model = initialModel;
        this.cast = cast;

        // TODO share?
        models = BehaviorSubject.create();
    }

    /**
     * Runs the application.
     * <p>
     * It enters a infinite loop until Model becomes null.
     */
    public void run() {
        cast.getActions().subscribe(action -> {
            model = model.actUpon(action);

            // Ensure every actor exists, and no one more
            // Cast will complete its getActions Observable when given an empty Actor set, so this subscription will
            // end
            cast.ensureCastExistsConnectedTo(model == null ? Collections.emptySet() : model.getActors(), models);

            cast.apply(model);
        }, throwable -> {
            throwable.printStackTrace();
        });
    }
}
