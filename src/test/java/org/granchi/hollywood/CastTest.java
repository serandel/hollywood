package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class CastTest {
    private interface MockModel extends Model<MockModel, ActorMetadata> {
    }

    private class MinimumCast extends Cast<ActorMetadata, MockModel> {
        protected MinimumCast(Observable<MockModel> models) {
            super(models);
        }

        @Override
        protected Actor buildActorFrom(ActorMetadata metadata) {
            return actorFactory.create(metadata);
        }

        @Override
        protected boolean containsActorFrom(ActorMetadata metadata) {
            return false;
        }

        @Override
        protected Collection<Actor<MockModel>> getActors() {
            return Collections.emptyList();
        }

        @Override
        protected boolean isActorFrom(Actor<MockModel> actor, ActorMetadata metadata) {
            return true;
        }

        @Override
        protected void remove(Actor actor) {
        }
    }

    @Mock
    private MockModel model;

    @Mock
    private Actor.Factory<MockModel, ActorMetadata> actorFactory;

    @Mock
    private ActorMetadata metadata, metadata2;

    @Mock
    private Actor actor, actor2;

    @Test
    public void testPropagatesModelsObservable() {
        Set<ActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata, metadata2));
        Subject<MockModel, MockModel> models = BehaviorSubject.create();

        when(actorFactory.create(metadata)).thenReturn(actor);
        when(actorFactory.create(metadata2)).thenReturn(actor2);

        TestSubscriber<MockModel> testSubscriber = new TestSubscriber<>();
        TestSubscriber<MockModel> testSubscriber2 = new TestSubscriber<>();

        doAnswer(invocation -> {
            invocation.getArgumentAt(0, Observable.class).subscribe(testSubscriber);
            return null;
        }).when(actor).subscribeTo(models);

        doAnswer(invocation -> {
            invocation.getArgumentAt(0, Observable.class).subscribe(testSubscriber2);
            return null;
        }).when(actor2).subscribeTo(models);

        MinimumCast cast = new MinimumCast(models);
        cast.ensureCast(metadatas);
        models.onNext(model);

        verify(actor).subscribeTo(models);
        verify(actor2).subscribeTo(models);

        testSubscriber.assertValue(model);
        testSubscriber2.assertValue(model);
    }

    // TODO send model from observable in cast, verify mock onNext

    // TODO merge actors actions
    // TODO create an actor and event merges
}