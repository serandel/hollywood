package org.granchi.hollywood;

import rx.Observable;

/**
 * An Actor is a mutable entity that receives models from the application and makes any side effect needed for
 * interfacing the business logic with the outside world.
 * <p>
 * Everything that is blocking or need an specific or new thread must be put out of the model and into an Actor.
 * <p>
 * Examples can be:
 * <ul>
 * <li>GUI</li>
 * <li>database</li>
 * <li>external server</li>
 * </ul>
 *
 * @param <D> type of metadata the Actor is built from
 *
 * @author serandel
 */
public interface Actor<D extends ActorMetadata> {
    /**
     * Return an Observable with all future Actions.
     *
     * @return actions
     */
    Observable<Action> getActions();

    /**
     * Subscribes to all future Models, so it can adapt to changes in the business logic.
     *
     * The Models can't be given in the constructor, because some Actors will be built by the SO, like Android views.
     *
     * @param models Models
     */
    void subscribeTo(Observable<Model<D>> models);

    /**
     * Factory for creating Actors.
     *
     * @param <D> type of ActorMetadata it uses for building and identifying Actors
     */
    @FunctionalInterface
    interface Factory<D extends ActorMetadata> {
        /**
         * Create an Actor, subscribed to an Observable of Models.
         *
         * @param metadata metadata
         * @return Actor
         */
        Actor<D> create(D metadata);
    }
}