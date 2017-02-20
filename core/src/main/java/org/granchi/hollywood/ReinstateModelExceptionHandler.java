package org.granchi.hollywood;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ModelExceptionHandler that keeps the same model that threw the exception, discarding the action.
 *
 * @author serandel
 */
public class ReinstateModelExceptionHandler implements ModelExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(HollywoodApplication.class);

    @Override
    public Model onException(Model model, Action action, Exception exception) {
        log.debug("Reinstating model {}", model);

        return model;
    }
}
