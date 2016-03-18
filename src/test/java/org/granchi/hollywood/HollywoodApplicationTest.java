package org.granchi.hollywood;

import org.junit.Test;
import rx.Observable;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

public class HollywoodApplicationTest {
    interface MockModel extends Model<MockModel, ActorMetadata> {}

    @Test(expected=NullPointerException.class)
    public void testCantHaveANullModel() throws Exception {
        new HollywoodApplication<>(null, models -> mock(Cast.class));
    }

    @Test(expected=NullPointerException.class)
    public void testCantHaveANullCastFactory() throws Exception {
        new HollywoodApplication<>(mock(MockModel.class), null);
    }

    @Test
    public void testBuildsAFactory() throws Exception {
        Cast.Factory<ActorMetadata, MockModel> factory = mock(Cast.Factory.class);

        when(factory.build(any(Observable.class))).thenReturn(mock(Cast.class));

        new HollywoodApplication<>(mock(MockModel.class), factory);

        verify(factory).build(any(Observable.class));
    }


    @Test(expected=IllegalStateException.class)
    public void testCantHaveANullCast() throws Exception {
        new HollywoodApplication<>(mock(MockModel.class), models -> null);
    }

    @Test(timeout=1000)
    public void testModelWithNoActorsEndsApp() throws Exception {
        MockModel model = mock(MockModel.class);
        Cast cast = mock(Cast.class);

        when(model.getActors()).thenReturn(Collections.emptySet());
        when(cast.getActions()).thenReturn(Observable.empty());

        new HollywoodApplication<>(model, models -> cast).run();

        assertThat(true);
    }

    @Test(timeout=1000)
    @SuppressWarnings("unchecked")
    public void testBasicLoop() throws Exception {
        MockModel model1 = mock(MockModel.class);
        MockModel model2 = mock(MockModel.class);
        MockModel model3 = mock(MockModel.class);

        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);

        ActorMetadata actorMetadata = mock(ActorMetadata.class);
        Cast<ActorMetadata, MockModel> cast = mock(Cast.class);

        Observable<Action> actions = Observable.just(action1, action2, action3);
        Set<ActorMetadata> metadata = new HashSet<>(Collections.singletonList(actorMetadata));

        when(cast.getActions()).thenReturn(actions);

        when(model1.actUpon(action1)).thenReturn(model2);
        when(model2.actUpon(action2)).thenReturn(model3);
        when(model3.actUpon(action3)).thenReturn(null);

        when(model1.getActors()).thenReturn(metadata);
        when(model2.getActors()).thenReturn(metadata);
        when(model3.getActors()).thenReturn(metadata);

        new HollywoodApplication<>(model1, models -> cast).run();

        verify(cast, times(3)).ensureCast(same(metadata));

        verify(model1).actUpon(action1);
        verify(model2).actUpon(action2);
        verify(model3).actUpon(action3);
    }

    // TODO if an actor is present there is no building again
    // TODO model returns several models and then null, assert everything is being called meanwhile

    // TODO exception while actUpon
    // TODO with no exceptionhandler or with one that says null ends app

    // TODO unsubscribe when an actor dies
    // TODO subscribe when an actor is created from outside
    // TODO an actor created from outside receives last model

    // TODO actors die when model doesn't want them anymore
    // TODO check an actor is already created

    // TODO two actors receive the same model
}
