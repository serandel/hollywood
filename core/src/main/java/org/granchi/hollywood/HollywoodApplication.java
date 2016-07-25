package org.granchi.hollywood;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import rx.Subscription;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

/**
 * Hollywood application.
 * <p>
 * It orchestrates a Model (single-threaded, non-blocking, immutable) with a set of different Actors
 * (blocking,
 * multi-threaded, mutable). It runs in a loop, where actions from an actor are applied to the
 * model, who returns a
 * (possibly) different model that is applied to every actor.
 * <p>
 * Actions are applied in the same thread, so the model is easy to test and understand.
 * <p>
 * Subclasses that manipulate Actors or Models from outside the loop (for example, Android views
 * being created by the
 * S.O.) must coordinate their work with the provided executor.
 *
 * @author serandel
 */
public abstract class HollywoodApplication {
    protected final Executor executor;
    // TODO agents
    private Subscription loopSubscription;
    private Model model;
    private Subject<Model, Model> models;

    private ModelExceptionHandler exceptionHandler;

    /**
     * Constructor.
     *
     * @param initialModel     initial Model, can't be null
     * @param exceptionHandler handler for Exceptions during Model.actUpon, can be null to just end
     *                         the app if it
     *                         happens
     */
    // TODO agents
    public HollywoodApplication(Model initialModel, ModelExceptionHandler exceptionHandler) {
        if (initialModel == null) {
            throw new NullPointerException();
        }

        this.model = initialModel;
        this.exceptionHandler = exceptionHandler;

        models = BehaviorSubject.create();

        // Every cycle goes in the same thread
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Runs the application.
     * <p>
     * Creates a new Thread that executes action->model->actor cycles in a loop until Model becomes
     * null or no Actors
     * are wanted by the Model.
     */
    public void run() {
        executor.execute(() -> {
            // Feed them initial model
            models.onNext(model);

            // TODO agents
//            // And from now on...
//            loopSubscription =
//                    crew.getActions().subscribeOn(Schedulers.from(executor)).subscribe(action -> {
//                        try {
//                            model = model.actUpon(action);
//                        } catch (Exception e) {
//                            if (exceptionHandler == null) {
//                                model = null;
//                                logError("Exception during model.actUpon", e);
//                            } else {
//                                model = exceptionHandler.onException(model, action, e);
//                            }
//                        }
//
//                        // Ensure every actor exists, and no one more
//                        // Crew will complete its getActions Observable when given an empty Actor
//                        // set, so this subscription will
//                        // end
//                        crew.ensureCrew(model ==
//                                        null ? Collections.emptyList() : model.getActors());
//
//                        // Unrecoverable state, there is no model or no actors to react to it
//                        if (model == null || model.getActors().isEmpty()) {
//                            if (model == null) {
//                                logInfo("Ending cycle: model null");
//                            } else {
//                                logInfo("Ending cycle: no actors");
//                            }
//
//                            // We're done!
//                            models.onCompleted();
//                            loopSubscription.unsubscribe();
//                        } else {
//                            models.onNext(model);
//                        }
//                    }, throwable -> {
//                        logError("Throwable during action->model->actor cycle", throwable);
//                    });
        });
    }

    /**
     * Says if the application is currently running.
     * <p>
     * If not, it can be that run() hasn't been invoked or that the cycle has ended.
     *
     * @return if the application is running
     */
    public boolean isRunning() {
        return loopSubscription != null && !loopSubscription.isUnsubscribed();
    }

    /**
     * Logs a warning message.
     *
     * @param msg warning message
     */
    protected abstract void logWarning(String msg);

    /**
     * Logs an error message, with an attached throwable.
     *
     * @param msg       error message
     * @param throwable throwable
     */
    protected abstract void logError(String msg, Throwable throwable);

    /**
     * Logs a info message.
     *
     * @param msg info message
     */
    protected abstract void logInfo(String msg);

    /**
     * Logs a debug message.
     *
     * @param msg debug message
     */
    protected abstract void logDebug(String msg);
}