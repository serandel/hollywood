package org.granchi.hollywood

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class HollywoodApplicationTest {
    // Milliseconds to wait for the loop to finish
    private val TIMEOUT_FOR_EXECUTION_TO_FINISH = 200

    @Mock
    private lateinit var model: Model
    @Mock
    private lateinit var model2: Model
    @Mock
    private lateinit var model3: Model

    @Mock
    private lateinit var actor: Actor
    @Mock
    private lateinit var actor2: Actor

    @Mock
    private lateinit var action: Action
    @Mock
    private lateinit var action2: Action
    @Mock
    private lateinit var action3: Action

    @Mock
    private lateinit var exceptionHandler: ModelExceptionHandler

    @Test(expected = IllegalArgumentException::class)
    fun testCantHaveAnEmptyActorCollection() {
        HollywoodApplication(model, emptyList<Actor>(), exceptionHandler)
    }

    @Test
    fun testIfNothingHappensAppKeepsRunning() {
        // If the observable of actions from the actor is null then Rx can't subscribe the model
        // to it and the app exists inmediately
        whenever(actor.actions).thenReturn(Observable.never())

        HollywoodApplication(model, listOf(actor))
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH.toLong(), TimeUnit.MILLISECONDS)
                .assertNoValues()
                .assertNotTerminated()
                .dispose()
    }

    @Test(expected = IllegalStateException::class)
    fun testCantRunTwiceAtTheSameTime() {
        // If the observable of actions from the actor is null then Rx can't subscribe the model
        // to it and the app exists inmediately
        whenever(actor.actions).thenReturn(Observable.never())

        val app = HollywoodApplication(model, listOf(actor))
        app.run()
        app.run()
    }

    @Test
    fun testNullModelEndsApp() {
        whenever(actor.actions).thenReturn(Observable.just(action))
        whenever(model.actUpon(action)).thenReturn(null)

        HollywoodApplication(model, listOf(actor))
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH.toLong(), TimeUnit.MILLISECONDS)
                .assertNoValues()
                .assertComplete()
    }

    @Test(expected = IllegalStateException::class)
    fun testAppCantBeRelaunched() {
        whenever(actor.actions).thenReturn(Observable.just(action))
        whenever(model.actUpon(action)).thenReturn(null)

        val app = HollywoodApplication(model, listOf(actor))
        app.run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH.toLong(), TimeUnit.MILLISECONDS)
                .assertNoValues()
                .assertComplete()

        app.run()
    }

    @Test
    fun testBasicCycle() {
        val actions = BehaviorSubject.create<Action>()
        val actions2 = BehaviorSubject.create<Action>()

        // Models act upon Actions
        whenever(model.actUpon(action)).thenReturn(model2)
        whenever(model2.actUpon(action2)).thenReturn(model3)

        whenever(actor.actions).thenReturn(actions)
        whenever(actor2.actions).thenReturn(actions2)

        val app = HollywoodApplication(model, listOf(actor, actor2))

        // Actors receive Models
        argumentCaptor<Observable<Model>>().apply {
            verify(actor).subscribeTo(capture())
            val models = firstValue
            verify(actor2).subscribeTo(models)

            val modelsObserver = object : TestObserver<Model>() {
                override fun onNext(m: Model) {
                    // The sequence is:
                    // Action from Actor
                    // Action2 from Actor2
                    // Action3 from Actor, again
                    if (m === model) {
                        actions.onNext(action)
                    } else if (m === model2) {
                        actions2.onNext(action2)
                    } else if (m === model3) {
                        actions.onNext(action3)
                    }

                    super.onNext(m)
                }
            }

            models.subscribe(modelsObserver)

            app.run()
                    .test()
                    .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH.toLong(), TimeUnit.MILLISECONDS)
                    .assertNoValues()
                    .assertComplete()

            // Models received Actions
            verify(model).actUpon(action)
            verify(model2).actUpon(action2)
            verify(model3).actUpon(action3)

            // Actors received all Models
            modelsObserver.assertValues(model, model2, model3)
        }
    }

    @Test
    fun testRepeatedActorsAreIgnored() {
        whenever(model.actUpon(action)).thenReturn(model2)
        whenever(actor.actions).thenReturn(Observable.just(action))

        HollywoodApplication(model, listOf(actor, actor))
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH.toLong(), TimeUnit.MILLISECONDS)
                .assertNoValues()
                .assertNotTerminated()
                .dispose()

        // One subscription, not two
        verify(actor).subscribeTo(any())
        verify(model).actUpon(action)
        // No call on model2 because action was received just once
        verify(model2, never()).actUpon(action)
    }

    @Test
    fun testActionsAreDeliveredInSameThread() {
        var thread1: Thread? = null
        var thread2: Thread? = null

        whenever(model.actUpon(action)).thenAnswer {
            thread1 = Thread.currentThread()
            model
        }
        whenever(model.actUpon(action2)).thenAnswer {
            thread2 = Thread.currentThread()
            model
        }

        whenever(actor.actions).thenReturn(Observable
                .just(action)
                .subscribeOn(Schedulers.newThread()))
        whenever(actor2.actions).thenReturn(Observable
                .just(action2)
                .subscribeOn(Schedulers.newThread()))

        HollywoodApplication(model, listOf(actor, actor2))
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH.toLong(), TimeUnit.MILLISECONDS)

        assertThat(thread1).isNotNull()
        assertThat(thread1).isEqualTo(thread2)
    }

    @Test
    fun testExceptionInModelWithoutHandlerEndsApp() {
        val ex = RuntimeException()

        whenever(actor.actions).thenReturn(Observable.just(action, action2))
        whenever(model.actUpon(action)).thenThrow(ex)

        val testObserver = HollywoodApplication(model, listOf(actor))
                .run()
                .test()
        Thread.sleep(TIMEOUT_FOR_EXECUTION_TO_FINISH.toLong())

        testObserver.assertResult(ex)
    }

    @Test
    fun testModelExceptionHandlerReturningNullEndsApp() {
        val ex = RuntimeException()

        whenever(actor.actions).thenReturn(Observable.just(action, action2))
        whenever(model.actUpon(action)).thenThrow(ex)

        whenever(exceptionHandler.onException(model, action, ex)).thenReturn(null)

        HollywoodApplication(model, listOf(actor), exceptionHandler)
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH.toLong(), TimeUnit.MILLISECONDS)
                .assertComplete()
    }

    @Test
    fun testModelExceptionHandlerCanRecover() {
        val ex = RuntimeException()

        whenever(actor.actions).thenReturn(Observable.just(action, action2))
        whenever(model.actUpon(action)).thenThrow(ex)

        whenever(exceptionHandler.onException(model, action, ex)).thenReturn(model2)

        HollywoodApplication(model, listOf(actor), exceptionHandler)
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH.toLong(), TimeUnit.MILLISECONDS)
                .assertNoValues()
                .assertComplete()

        verify(exceptionHandler).onException(model, action, ex)
        verify(model).actUpon(action)
        verify(model2).actUpon(action2)
    }
}