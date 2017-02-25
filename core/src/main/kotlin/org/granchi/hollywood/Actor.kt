package org.granchi.hollywood

import io.reactivex.Observable

/**
 * An Actor is a mutable entity that receives models from the application and makes any side effect
 * needed for interfacing the business logic with the outside world.
 *
 * Everything that is blocking or need an specific or new thread must be put out of the model and
 * into an Actor.
 *
 * Examples can be:
 *
 *  * GUI
 *  * database
 *  * external server
 */
interface Actor {
    /**
     * Return an Observable with all future [actions].
     */
    val actions: Observable<Action>

    /**
     * Subscribes to all future [models], so it can adapt to changes in the business logic.
     */
    fun subscribeTo(models: Observable<Model>)
}
