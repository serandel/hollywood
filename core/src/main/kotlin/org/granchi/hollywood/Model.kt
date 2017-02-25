package org.granchi.hollywood

import kotlin.reflect.KClass

/**
 * Model for a Hollywood application.
 *
 *  * Should not block, nor start any thread
 *  * Should be immutable, so there are no lateral effects from it being passed from actor to
 * actor
 *  * Is called from a single thread, so it will never receive two actions concurrently
 *  * It's ok to return itself from an action, if no change is needed
 *  * It's advisable to implement equals and hashCode
 *
 */
abstract class Model {
    /**
     * Generates a new model, from itself and the data contained in an [action], null to end the
     * application.
     *
     * It is allowed to return itself if the action didn't cause any state change.
     */
    abstract fun actUpon(action: Action): Model?

    /**
     * Returns a collection with itself if it's of a specific [type] or a subclass of it, or, for
     * composite models, the submodels that are compliant with the same condition.
     */
    // Can't be reified because it can't be inline because it's open
    @Suppress("UNCHECKED_CAST")
    open fun <T : Model> getSubmodelsOfType(type: KClass<out T>): Collection<T> {
        if (type.isInstance(this)) {
            return listOf(this as T)
        } else {
            return emptyList()
        }
    }
}