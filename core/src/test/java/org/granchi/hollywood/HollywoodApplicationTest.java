package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import rx.Observable;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HollywoodApplicationTest {
    @Mock
    private Model model, model2, model3;

    @Mock
    private Crew.Factory<ActorMetadata> crewFactory;
    @Mock
    private Crew crew;
    @Mock
    private ActorMetadata actorMetadata;
    @Mock
    private Action action, action2, action3;

    @Mock
    private ModelExceptionHandler exceptionHandler;

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullModel() throws Exception {
        new MockHollywoodApplication(null, models -> crew, null);
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullCrewFactory() throws Exception {
        new MockHollywoodApplication(model, null, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildsAFactory() throws Exception {
        when(crewFactory.build(any(Observable.class))).thenReturn(crew);

        new MockHollywoodApplication(model, crewFactory, null);

        verify(crewFactory).build(any(Observable.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testCantHaveANullCast() throws Exception {
        new MockHollywoodApplication(model, models -> null, null);
    }

    @Test
    public void testModelWithNoActorsEndsApp() throws Exception {
        when(model.getActors()).thenReturn(Collections.emptyList());
        when(crew.getActions()).thenReturn(Observable.empty());

        MockHollywoodApplication app = new MockHollywoodApplication(model, models -> crew, null);
        app.run();

        Thread.sleep(100);
        assertThat(app.isRunning()).isFalse();
    }

    @Test
    public void testNullModelEndsApp() throws Exception {
        Collection<ActorMetadata> metadata = Collections.singletonList(actorMetadata);

        when(model.getActors()).thenReturn(metadata);
        when(crew.getActions()).thenReturn(Observable.just(action));
        when(model.actUpon(action)).thenReturn(null);

        MockHollywoodApplication app = new MockHollywoodApplication(model, models -> crew, null);
        app.run();

        Thread.sleep(100);
        assertThat(app.isRunning()).isFalse();
    }

    @Test(timeout = 1000)
    @SuppressWarnings("unchecked")
    public void testBasicCycle() throws Exception {
        // Delay is so isRunning will get the app still running
        Observable<Action> actions = Observable.just(action, action2, action3).delay(100, TimeUnit.MILLISECONDS);
        Collection<ActorMetadata> metadata = Collections.singletonList(actorMetadata);

        when(crew.getActions()).thenReturn(actions);

        when(model.actUpon(action)).thenReturn(model2);
        when(model2.actUpon(action2)).thenReturn(model3);
        when(model3.actUpon(action3)).thenReturn(null);

        when(model.getActors()).thenReturn(metadata);
        when(model2.getActors()).thenReturn(metadata);
        when(model3.getActors()).thenReturn(metadata);

        MockHollywoodApplication app = new MockHollywoodApplication(model, models -> crew, null);
        app.run();

        assertThat(app.isRunning());

        // Should be enough
        Thread.sleep(250);

        verify(crew, times(3)).ensureCrew(same(metadata));

        verify(model).actUpon(action);
        verify(model2).actUpon(action2);
        verify(model3).actUpon(action3);
    }

    @Test
    public void testExceptionInModelWithoutHandlerEndsApp() throws Exception {
        Collection<ActorMetadata> metadata = Collections.singletonList(actorMetadata);

        when(model.getActors()).thenReturn(metadata);
        // One action, one second waiting and then more actions
        when(crew.getActions()).thenReturn(Observable.just(action).mergeWith(
                Observable.just(action2, action3).delay(1000, TimeUnit.MILLISECONDS)));
        when(model.actUpon(action)).thenThrow(new RuntimeException());

        MockHollywoodApplication app = new MockHollywoodApplication(model, models -> crew, null);
        app.run();

        Thread.sleep(100);
        assertThat(app.isRunning()).isFalse();
    }

    @Test
    public void testModelExceptionHandlerReturningNullEndsApp() throws Exception {
        Collection<ActorMetadata> metadata = Collections.singletonList(actorMetadata);
        Exception ex = new RuntimeException();

        when(model.getActors()).thenReturn(metadata);
        // One action, one second waiting and then more actions
        when(crew.getActions()).thenReturn(Observable.just(action).mergeWith(
                Observable.just(action2, action3).delay(1000, TimeUnit.MILLISECONDS)));
        when(model.actUpon(action)).thenThrow(ex);

        when(exceptionHandler.onException(model, action, ex)).thenReturn(null);

        MockHollywoodApplication
                app =
                new MockHollywoodApplication(model, models -> crew, exceptionHandler);
        app.run();

        Thread.sleep(100);
        assertThat(app.isRunning()).isFalse();
    }

    @Test
    public void testModelExceptionHandlerCanRecover() throws Exception {
        Collection<ActorMetadata> metadata = Collections.singletonList(actorMetadata);
        Exception ex = new RuntimeException();

        when(model.getActors()).thenReturn(metadata);
        when(model2.getActors()).thenReturn(metadata);

        when(crew.getActions()).thenReturn(Observable.just(action, action2));
        when(model.actUpon(action)).thenThrow(ex);

        when(exceptionHandler.onException(model, action, ex)).thenReturn(model2);

        MockHollywoodApplication
                app =
                new MockHollywoodApplication(model, models -> crew, exceptionHandler);
        app.run();

        Thread.sleep(100);

        verify(exceptionHandler).onException(model, action, ex);
        verify(model).actUpon(action);
        verify(model2).actUpon(action2);
        assertThat(app.isRunning()).isFalse();
    }

    private class MockHollywoodApplication extends HollywoodApplication<ActorMetadata> {
        public MockHollywoodApplication(Model<ActorMetadata> initialModel,
                                        Crew.Factory<ActorMetadata> crewFactory,
                                        ModelExceptionHandler<ActorMetadata> exceptionHandler) {
            super(initialModel, crewFactory, exceptionHandler);
        }

        @Override
        protected void logWarning(String msg) {
            Logger.getGlobal().warning(msg);
        }

        @Override
        protected void logError(String msg, Throwable throwable) {
            Logger.getGlobal().severe(msg);
        }

        @Override
        protected void logInfo(String msg) {
            Logger.getGlobal().info(msg);
        }

        @Override
        protected void logDebug(String msg) {

        }
    }

    // TODO all the actions in one thread
    // TODO hook events to the same thread

    // TODO unsubscribe when an actor dies
    // TODO subscribe when an actor is created from outside
    // TODO an actor created from outside receives last model
}