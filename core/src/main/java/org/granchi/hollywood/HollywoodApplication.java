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
import io.reactivex.observers.DisposableObserver;
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
public class HollywoodApplication {
    private static final Logger log = LoggerFactory.getLogger(HollywoodApplication.class);

    private Model model;
    private Subject<Model> models;
    private Observable<Action> actions;

    // It doubles as a flag to check if the application has been run
    private Executor executor;

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
    }

    /**
     * Runs the application.
     * <p>
     * Creates a new Thread that executes action->model->actor cycles in a loop until Model becomes
     * null.
     *
     * @return observable with all the possible exceptions that can arise during the loop, it will
     * complete when the loop finishes, if ever
     *
     * @throws IllegalStateException if the application is already running or was running in the
     *                               past
     */
    public synchronized Observable<Exception> run() {
        if (executor != null) {
            throw new IllegalStateException(
                    "Application is already running or stopped after running");
        }

        // Every cycle goes in the same thread
        executor = Executors.newSingleThreadExecutor();
        Subject<Exception> exceptions = PublishSubject.create();

        executor.execute(() -> {
            actions.subscribeOn(Schedulers.from(executor))
                   .subscribe(
                           new DisposableObserver<Action>() {
                               @Override
                               public void onNext(Action action) {
                                   try {
                                       log.debug("Received action: {}", action);
                                       model = model.actUpon(action);
                                   } catch (Exception e) {
                                       if (exceptionHandler == null) {
                                           model = null;

                                           String msg = "Exception during model.actUpon";
                                           log.error(msg, e);

                                           exceptions.onNext(new RuntimeException(msg, e));
                                       } else {
                                           String
                                                   msg =
                                                   "Exception during model.actUpon, relying on " +
                                                   "ExceptionHandler";
                                           log.warn(msg, e);

                                           // TODO perhaps pass exceptions subject too
                                           model = exceptionHandler.onException(model, action, e);
                                       }
                                   }

                                   // Unrecoverable state, there is no model
                                   if (model == null) {
                                       log.info("Ending cycle: model null");

                                       // We're done!
                                       models.onComplete();
                                       exceptions.onComplete();

                                       dispose();
                                   } else {
                                       models.onNext(model);
                                   }
                               }

                               @Override
                               public void onError(Throwable throwable) {
                                   String msg = "Throwable during action->model->actor cycle";

                                   log.error(msg, throwable);

                                   exceptions.onNext(new RuntimeException(msg, throwable));
                                   exceptions.onComplete();
                               }

                               @Override
                               public void onComplete() {
                                   // It won't happen, as the actors don't close their action
                                   // observables ever
                               }
                           });

            // Start the loop by feeding the initial model
            models.onNext(model);
        });

        return exceptions;
    }
}