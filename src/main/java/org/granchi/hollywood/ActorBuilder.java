package org.granchi.hollywood;

import rx.Observable;
import rx.subjects.Subject;

import java.util.Set;

/**
 * Creates Actors from ActorMetadata.
 *
 * @param <M> type of ActorMetadata it uses for building Actors
 * @param <N> type of Model the Actors can accept
 */
public abstract class ActorBuilder<M extends ActorMetadata, N extends Model> {
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
     *
     * If an Actor has to be created, it is subscribed to the Model Observable.
     *
     * @param actors metadata for all the desired Actors
     * @param models Observable for the Model changes
     */
    void ensureCastExistsConnectedTo(Set<M> actors, Observable<N> models) {
        for (M metadata : actors) {
            if (!containsActorFrom(metadata)) {
                buildActorFrom(metadata, models);
            }
        }

        // TODO remove unwanted actors
    }
}
