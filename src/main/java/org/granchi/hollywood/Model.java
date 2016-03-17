package org.granchi.hollywood;

import java.util.Set;

/**
 * Model for a Hollywood application.
 * <p>
 * <ul>
 * <li>Maintains a list of actors that should be alive in every moment</li>
 * <li>Should not block, nor start any thread</li>
 * <li>Should be immutable, so there are no lateral effects from it being passed from actor to actor</li>
 * <li>Is called from a single thread, so it will never receive two actions concurrently</li>
 * <li>Is ok to return itself from an action, if no change is needed</li>
 * </ul>
 *
 * @param <M> type of Model output by this one; the usual usage will be creating a subclass like <code>public class
 *            MyModel implements Model<MyModel></code>
 * @param <D> type of the ActorMetadata used to build Actors
 * @author serandel
 */
public interface Model<M extends Model, D extends ActorMetadata> {
    /**
     * Generates a new model, from itself and the data contained in an action.
     * <p>
     * It is allowed to return itself if the action didn't cause any state change.
     *
     * @param action action, can't be null
     * @return new model
     */
    M actUpon(Action action);

    /**
     * Gets the list of Actors the model wants to have in this iteration of the app cycle, as a collection of
     * ActorMetadata that can be used to buildActorFrom which ones are missing.
     *
     * @return list of actors
     */
    Set<D> getActors();
}
