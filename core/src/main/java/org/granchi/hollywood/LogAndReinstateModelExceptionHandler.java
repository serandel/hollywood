package org.granchi.hollywood;

/**
 * ModelExceptionHandler that logs that the exception has happened and keeps the same model, discarding the action.
 *
 * @param <D> type of the ActorMetadata used to build Actors
 * @author serandel
 */
public class LogAndReinstateModelExceptionHandler<D extends ActorMetadata> implements ModelExceptionHandler<D>{
    /**
     * Sets where to log when an Exception happens.
     *
     * @param <D> type of the ActorMetadata used to build Actors
     */
    public interface Logger<D extends ActorMetadata> {
        /**
         * Logs when an Exception happends while a Model tries to act upon an Action.
         *
         * @param model Model
         * @param action Action
         * @param exception Exception
         */
        void log(Model<D> model, Action action, Exception exception);
    }

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
    public Model<D> onException(Model<D> model, Action action, Exception exception) {
        logger.log(model, action, exception);

        return model;
    }
}
