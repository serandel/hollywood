package org.granchi.hollywood;

import rx.Observable;
import rx.subjects.Subject;

import java.util.Set;

/**
 * Stores all the active Actors, creating or removing them according to the ActorMetadata that the model provides.
 *
 * @param <M> type of ActorMetadata it uses for building and identifying Actors
 * @param <N> type of Model the Actors can accept
 */
public abstract class Cast<M extends ActorMetadata, N extends Model> {
    /**
     * Build a new Actor.
     *
     * @param actorMetadata metadata for creating the actor
     * @return actor
     */
    protected abstract Actor<N> buildActorFrom(M actorMetadata, Observable<N> models);

    /**
     * Check if there is already an Actor built from an specific ActorMetadata.
     *
     * @param metadata metadata
     * @return if there is an Actor from this metadata
     */
    protected abstract boolean containsActorFrom(ActorMetadata metadata);

    /**
     * Ensures the cast contains every requested Actor and no more.
     * <p>
     * If an Actor has to be created, it is subscribed to the Model Observable.
     *
     * @param actors metadata for all the desired Actors
     * @param models Observable for the Model changes
     */
    public void ensureCastExistsConnectedTo(Set<M> actors, Observable<N> models) {
        for (M metadata : actors) {
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
     * Applies a new Model to every active Actor.
     *
     * @param model Model
     */
    public void apply(N model) {

    }


    /**
     * Says if there is no active Actors.
     *
     * @return if there are no active Actors
     */
    public boolean isEmpty() {
        return true;
    }


}
