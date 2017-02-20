package org.granchi.hollywood;

import java.util.Collection;
import java.util.Collections;

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
public abstract class Model {
    /**
     * Generates a new model, from itself and the data contained in an action.
     * <p>
     * It is allowed to return itself if the action didn't cause any state change.
     *
     * @param action action, can't be null
     * @return new model
     */
    protected abstract Model actUpon(Action action);

    /**
     * Returns itself if it's of a specific class or a subclass of it, or, for composite models, the
     * submodels that are compliant with the same condition.
     *
     * @param type desired class
     * @param <T>  type of the desired class
     * @return collection of submodels of that class
     */
    @SuppressWarnings("unchecked")
    public <T extends Model> Collection<T> getSubmodelsOfType(Class<T> type) {
        if (type.isAssignableFrom(this.getClass())) {
            return Collections.singletonList((T) this);
        } else {
            return Collections.emptyList();
        }
    }
}