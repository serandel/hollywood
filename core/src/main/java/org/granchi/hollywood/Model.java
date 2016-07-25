package org.granchi.hollywood;

/**
 * Model for a Hollywood application.
 * <p>
 * <ul>
 * <li>Should not block, nor start any thread</li>
 * <li>Should be immutable, so there are no lateral effects from it being passed from actor to
 * actor</li>
 * <li>Is called from a single thread, so it will never receive two actions concurrently</li>
 * <li>Is ok to return itself from an action, if no change is needed</li>
 * </ul>
 *
 * @author serandel
 */
public interface Model {
    /**
     * Generates a new model, from itself and the data contained in an action.
     * <p>
     * It is allowed to return itself if the action didn't cause any state change.
     *
     * @param action action, can't be null
     * @return new model
     */
    Model actUpon(Action action);
}