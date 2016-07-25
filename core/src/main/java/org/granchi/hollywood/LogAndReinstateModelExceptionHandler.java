package org.granchi.hollywood;

/**
 * ModelExceptionHandler that logs that the exception has happened and keeps the same model, discarding the action.
 *
 * @author serandel
 */
public class LogAndReinstateModelExceptionHandler implements ModelExceptionHandler {
    private final Logger logger;

    /**
     * Constructor.
     *
     * @param logger Logger to log when an Exception happens
     */
    public LogAndReinstateModelExceptionHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Model onException(Model model, Action action, Exception exception) {
        logger.log(model, action, exception);

        return model;
    }

    /**
     * Sets where to log when an Exception happens.
     */
    public interface Logger {
        /**
         * Logs when an Exception happens while a Model tries to act upon an Action.
         *
         * @param model     Model
         * @param action    Action
         * @param exception Exception
         */
        void log(Model model, Action action, Exception exception);
    }
}
