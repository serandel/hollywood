package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HollywoodApplicationTest {
    // Milliseconds to wait for the loop to finish
    public static final int TIMEOUT_FOR_EXECUTION_TO_FINISH = 200;

    @Mock
    private Model model, model2, model3;

    @Mock
    private Actor actor, actor2;

    @Mock
    private Action action, action2, action3;

    @Mock
    private ModelExceptionHandler exceptionHandler;

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullModel() throws Exception {
        new HollywoodApplication(null, Arrays.asList(actor, actor2), exceptionHandler);
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullActorCollection() throws Exception {
        new HollywoodApplication(model, null, exceptionHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCantHaveAnEmptyActorCollection() throws Exception {
        new HollywoodApplication(model, Collections.emptyList(), exceptionHandler);
    }

    @Test
    public void testIfNothingHappensAppKeepsRunning() throws Exception {
        // If the observable of actions from the actor is null then Rx can't subscribe the model
        // to it and the app exists inmediately
        when(actor.getActions()).thenReturn(Observable.never());

        new HollywoodApplication(model, Arrays.asList(actor), null)
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH, TimeUnit.MILLISECONDS)
                .assertNoValues()
                .assertNotTerminated()
                .dispose();
    }

    @Test(expected = IllegalStateException.class)
    public void testCantRunTwiceAtTheSameTime() throws Exception {
        // If the observable of actions from the actor is null then Rx can't subscribe the model
        // to it and the app exists inmediately
        when(actor.getActions()).thenReturn(Observable.never());

        HollywoodApplication app =
                new HollywoodApplication(model, Arrays.asList(actor), null);
        app.run();
        app.run();
    }

    @Test
    public void testNullModelEndsApp() throws Exception {
        when(actor.getActions()).thenReturn(Observable.just(action));
        when(model.actUpon(action)).thenReturn(null);

        new HollywoodApplication(model, Arrays.asList(actor), null)
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH, TimeUnit.MILLISECONDS)
                .assertNoValues()
                .assertComplete();
    }

    @Test(expected = IllegalStateException.class)
    public void testAppCantBeRelaunched() throws Exception {
        when(actor.getActions()).thenReturn(Observable.just(action));
        when(model.actUpon(action)).thenReturn(null);

        HollywoodApplication app =
                new HollywoodApplication(model, Arrays.asList(actor), null);
        app.run()
           .test()
           .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH, TimeUnit.MILLISECONDS)
           .assertNoValues()
           .assertComplete();

        app.run();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBasicCycle() throws Exception {
        ArgumentCaptor<Observable<Model>> modelsCaptor = ArgumentCaptor.forClass(Observable
                                                                                         .class);
        final BehaviorSubject<Action> actions = BehaviorSubject.create();
        final BehaviorSubject<Action> actions2 = BehaviorSubject.create();
        // Models act upon Actions
        when(model.actUpon(action)).thenReturn(model2);
        when(model2.actUpon(action2)).thenReturn(model3);

        when(actor.getActions()).thenReturn(actions);
        when(actor2.getActions()).thenReturn(actions2);

        HollywoodApplication
                app =
                new HollywoodApplication(model, Arrays.asList(actor, actor2), null);

        // Actors receive Models
        verify(actor).subscribeTo(modelsCaptor.capture());
        Observable<Model> models = modelsCaptor.getValue();
        verify(actor2).subscribeTo(models);

        TestObserver<Model> modelsObserver = new TestObserver<Model>() {
            @Override
            public void onNext(Model m) {
                // The sequence is:
                // Action from Actor
                // Action2 from Actor2
                // Action3 from Actor, again
                if (m == model) {
                    actions.onNext(action);
                } else if (m == model2) {
                    actions2.onNext(action2);
                } else if (m == model3) {
                    actions.onNext(action3);
                }

                super.onNext(m);
            }
        };

        models.subscribe(modelsObserver);

        app.run()
           .test()
           .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH, TimeUnit.MILLISECONDS)
           .assertNoValues()
           .assertComplete();

        // Models received Actions
        verify(model).actUpon(action);
        verify(model2).actUpon(action2);
        verify(model3).actUpon(action3);

        // Actors received all Models
        modelsObserver.assertValues(model, model2, model3);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRepeatedActorsAreIgnored() throws Exception {
        when(model.actUpon(action)).thenReturn(model2);
        when(actor.getActions()).thenReturn(Observable.just(action));

        ArgumentCaptor<Observable<Model>> modelsCaptor = ArgumentCaptor.forClass(Observable
                                                                                         .class);
        new HollywoodApplication(model, Arrays.asList(actor, actor), null)
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH, TimeUnit.MILLISECONDS)
                .assertNoValues()
                .assertNotTerminated()
                .dispose();

        // One subscription, not two
        verify(actor).subscribeTo(modelsCaptor.capture());
        verify(model).actUpon(action);
        // No call on model2 because action was received just once
        verify(model2, never()).actUpon(action);
    }

    @Test
    public void testActionsAreDeliveredInSameThread() throws Exception {
        final Thread[] threads = new Thread[2];

        when(model.actUpon(action)).thenAnswer(invocation -> {
            threads[0] = Thread.currentThread();
            return model;
        });
        when(model.actUpon(action2)).thenAnswer(invocation -> {
            threads[1] = Thread.currentThread();
            return model;
        });

        when(actor.getActions()).thenReturn(Observable.just(action).subscribeOn(Schedulers
                                                                                        .newThread()));
        when(actor2.getActions()).thenReturn(Observable.just(action2).subscribeOn(Schedulers
                                                                                          .newThread()));

        new HollywoodApplication(model, Arrays.asList(actor, actor2), null)
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH, TimeUnit.MILLISECONDS);

        assertThat(threads[0]).isEqualTo(threads[1]);
    }

    @Test
    public void testExceptionInModelWithoutHandlerEndsApp() throws Exception {
        RuntimeException ex = new RuntimeException();

        when(actor.getActions()).thenReturn(Observable.fromArray(action, action2));
        when(model.actUpon(action)).thenThrow(ex);

        TestObserver<RuntimeException>
                testObserver =
                new HollywoodApplication(model, Arrays.asList(actor), null).run()
                                                                           .test();
        Thread.sleep(TIMEOUT_FOR_EXECUTION_TO_FINISH);

        testObserver.assertResult(ex);
    }

    @Test
    public void testModelExceptionHandlerReturningNullEndsApp() throws Exception {
        Exception ex = new RuntimeException();

        when(actor.getActions()).thenReturn(Observable.just(action, action2));
        when(model.actUpon(action)).thenThrow(ex);

        when(exceptionHandler.onException(model, action, ex)).thenReturn(null);

        new HollywoodApplication(model, Arrays.asList(actor), exceptionHandler)
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH, TimeUnit.MILLISECONDS)
                .assertComplete();
    }

    @Test
    public void testModelExceptionHandlerCanRecover() throws Exception {
        Exception ex = new RuntimeException();

        when(actor.getActions()).thenReturn(Observable.just(action, action2));
        when(model.actUpon(action)).thenThrow(ex);

        when(exceptionHandler.onException(model, action, ex)).thenReturn(model2);

        new HollywoodApplication(model, Arrays.asList(actor), exceptionHandler)
                .run()
                .test()
                .awaitDone(TIMEOUT_FOR_EXECUTION_TO_FINISH, TimeUnit.MILLISECONDS)
                .assertNoValues()
                .assertComplete();

        verify(exceptionHandler).onException(model, action, ex);
        verify(model).actUpon(action);
        verify(model2).actUpon(action2);
    }
}