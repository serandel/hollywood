package org.granchi.hollywood;

import org.junit.Test;
import rx.Observable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

public class HollywoodApplicationTest {
    interface MockModel extends Model<MockModel> {}

    @Test(expected=NullPointerException.class)
    public void testCantHaveANullModel() throws Exception {
        new HollywoodApplication<>(null, mock(ActorBuilder.class));
    }

    @Test(expected=NullPointerException.class)
    public void testCantHaveANullActorBuilder() throws Exception {
        new HollywoodApplication<>(mock(MockModel.class), null);
    }

    @Test()
    public void testModelWithNoActorsEndsApp() throws Exception {
        MockModel model = mock(MockModel.class);

        when(model.getActors()).thenReturn(Collections.emptySet());

        new HollywoodApplication<>(model, mock(ActorBuilder.class)).run();

        assertThat(true);
    }

    @Test
    public void testApplicationUsesActorBuilder() {
        MockModel model = mock(MockModel.class);
        ActorMetadata actorMetadata = mock(ActorMetadata.class);

        // TODO
//        when(model.actUpon(action)).thenReturn(null);
//        when(model.getActors()).thenReturn(new HashSet<>(Arrays.asList(actorMetadata)));
//        when(actorBuilder.build(actorMetadata)).thenReturn(actor);
//
//        new HollywoodApplication<MockModel>(model, actorBuilder);

        assertThat(true);
    }

    // TODO if an actor is present there is no building again
    // TODO model returns several models and then null, assert everything is being called meanwhile

    // TODO end without an exception?
    @Test(expected=IllegalStateException.class)
    public void testModelCantOutputNullSucessor() throws Exception {
        MockModel model = mock(MockModel.class);
        Action action = mock(Action.class);
        Actor actor = mock(Actor.class);
        ActorMetadata actorMetadata = mock(ActorMetadata.class);
        ActorBuilder actorBuilder = mock(ActorBuilder.class);

        Observable<Action> actions = Observable.just(action);

        when(actor.getActions()).thenReturn(actions);
        when(model.actUpon(action)).thenReturn(null);
        when(model.getActors()).thenReturn(new HashSet<>(Arrays.asList(actorMetadata)));
        when(actorBuilder.build(actorMetadata)).thenReturn(actor);

        new HollywoodApplication<MockModel>(model, actorBuilder).run();
    }

    // TODO exception while actUpon
    // TODO with no exceptionhandler or with one that says null ends app
}
