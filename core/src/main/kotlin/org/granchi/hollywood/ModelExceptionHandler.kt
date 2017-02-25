package org.granchi.hollywood

/**
 * Handler for reacting to exceptions during [Model.actUpon(Action)].
 *
 * Without it, the application would end if an Exception happens.
 */
interface ModelExceptionHandler {
    /**
     * Return the next [Model] to use in the application, or null to end it, when an Exception has
     * ocurred during [Model.actUpon(Action)].
     */
    fun onException(model: Model, action: Action, exception: Exception): Model?
}
