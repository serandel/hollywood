package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import rx.Observable;
import rx.observers.TestSubscriber;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HollywoodApplicationTest {
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
        new MockHollywoodApplication(null, Arrays.asList(actor, actor2), exceptionHandler);
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullActorCollection() throws Exception {
        new MockHollywoodApplication(model, null, exceptionHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCantHaveAnEmptyActorCollection() throws Exception {
        new MockHollywoodApplication(model, Collections.emptyList(), exceptionHandler);
    }

    @Test
    public void testAppDoesntRunUntilToldSo() throws Exception {
        MockHollywoodApplication
                app =
                new MockHollywoodApplication(model, Arrays.asList(actor), null);
        assertThat(app.isRunning()).isFalse();
    }

    @Test
    public void testIfNothingHappensAppKeepsRunning() throws Exception {
        // If the observable of actions from the actor is null then Rx can't subscribe the model
        // to it and the app exists inmediately
        when(actor.getActions()).thenReturn(Observable.never());

        MockHollywoodApplication
                app =
                new MockHollywoodApplication(model, Arrays.asList(actor), null);
        app.run();

        Thread.sleep(100);
        assertThat(app.isRunning()).isTrue();
    }

    @Test
    public void testNullModelEndsApp() throws Exception {
        when(actor.getActions()).thenReturn(Observable.just(action));
        when(model.actUpon(action)).thenReturn(null);

        MockHollywoodApplication
                app =
                new MockHollywoodApplication(model, Arrays.asList(actor), null);
        app.run();

        Thread.sleep(100);
        assertThat(app.isRunning()).isFalse();
    }

    @Test(timeout = 1000)
    @SuppressWarnings("unchecked")
    public void testBasicCycle() throws Exception {
        ArgumentCaptor<Observable<Model>> modelsCaptor = ArgumentCaptor.forClass(Observable.class);

        // Delay is so isRunning will get the app still running
        Observable<Action>
                actions1 =
                Observable.just(action, action2).delay(50, TimeUnit.MILLISECONDS);
        Observable<Action>
                actions2 =
                Observable.just(action3).delay(100, TimeUnit.MILLISECONDS);

        // Models act upon Actions
        when(model.actUpon(action)).thenReturn(model2);
        when(model2.actUpon(action2)).thenReturn(model3);
        when(model3.actUpon(action3)).thenReturn(null);

        // Actions come from Actors
        when(actor.getActions()).thenReturn(actions1);
        when(actor2.getActions()).thenReturn(actions2);

        MockHollywoodApplication
                app =
                new MockHollywoodApplication(model, Arrays.asList(actor, actor2), null);

        // Actors receive Models
        verify(actor).subscribeTo(modelsCaptor.capture());
        Observable<Model> models = modelsCaptor.getValue();
        verify(actor2).subscribeTo(models);
        TestSubscriber<Model> subscriber = new TestSubscriber<>();
        models.subscribe(subscriber);

        // Running
        app.run();
        assertThat(app.isRunning());

        // Should be enough
        Thread.sleep(250);

        // Models received Actions
        verify(model).actUpon(action);
        verify(model2).actUpon(action2);
        verify(model3).actUpon(action3);

        // Actors received all Models
        subscriber.assertReceivedOnNext(Arrays.asList(model, model2, model3));

        // App finished
        assertThat(app.isRunning()).isFalse();
    }

    @Test
    public void testRepeatedActorsAreIgnored() throws Exception {
        when(model.actUpon(action)).thenReturn(model2);

        when(actor.getActions()).thenReturn(Observable.just(action)
                                                      .delay(5, TimeUnit.MILLISECONDS));

        ArgumentCaptor<Observable<Model>> modelsCaptor = ArgumentCaptor.forClass(Observable.class);

        MockHollywoodApplication
                app =
                new MockHollywoodApplication(model, Arrays.asList(actor, actor), null);

        // One subscription, not two
        verify(actor).subscribeTo(modelsCaptor.capture());
        TestSubscriber<Model> subscriber = new TestSubscriber<>();
        modelsCaptor.getValue().subscribe(subscriber);

        // Running
        app.run();

        Thread.sleep(10);

        // No call on model2 because action was received just once
        subscriber.assertReceivedOnNext(Arrays.asList(model, model2));
        verify(model2, never()).actUpon(action);

        assertThat(app.isRunning()).isTrue();
    }

    @Test
    public void testActionsAreDeliveredInSameThread() throws Exception {
        final Thread[] threads = new Thread[2];

        when(model.actUpon(action)).thenAnswer(new Answer<Model>() {
            @Override
            public Model answer(InvocationOnMock invocation) throws Throwable {
                threads[0] = Thread.currentThread();
                return model;
            }
        });
        when(model.actUpon(action2)).thenAnswer(new Answer<Model>() {
            @Override
            public Model answer(InvocationOnMock invocation) throws Throwable {
                threads[1] = Thread.currentThread();
                return model;
            }
        });

        when(actor.getActions()).thenReturn(Observable.just(action));
        when(actor2.getActions()).thenReturn(Observable.just(action2));

        MockHollywoodApplication
                app =
                new MockHollywoodApplication(model, Arrays.asList(actor, actor2), null);

        // Running
        app.run();

        Thread.sleep(10);

        assertThat(threads[0]).isEqualTo(threads[1]);
    }

    @Test
    public void testExceptionInModelWithoutHandlerEndsApp() throws Exception {
        // One action, one second waiting and then more actions
        when(actor.getActions()).thenReturn(Observable.just(action).mergeWith(
                Observable.just(action2, action3).delay(1000, TimeUnit.MILLISECONDS)));
        when(model.actUpon(action)).thenThrow(new RuntimeException());

        MockHollywoodApplication
                app =
                new MockHollywoodApplication(model, Arrays.asList(actor), null);
        app.run();

        Thread.sleep(100);
        assertThat(app.isRunning()).isFalse();
    }

    @Test
    public void testModelExceptionHandlerReturningNullEndsApp() throws Exception {
        Exception ex = new RuntimeException();

        // One action, one second waiting and then more actions
        when(actor.getActions()).thenReturn(Observable.just(action).mergeWith(
                Observable.just(action2, action3).delay(1000, TimeUnit.MILLISECONDS)));
        when(model.actUpon(action)).thenThrow(ex);

        when(exceptionHandler.onException(model, action, ex)).thenReturn(null);

        MockHollywoodApplication
                app =
                new MockHollywoodApplication(model, Arrays.asList(actor), exceptionHandler);
        app.run();

        Thread.sleep(100);
        assertThat(app.isRunning()).isFalse();
    }

    @Test
    public void testModelExceptionHandlerCanRecover() throws Exception {
        Exception ex = new RuntimeException();

        when(actor.getActions()).thenReturn(Observable.just(action, action2));
        when(model.actUpon(action)).thenThrow(ex);

        when(exceptionHandler.onException(model, action, ex)).thenReturn(model2);

        MockHollywoodApplication
                app =
                new MockHollywoodApplication(model, Arrays.asList(actor), exceptionHandler);
        app.run();

        Thread.sleep(100);

        verify(exceptionHandler).onException(model, action, ex);
        verify(model).actUpon(action);
        verify(model2).actUpon(action2);
        assertThat(app.isRunning()).isFalse();
    }

    private class MockHollywoodApplication extends HollywoodApplication {
        public MockHollywoodApplication(Model initialModel,
                                        Collection<Actor> actors,
                                        ModelExceptionHandler exceptionHandler) {
            super(initialModel, actors, exceptionHandler);
        }

        @Override
        protected void logWarning(String msg) {
            Logger.getLogger(MockHollywoodApplication.class.getName()).warning(msg);
        }

        @Override
        protected void logWarning(String msg, Throwable throwable) {
            Logger.getLogger(MockHollywoodApplication.class.getName())
                  .log(Level.WARNING, msg, throwable);
        }

        @Override
        protected void logError(String msg, Throwable throwable) {
            Logger.getLogger(MockHollywoodApplication.class.getName())
                  .log(Level.SEVERE, msg, throwable);
        }

        @Override
        protected void logInfo(String msg) {
            Logger.getLogger(MockHollywoodApplication.class.getName()).info(msg);
        }

        @Override
        protected void logDebug(String msg) {
            Logger.getLogger(MockHollywoodApplication.class.getName()).fine(msg);
        }
    }
}