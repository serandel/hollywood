package org.granchi.hollywood;

import java.util.Collection;
import java.util.HashMap;

import rx.Observable;

/**
 * Crew of Actors where there can be no more than one instance of each specific Actor class.
 *
 * @param <R> type of Roster that defines all the possible types of Actors
 * @param <D> type of ActorMetadata it uses for building and identifying Actors
 */
// TODO kill and take the singleInstance flag to ActorMetadata?
public class SingleInstanceCrew<R extends Roster, D extends ActorMetadata<R>> extends Crew<R, D> {
    private final Actor.Factory<SingleInstanceActorMetadata> factory;

    private HashMap<String, Actor<SingleInstanceActorMetadata>> actors;

    /**
     * Constructor.
     *
     * @param factory factory for Actors
     * @param models Observable for Models
     */
    public SingleInstanceCrew(Actor.Factory<SingleInstanceActorMetadata> factory,
                              Observable<Model<SingleInstanceActorMetadata>> models) {
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
    protected Actor<SingleInstanceActorMetadata> buildActorFrom(SingleInstanceActorMetadata metadata) {
        Actor<SingleInstanceActorMetadata> actor = factory.create(metadata);
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
    protected Collection<Actor<SingleInstanceActorMetadata>> getActors() {
        return actors.values();
    }

    @Override
    protected boolean isActorFrom(Actor<SingleInstanceActorMetadata> actor, SingleInstanceActorMetadata metadata) {
        return (metadata != null) && (actor != null) && metadata.getActorClass().equals(actor.getClass().getName());
    }

    @Override
    protected void remove(Actor actor) {
        actors.remove(actor.getClass().getName());
    }
}