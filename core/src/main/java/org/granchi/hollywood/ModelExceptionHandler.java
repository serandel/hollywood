package org.granchi.hollywood;

/**
 * Handler for reacting to exceptions during Model.actUpon(Action).
 *
 * Without it, the application would end if an Exception happens.
 *
 * @param <D> type of the ActorMetadata used to build Actors
 * @author serandel
 */
public interface ModelExceptionHandler <D extends ActorMetadata>{
    /**
     * An Exception has ocurred during Model.actUpon(Action).
     *
     * @param model Model that received the Action
     * @param action Action that was acted upon
     * @param exception Exception thrown
     * @return Model that the application should keep as the current model, null for finishing the app
     */
    Model<D> onException(Model<D> model, Action action, Exception exception);
}
