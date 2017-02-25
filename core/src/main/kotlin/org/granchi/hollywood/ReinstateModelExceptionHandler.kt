package org.granchi.hollywood

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * [ModelExceptionHandler] that keeps the same [model] that threw the [exception], discarding the [action].
 */
class ReinstateModelExceptionHandler(
        private val log: Logger
        = LoggerFactory.getLogger(ReinstateModelExceptionHandler::class.qualifiedName))
    : ModelExceptionHandler {

    override fun onException(model: Model, action: Action, exception: Exception): Model? {
        log.debug("Reinstating model {}", model)

        return model
    }
}
