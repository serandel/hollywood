package org.granchi.hollywood;

import rx.Observable;

import java.util.Collection;
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
     * @param metadatas metadata for all the desired Actors
     */
    public void ensureCast(Set<D> metadatas) {
        for (D metadata : metadatas) {
            if (!containsActorFrom(metadata)) {
                buildActorFrom(metadata).subscribeTo(models);
            }
        }

        // Remove unwanted ones
        Collection<Actor<M>> actors = getActors();
        if (actors.size() != metadatas.size()) {
            for (Actor<M> actor : actors) {
                boolean wanted = false;

                for (D metadata : metadatas) {
                    if (isActorFrom(actor, metadata)) {
                        wanted = true;
                        break;
                    }
                }

                if (!wanted) {
                    remove(actor);
                }
            }
        }
    }

    /**
     * Returns the collection of created Actors.
     *
     * @return Actors
     */
    protected abstract Collection<Actor<M>> getActors();

    /**
     * Says if an Actor has been created from a specific Metadata.
     *
     * @param actor Actor
     * @param metadata Metadata
     * @return if the Actor was created from the Metadata
     */
    protected abstract boolean isActorFrom(Actor<M> actor, D metadata);

    /**
     * Removes an Actor from the Cast.
     *
     * @param actor Actor
     */
    protected abstract void remove(Actor actor);

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
     * <p>
     * It is needed, because we want the application to create the Models observable to give to the Cast in its
     * constructor.
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