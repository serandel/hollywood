package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HollywoodApplicationTest {
    private interface MockModel extends Model<MockModel, ActorMetadata> {
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
        new HollywoodApplication<>(null, models -> cast);
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullCastFactory() throws Exception {
        new HollywoodApplication<>(model, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildsAFactory() throws Exception {
        when(castFactory.build(any(Observable.class))).thenReturn(cast);

        new HollywoodApplication<>(model, castFactory);

        verify(castFactory).build(any(Observable.class));
    }


    @Test(expected = IllegalStateException.class)
    public void testCantHaveANullCast() throws Exception {
        new HollywoodApplication<>(model, models -> null);
    }

    @Test(timeout = 1000)
    public void testModelWithNoActorsEndsApp() throws Exception {
        when(model.getActors()).thenReturn(Collections.emptySet());
        when(cast.getActions()).thenReturn(Observable.empty());

        new HollywoodApplication<>(model, models -> cast).run();
    }

    @Test(timeout = 1000)
    @SuppressWarnings("unchecked")
    public void testBasicLoop() throws Exception {
        Observable<Action> actions = Observable.just(action, action2, action3);
        Set<ActorMetadata> metadata = new HashSet<>(Collections.singletonList(actorMetadata));

        when(cast.getActions()).thenReturn(actions);

        when(model.actUpon(action)).thenReturn(model2);
        when(model2.actUpon(action2)).thenReturn(model3);
        when(model3.actUpon(action3)).thenReturn(null);

        when(model.getActors()).thenReturn(metadata);
        when(model2.getActors()).thenReturn(metadata);
        when(model3.getActors()).thenReturn(metadata);

        new HollywoodApplication<>(model, models -> cast).run();

        verify(cast, times(3)).ensureCast(same(metadata));

        verify(model).actUpon(action);
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
