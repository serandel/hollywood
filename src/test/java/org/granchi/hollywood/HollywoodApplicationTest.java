package org.granchi.hollywood;

import org.junit.Test;
import rx.Observable;

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
    public void testApplicationUsesCast() {
        MockModel model = mock(MockModel.class);
        ActorMetadata actorMetadata = mock(ActorMetadata.class);

        // TODO renombrar actorBuilder a Cast o algo así

        // TODO
//        when(model.actUpon(action)).thenReturn(null);
//        when(model.getActors()).thenReturn(new HashSet<>(Arrays.asList(actorMetadata)));
//        when(actorBuilder.buildActorFrom(actorMetadata)).thenReturn(actor);
//
//        new HollywoodApplication<MockModel>(model, actorBuilder);

        assertThat(true);
    }

    // TODO if an actor is present there is no building again
    // TODO model returns several models and then null, assert everything is being called meanwhile

    @Test
    @SuppressWarnings("unchecked")
    public void testEndsWithNullNextModel() throws Exception {
        MockModel model1 = mock(MockModel.class);
        MockModel model2 = mock(MockModel.class);
        MockModel model3 = mock(MockModel.class);

        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);

        Actor<MockModel> actor = mock(Actor.class);
        ActorMetadata actorMetadata = mock(ActorMetadata.class);
        ActorBuilder<ActorMetadata, MockModel> actorBuilder = mock(ActorBuilder.class);

        Observable<Action> actions = Observable.just(action1, action2, action3);

        when(actor.getActions()).thenReturn(actions);

        when(model1.actUpon(action1)).thenReturn(model2);
        when(model2.actUpon(action2)).thenReturn(model3);
        when(model3.actUpon(action3)).thenReturn(null);

        when(model1.getActors()).thenReturn(new HashSet<>(Collections.singletonList(actorMetadata)));
        when(model2.getActors()).thenReturn(new HashSet<>(Collections.singletonList(actorMetadata)));
        when(model3.getActors()).thenReturn(new HashSet<>(Collections.singletonList(actorMetadata)));

        // IDK why it doesn't compile without casting
        when(actorBuilder.buildActorFrom(actorMetadata)).thenReturn((Actor) actor);

        new HollywoodApplication<>(model1, actorBuilder).run();

        verify(actorBuilder).buildActorFrom(actorMetadata);

        verify(model1.actUpon(action1));
        verify(model2.actUpon(action2));
        verify(model3.actUpon(action3));

        verify(actor).apply(model1);
        verify(actor).apply(model2);
        verify(actor).apply(model3);
    }

    // TODO exception while actUpon
    // TODO with no exceptionhandler or with one that says null ends app

    // TODO unsubscribe when an actor dies
    // TODO subscribe when an actor is created from outside
    // TODO an actor created from outside receives last model

    // TODO actors die when model doesn't want them anymore
    // TODO check an actor is already created

    // TODO two actors receive the same model
}
