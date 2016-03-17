package org.granchi.hollywood;

import rx.Observable;

import java.util.Set;

/**
 * Stores all the active Actors, creating or removing them according to the ActorMetadata that the model provides.
 *
 * @param <D> type of ActorMetadata it uses for building and identifying Actors
 * @param <M> type of Model the Actors can accept
 */
public abstract class Cast<D extends ActorMetadata, M extends Model> {
    /**
     * Build a new Actor.
     *
     * @param actorMetadata metadata for creating the actor
     * @return actor
     */
    protected abstract Actor<M> buildActorFrom(D actorMetadata, Observable<M> models);

    /**
     * Check if there is already an Actor built from an specific ActorMetadata.
     *
     * @param metadata metadata
     * @return if there is an Actor from this metadata
     */
    protected abstract boolean containsActorFrom(D metadata);

    /**
     * Ensures the cast contains every requested Actor and no more.
     * <p>
     * If an Actor has to be created, it is subscribed to the Model Observable.
     *
     * @param actors metadata for all the desired Actors
     * @param models Observable for the Model changes
     */
    public void ensureCastExistsConnectedTo(Set<D> actors, Observable<M> models) {
        for (D metadata : actors) {
            if (!containsActorFrom(metadata)) {
                buildActorFrom(metadata, models);
            }
        }

        // TODO remove unwanted actors
    }

    /**
     * Gives an Observable with Actions from any active Actor in the Cast.
     *
     * @return Observable for Actions
     */
    public Observable<Action> getActions() {
        return Observable.empty();
    }

    /**
     * Factory for creating Casts.
     *
     * @param <D> type of ActorMetadata it uses for building and identifying Actors
     * @param <M> type of Model the Actors can accept
     */
    @FunctionalInterface
    public interface Factory<D extends ActorMetadata, M extends Model<M, D>> {
        /**
         * Create a Cast, subscribed to an Observable of Models.
         *
         * @param models Observable of Models
         * @return Cast
         */
        Cast<D, M> create(Observable<M> models);
    }
}
