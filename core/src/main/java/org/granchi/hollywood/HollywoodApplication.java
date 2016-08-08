package org.granchi.hollywood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

/**
 * Hollywood application.
 * <p>
 * It orchestrates a Model (single-threaded, non-blocking, immutable) with a set of different
 * Actors (blocking, multi-threaded, mutable). It runs in a loop, where actions from an actor are
 * applied to the model, who returns a (possibly) different model that is applied to every actor.
 * <p>
 * Actions are applied in the same thread, so the model is easy to test and understand.
 *
 * @author serandel
 */
// TODO put logger in a new class?
public abstract class HollywoodApplication {
    protected final Executor executor;
    private Model model;
    private Subject<Model, Model> models;
    private Observable<Action> actions;
    private Subscription loopSubscription;
    private ModelExceptionHandler exceptionHandler;

    /**
     * Constructor.
     *
     * @param initialModel     initial Model, can't be null
     * @param actors           Actors for the application, can't be null or empty
     * @param exceptionHandler handler for Exceptions during Model.actUpon, can be null to just end
     *                         the app if an Exception happens
     */
    public HollywoodApplication(Model initialModel,
                                Collection<Actor> actors,
                                ModelExceptionHandler exceptionHandler) {
        if (initialModel == null) {
            throw new NullPointerException("Null initial model");
        }
        if (actors == null) {
            throw new NullPointerException("Null actor collection");
        } else if (actors.isEmpty()) {
            throw new IllegalArgumentException("Empty actor collection");
        }

        this.model = initialModel;
        models = BehaviorSubject.create();

        ArrayList<Observable<Action>> actionObservables = new ArrayList<>();
        // We don't want repeated actors
        Set<Actor> uniqueActors = new HashSet<>(actors);
        for (Actor actor : uniqueActors) {
            actor.subscribeTo(models);
            actionObservables.add(actor.getActions());
        }
        actions = Observable.merge(actionObservables);

        this.exceptionHandler = exceptionHandler;

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

            // And from now on...
            loopSubscription =
                    actions.subscribeOn(Schedulers.from(executor)).subscribe(action -> {
                        try {
                            model = model.actUpon(action);
                        } catch (Exception e) {
                            if (exceptionHandler == null) {
                                model = null;
                                logError("Exception during model.actUpon", e);
                            } else {
                                model = exceptionHandler.onException(model, action, e);
                            }
                        }

                        // Unrecoverable state, there is no model
                        if (model == null) {
                            logInfo("Ending cycle: model null");

                            // We're done!
                            models.onCompleted();
                            loopSubscription.unsubscribe();
                        } else {
                            models.onNext(model);
                        }
                    }, throwable -> {
                        logError("Throwable during action->model->actor cycle", throwable);
                    });
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