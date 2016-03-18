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
    // TODO share?
    private final Observable<M> models;

    /**
     * Constructor.
     *
     * @param models Observable for Models
     */
    protected Cast(Observable<M> models) {
        if (models == null) {
            throw new NullPointerException();
        }

        this.models = models;
    }

    /**
     * Build a new Actor.
     *
     * @param metadata metadata for creating the actor
     */
    protected abstract Actor<M> buildActorFrom(D metadata);

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
     */
    public void ensureCast(Set<D> actors) {
        for (D metadata : actors) {
            if (!containsActorFrom(metadata)) {
                buildActorFrom(metadata).subscribeTo(models);
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
        Cast<D, M> build(Observable<M> models);
    }
}