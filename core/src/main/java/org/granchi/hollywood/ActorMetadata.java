package org.granchi.hollywood;

/**
 * Metadata used to create an Actor.
 * <p>
 * It's what a Model outputs when it wants to integrate with a Actor, so the HollywoodApplication can buildActorFrom it.
 *
 * @param <R> type of Roster that defines all the possible types of Actors
 *
 * @author serandel
 */
public interface ActorMetadata<R extends Roster> {

    // TODO kill?
//    /**
//     * Return the Roster this metadata represents.
//     *
//     * @return Roster
//     */
//    R getRoster();
}