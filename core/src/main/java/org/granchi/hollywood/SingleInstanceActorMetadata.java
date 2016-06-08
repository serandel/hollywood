package org.granchi.hollywood;

/**
 * Metadata for Actors that are just identified by their classes. There can only be a single instance of each specific
 * Actor class.
 *
 * @param <R> type of Roster that defines all the possible types of Actors
 */
// TODO parameters
// TODO remove this subclass and put everything in the base class?
public class SingleInstanceActorMetadata<R extends Roster> implements ActorMetadata<R> {
    // TODO kill this?
    private final R roster;

    /**
     * Constructor.
     *
     * @param roster type of Actor from the Roster
     */
    // TODO kill this?
    public SingleInstanceActorMetadata(R roster) {
        this.roster = roster;
    }
}
