package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HollywoodApplicationTest {
    private interface MockModel extends Model<MockModel, ActorMetadata> {
    }

    private class MockHollywoodApplication extends HollywoodApplication<MockModel, ActorMetadata> {
        public MockHollywoodApplication(MockModel initialModel,
                                        Cast.Factory<ActorMetadata, MockModel> castFactory) {
            super(initialModel, castFactory);
        }

        @Override
        protected void logWarning(String msg) {

        }

        @Override
        protected void logError(String msg, Throwable throwable) {

        }

        @Override
        protected void logInfo(String msg) {

        }

        @Override
        protected void logDebug(String msg) {

        }
    }

    @Mock
    private MockModel model, model2, model3;

    @Mock
    private Cast.Factory<ActorMetadata, MockModel> castFactory;

    @Mock
    private Cast cast;

    @Mock
    private ActorMetadata actorMetadata;

    @Mock
    private Action action, action2, action3;

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullModel() throws Exception {
        new MockHollywoodApplication(null, models -> cast);
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullCastFactory() throws Exception {
        new MockHollywoodApplication(model, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildsAFactory() throws Exception {
        when(castFactory.build(any(Observable.class))).thenReturn(cast);

        new MockHollywoodApplication(model, castFactory);

        verify(castFactory).build(any(Observable.class));
    }


    @Test(expected = IllegalStateException.class)
    public void testCantHaveANullCast() throws Exception {
        new MockHollywoodApplication(model, models -> null);
    }

    @Test
    public void testModelWithNoActorsEndsApp() throws Exception {
        when(model.getActors()).thenReturn(Collections.emptySet());
        when(cast.getActions()).thenReturn(Observable.empty());

        MockHollywoodApplication app = new MockHollywoodApplication(model, models -> cast);
        app.run();

        Thread.sleep(100);
        assertThat(app.isRunning()).isFalse();
    }

    @Test
    public void testNullModelEndsApp() throws Exception {
        Set<ActorMetadata> metadata = new HashSet<>(Collections.singletonList(actorMetadata));

        when(model.getActors()).thenReturn(metadata);
        when(cast.getActions()).thenReturn(Observable.just(action));
        when(model.actUpon(action)).thenReturn(null);

        MockHollywoodApplication app = new MockHollywoodApplication(model, models -> cast);
        app.run();

        Thread.sleep(100);
        assertThat(app.isRunning()).isFalse();
    }

    @Test(timeout = 1000)
    @SuppressWarnings("unchecked")
    public void testBasicCycle() throws Exception {
        // Delay is so isRunning will get the app still running
        Observable<Action> actions = Observable.just(action, action2, action3).delay(100, TimeUnit.MILLISECONDS);
        Set<ActorMetadata> metadata = new HashSet<>(Collections.singletonList(actorMetadata));

        when(cast.getActions()).thenReturn(actions);

        when(model.actUpon(action)).thenReturn(model2);
        when(model2.actUpon(action2)).thenReturn(model3);
        when(model3.actUpon(action3)).thenReturn(null);

        when(model.getActors()).thenReturn(metadata);
        when(model2.getActors()).thenReturn(metadata);
        when(model3.getActors()).thenReturn(metadata);

        MockHollywoodApplication app = new MockHollywoodApplication(model, models -> cast);
        app.run();

        assertThat(app.isRunning());

        // Should be enough
        Thread.sleep(250);

        verify(cast, times(3)).ensureCast(same(metadata));

        verify(model).actUpon(action);
        verify(model2).actUpon(action2);
        verify(model3).actUpon(action3);
    }

    // TODO exception while actUpon
    // TODO with no exceptionhandler or with one that says null ends app

    // TODO all the actions in one thread
    // TODO hook events to the same thread

    // TODO unsubscribe when an actor dies
    // TODO subscribe when an actor is created from outside
    // TODO an actor created from outside receives last model
}