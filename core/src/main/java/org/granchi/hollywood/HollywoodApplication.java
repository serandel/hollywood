package org.granchi.hollywood;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

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
public abstract class HollywoodApplication {
    private static final Logger log = LoggerFactory.getLogger(HollywoodApplication.class);

    protected final Executor executor;

    private Model model;
    private Subject<Model> models;
    private Observable<Action> actions;

    private Subject<Exception> exceptions;

    private Disposable loopDisposable;

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

        exceptions = PublishSubject.create();

        this.exceptionHandler = exceptionHandler;

        // Every cycle goes in the same thread
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Runs the application.
     * <p>
     * Creates a new Thread that executes action->model->actor cycles in a loop until Model becomes
     * null.
     */
    public void run() {
        executor.execute(() -> {
            // Set basic cycle
            loopDisposable =
                    actions.subscribeOn(Schedulers.from(executor)).subscribe(action -> {
                        try {
                            log.debug("Received action: {}", action);
                            model = model.actUpon(action);
                        } catch (Exception e) {
                            if (exceptionHandler == null) {
                                model = null;
                                log.error("Exception during model.actUpon", e);
                            } else {
                                log.warn(
                                        "Exception during model.actUpon, relying on " +
                                        "ExceptionHandler",
                                        e);
                                model = exceptionHandler.onException(model, action, e);
                            }
                        }

                        // Unrecoverable state, there is no model
                        if (model == null) {
                            log.info("Ending cycle: model null");

                            // We're done!
                            models.onComplete();
                            loopDisposable.dispose();
                        } else {
                            models.onNext(model);
                        }
                    }, throwable -> {
                        log.error("Throwable during action->model->actor cycle", throwable);
                    });

            // And feed the initial model
            models.onNext(model);
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
        return loopDisposable != null && !loopDisposable.isDisposed();
    }


    /**
     * Returns an observable of all the possible exceptions during the loop, that completes if the
     * application stops running.
     * <p>
     * An application can use it to show errors to the user.
     *
     * @return exceptions
     */
    public Observable<Exception> getExceptions() {
        return exceptions;
    }
}