package org.granchi.hollywood;

import rx.Observable;

/**
 * An Actor is a mutable entity that receives models from the application and makes any side effect needed for
 * interfacing the business logic with the outside world.
 * <p>
 * Everything that is blocking or need an specific or new thread must be put out of the model and into an Actor.
 * <p>
 * Examples can be:
 * <ul>
 * <li>GUI</li>
 * <li>database</li>
 * <li>external server</li>
 * </ul>
 *
 * @param <M> type of model that can be applied
 * @author serandel
 */
public interface Actor<M extends Model> {
    /**
     * Return an Observable with all future Actions.
     *
     * @return actions
     */
    Observable<Action> getActions();

    /**
     * Applies a Model to the Actor, so it can adapt to changes in the business logic.
     *
     * @param model model
     */
    void apply(M model);
}
