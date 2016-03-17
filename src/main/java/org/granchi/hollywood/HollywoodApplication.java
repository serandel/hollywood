package org.granchi.hollywood;

import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

import java.util.Collections;

/**
 * Hollywood application.
 * <p>
 * It orchestrates a Model (single-threaded, non-blocking, immutable) with a set of different Actors (blocking,
 * multi-threaded, mutable). It runs in a loop, where actions from an actor are applied to the model, who returns a
 * (possibly) different model that is applied to every actor.
 * <p>
 * Actions are applied in the same thread, so the model is easy to test and understand.
 *
 * @param <M> type of the Model
 * @param <D> type of the ActorMetadata used to build Actors
 * @author serandel
 */
// TODO hook actions subscribe to app lifecycle
public class HollywoodApplication<M extends Model<M, D>, D extends ActorMetadata> {
    private M model;
    private final Cast<D, M> cast;

    private Subject<M, M> models;

    /**
     * Constructor.
     *
     * @param initialModel initial Model, can't be null
     * @param castFactory  factory for Cast, can't be null
     */
    public HollywoodApplication(M initialModel, Cast.Factory<D, M> castFactory) {
        if (initialModel == null) {
            throw new NullPointerException();
        }
        if (castFactory == null) {
            throw new NullPointerException();
        }

        this.model = initialModel;

        // TODO share?
        models = BehaviorSubject.create();

        cast = castFactory.create(models);
        if (cast == null) {
            throw new IllegalStateException("Cast null");
        }
    }

    /**
     * Runs the application.
     * <p>
     * It enters a infinite loop until Model becomes null.
     */
    public void run() {
        // Have to create the initial set of Actors
        cast.ensureCastExistsConnectedTo(model.getActors(), models);

        // Feed them initial model
        models.onNext(model);

        // And from now on...
        cast.getActions().subscribe(action -> {
            model = model.actUpon(action);

            // Ensure every actor exists, and no one more
            // Cast will complete its getActions Observable when given an empty Actor set, so this subscription will
            // end
            cast.ensureCastExistsConnectedTo(model == null ? Collections.emptySet() : model.getActors(), models);

            models.onNext(model);

            // TODO something with null model or empty actors
        }, throwable -> {
            throwable.printStackTrace();
        });
    }
}
