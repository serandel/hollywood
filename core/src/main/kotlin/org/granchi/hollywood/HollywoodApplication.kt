package org.granchi.hollywood

import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Hollywood application.
 * *
 * It orchestrates a Model (single-threaded, non-blocking, immutable) with a set of different
 * Actors (blocking, multi-threaded, mutable). It runs in a loop, where actions from an actor are
 * applied to the model, who returns a (possibly) different model that is applied to every actor.
 * *
 * Actions are applied in the same thread, so the model is easy to test and understand.
 */
class HollywoodApplication(private var model: Model,
                           actors: Collection<Actor>,
                           private val exceptionHandler: ModelExceptionHandler? = null,
                           private val log: Logger = LoggerFactory.getLogger(HollywoodApplication::class.qualifiedName)) {
    private val models: Subject<Model> = BehaviorSubject.create<Model>()
    private val actions: Observable<Action>
    // Every cycle goes in the same thread
    private val executor = Executors.newSingleThreadExecutor()

    private val hasBeenRun = AtomicBoolean(false)

    init {
        if (actors.isEmpty()) {
            throw throw IllegalArgumentException("Empty actor collection")
        }

        val actionObservables = ArrayList<Observable<Action>>()
        actors.distinct().forEach {
            it.subscribeTo(models)
            actionObservables.add(it.actions)
        }

        actions = Observable.merge(actionObservables)
    }


    /**
     * Runs the application.
     *
     * Creates a new Thread that executes action->model->actor cycles in a loop until Model becomes
     * null.
     *
     * Return an observable with all the possible exceptions that can arise during the execution,
     * that will complete when the execution finishes, if ever.
     *
     * Throws an [IllegalStateException] if the application is already running or was running in the
     * past.
     */
    fun run(): Observable<RuntimeException> {
        if (hasBeenRun.getAndSet(true)) {
            throw IllegalStateException("Application has already been run")
        }

        val execution = PublishSubject.create<RuntimeException>()

        executor.execute {
            actions.observeOn(Schedulers.from(executor))
                    .subscribe(object : DisposableObserver<Action>() {
                        override fun onNext(action: Action) {
                            var newModel: Model?

                            try {
                                log.debug("Received action: {}", action)
                                newModel = model.actUpon(action)
                            } catch (e: RuntimeException) {
                                newModel = if (exceptionHandler == null) {
                                    val msg = "Exception during model.actUpon"
                                    log.error(msg, e)

                                    // No need to encapsulate the exception
                                    execution.onNext(e)

                                    null
                                } else {
                                    val msg = "Exception during model.actUpon, relying on ExceptionHandler"
                                    log.warn(msg, e)

                                    // TODO perhaps pass exceptions subject too
                                    exceptionHandler.onException(model, action, e)
                                }
                            }

                            // Unrecoverable state, there is no model
                            if (newModel == null) {
                                log.info("Ending cycle: model null")

                                // We're done!
                                models.onComplete()
                                execution.onComplete()

                                dispose()
                            } else {
                                model = newModel
                                models.onNext(model)
                            }
                        }

                        override fun onError(throwable: Throwable) {
                            val msg = "Throwable during action->model->actor cycle"

                            log.error(msg, throwable)

                            execution.onNext(RuntimeException(msg, throwable))
                            execution.onComplete()
                        }

                        override fun onComplete() {
                            // It won't happen, as the actors don't close their action
                            // observables ever
                        }
                    })

            // Start the loop by feeding the initial model
            models.onNext(model)
        }

        log.debug("Application running!")

        return execution
    }
}