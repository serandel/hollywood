package org.granchi.hollywood;

import rx.Observable;

/**
 * An Actor is a mutable entity that receives models from the application and makes any side effect
 * needed for interfacing the business logic with the outside world.
 * <p>
 * Everything that is blocking or need an specific or new thread must be put out of the model and
 * into an Actor.
 * <p>
 * Examples can be:
 * <ul>
 * <li>GUI</li>
 * <li>database</li>
 * <li>external server</li>
 * </ul>
 *
 * @author serandel
 */
public interface Actor {
    /**
     * Return an Observable with all future Actions.
     *
     * @return actions
     */
    Observable<Action> getActions();

    /**
     * Subscribes to all future Models, so it can adapt to changes in the business logic.
     *
     * @param models Models
     */
    void subscribeTo(Observable<Model> models);
}