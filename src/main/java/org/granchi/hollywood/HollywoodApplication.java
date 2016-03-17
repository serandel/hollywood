package org.granchi.hollywood;

import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

/**
 * Hollywood application.
 * <p>
 * It orchestrates a model (single-threaded, non-blocking, immutable) with a set of different actors (blocking,
 * multi-threaded, mutable). It runs in a loop, where actions from an actor are applied to the model, who returns a
 * (possibly) different model that is applied to every actor.
 *
 * Actions are applied in the same thread, so the model is easy to test and understand.
 *
 * @author serandel
 */

public class HollywoodApplication<M extends Model<M>> {
    private M model;
    private final ActorBuilder actorBuilder;

    private Subject<M, M> models;

    /**
     * Constructor.
     *
     * @param initialModel initial Model, can't be null
     * @param actorBuilder ActorBuilder, can't be null
     */
    public HollywoodApplication(M initialModel, ActorBuilder actorBuilder) {
        if (initialModel == null) {
            throw new NullPointerException();
        }
        if (actorBuilder == null) {
            throw new NullPointerException();
        }

        this.model = initialModel;
        this.actorBuilder = actorBuilder;

        // TODO share?
        models = BehaviorSubject.create();
    }

    /**
     * Runs the application.
     *
     * It enters a infinite loop until Model becomes null.
     */
    public void run() {
        while(model != null) {
            // Ensure every actor exists, and no one more
            actorBuilder.ensureCastExistsConnectedTo(model.getActors(), models);



        }

    }
}
