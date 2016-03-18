package org.granchi.hollywood;

/**
 * Metadata for Actors that are just identified by their classes. There can only be a single instance of each specific
 * Actor class.
 */
// TODO parameters
public class SingleInstanceActorMetadata implements ActorMetadata {
    private final String actorClass;

    /**
     * Constructor.
     *
     * @param actorClass name of the class of the Actor, must be fully qualified
     */
    public SingleInstanceActorMetadata(String actorClass) {
        this.actorClass = actorClass;
    }

    /**
     * Return the class of the Actor it represents.
     *
     * @return class of the Actor
     */
    public String getActorClass() {
        return actorClass;
    }
}
