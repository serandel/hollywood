package org.granchi.hollywood;

/**
 * Creates Actors from ActorMetadata.
 */
@FunctionalInterface
public interface ActorBuilder {
    /**
     * Build a new Actor.
     *
     * @param actorMetadata metadata for creating the actor
     * @return actor
     */
    Actor<?> build(ActorMetadata actorMetadata);
}
