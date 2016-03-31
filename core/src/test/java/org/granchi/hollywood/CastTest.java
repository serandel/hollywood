package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CastTest {
    private class MinimumCast extends Cast<ActorMetadata> {
        final Map<ActorMetadata, Actor<ActorMetadata>> actors = new HashMap<>();

        protected MinimumCast(Observable<Model<ActorMetadata>> models) {
            super(models);
        }

        @Override
        protected Actor<ActorMetadata> buildActorFrom(ActorMetadata metadata) {
            Actor<ActorMetadata> actor = actorFactory.create(metadata);
            actors.put(metadata, actor);
            return actor;
        }

        @Override
        protected boolean containsActorFrom(ActorMetadata metadata) {
            return actors.containsKey(metadata);
        }

        @Override
        protected Collection<Actor<ActorMetadata>> getActors() {
            return actors.values();
        }

        @Override
        protected boolean isActorFrom(Actor<ActorMetadata> actor, ActorMetadata metadata) {
            return actors.containsKey(metadata) && (actors.get(metadata) == actor);
        }

        @Override
        protected void remove(Actor<ActorMetadata> actor) {
            Iterator<Map.Entry<ActorMetadata, Actor<ActorMetadata>>> it = actors.entrySet().iterator();

            while(it.hasNext()) {
                if (it.next().getValue() == actor) {
                    it.remove();
                    break;
                }
            }
        }
    }

    @Mock
    private Model<ActorMetadata> model;

    @Mock
    private Actor.Factory<ActorMetadata> actorFactory;

    @Mock
    private ActorMetadata metadata, metadata2;

    @Mock
    private Actor<ActorMetadata> actor, actor2;

    @Mock
    private Action action, action2, action3;

    @Test
    @SuppressWarnings("unchecked")
    public void testPropagatesModels() {
        Set<ActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata, metadata2));
        Subject<Model<ActorMetadata>, Model<ActorMetadata>> models = BehaviorSubject.create();

        when(actorFactory.create(metadata)).thenReturn(actor);
        when(actorFactory.create(metadata2)).thenReturn(actor2);

        when(actor.getActions()).thenReturn(Observable.empty());
        when(actor2.getActions()).thenReturn(Observable.empty());

        TestSubscriber<Model<ActorMetadata>> testSubscriber = new TestSubscriber<>();
        TestSubscriber<Model<ActorMetadata>> testSubscriber2 = new TestSubscriber<>();

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

    @Test
    public void testMergesActions() {
        Set<ActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata, metadata2));
        Subject<Model<ActorMetadata>, Model<ActorMetadata>> models = BehaviorSubject.create();

        when(actor.getActions()).thenReturn(Observable.just(action, action3));
        when(actor2.getActions()).thenReturn(Observable.just(action2));

        when(actorFactory.create(metadata)).thenReturn(actor);
        when(actorFactory.create(metadata2)).thenReturn(actor2);

        TestSubscriber<Action> testSubscriber = new TestSubscriber<>();

        MinimumCast cast = new MinimumCast(models);
        cast.getActions().subscribe(testSubscriber);

        cast.ensureCast(metadatas);

        // I don't have to be sure of the order
        testSubscriber.assertValueCount(3);
    }

    @Test
    public void testMergesActionsEvenIfCreatedLater() {
        Set<ActorMetadata> metadatas = new HashSet<>(Collections.singletonList(metadata));
        Subject<Model<ActorMetadata>, Model<ActorMetadata>> models = BehaviorSubject.create();

        when(actor.getActions()).thenReturn(Observable.just(action, action3));
        when(actor2.getActions()).thenReturn(Observable.just(action2));

        when(actorFactory.create(metadata)).thenReturn(actor);
        when(actorFactory.create(metadata2)).thenReturn(actor2);

        TestSubscriber<Action> testSubscriber = new TestSubscriber<>();

        MinimumCast cast = new MinimumCast(models);
        cast.getActions().subscribe(testSubscriber);

        cast.ensureCast(metadatas);

        metadatas.add(metadata2);

        cast.ensureCast(metadatas);

        // Now I know the order, because actor2 was created later
        testSubscriber.assertValues(action, action3, action2);
    }

    @Test
    public void testDontGetActionsFromRemovedActors() {
        Set<ActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata, metadata2));

        Subject<Model<ActorMetadata>, Model<ActorMetadata>> models = BehaviorSubject.create();

        PublishSubject<Action> actions2 = PublishSubject.create();

        when(actor.getActions()).thenReturn(Observable.just(action));
        when(actor2.getActions()).thenReturn(actions2);

        when(actorFactory.create(metadata)).thenReturn(actor);
        when(actorFactory.create(metadata2)).thenReturn(actor2);

        TestSubscriber<Action> testSubscriber = new TestSubscriber<>();

        MinimumCast cast = new MinimumCast(models);
        cast.getActions().subscribe(testSubscriber);

        // Two actors
        cast.ensureCast(metadatas);
        // Action from 2
        actions2.onNext(action2);

        // Remove actor2
        metadatas.remove(metadata2);
        cast.ensureCast(metadatas);

        // Another action
        actions2.onNext(action3);

        // No action3
        testSubscriber.assertValues(action, action2);
    }
}