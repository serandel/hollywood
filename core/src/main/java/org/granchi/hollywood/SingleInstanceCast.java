package org.granchi.hollywood;

import rx.Observable;

import java.util.Collection;
import java.util.HashMap;

/**
 * Cast of Actors where there can be no more than one instance of each specific Actor class.
 */
public class SingleInstanceCast<M extends Model<M, SingleInstanceActorMetadata>> extends Cast<SingleInstanceActorMetadata, M>{
    private final Actor.Factory<M, SingleInstanceActorMetadata> factory;

    private HashMap<String, Actor<M>> actors;

    /**
     * Constructor.
     *
     * @param factory factory for Actors
     * @param models Observable for Models
     */
    public SingleInstanceCast(Actor.Factory<M, SingleInstanceActorMetadata> factory, Observable<M> models) {
        super(models);

        if (factory == null) {
            throw new NullPointerException();
        }

        this.factory = factory;

        actors = new HashMap<>();
    }

    /**
     * Build a new Actor.
     *
     * @param metadata metadata for creating the actor
     */
    @Override
    protected Actor<M> buildActorFrom(SingleInstanceActorMetadata metadata) {
        Actor<M> actor = factory.create(metadata);
        actors.put(metadata.getActorClass(), actor);

        return actor;
    }

    /**
     * Check if there is already an Actor built from an specific ActorMetadata.
     *
     * @param metadata metadata
     * @return if there is an Actor from this metadata
     */
    @Override
    protected boolean containsActorFrom(SingleInstanceActorMetadata metadata) {
        return actors.containsKey(metadata.getActorClass());
    }

    @Override
    protected Collection<Actor<M>> getActors() {
        return actors.values();
    }

    @Override
    protected boolean isActorFrom(Actor<M> actor, SingleInstanceActorMetadata metadata) {
        return (metadata != null) && (actor != null) && metadata.getActorClass().equals(actor.getClass().getName());
    }

    @Override
    protected void remove(Actor actor) {
        actors.remove(actor.getClass().getName());
    }
}
