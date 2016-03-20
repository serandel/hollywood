package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SingleInstanceCastTest {
    interface MockModel extends Model<MockModel, SingleInstanceActorMetadata> {
    }

    @Mock
    private MockModel model;

    @Mock
    private Actor.Factory<MockModel, SingleInstanceActorMetadata> actorFactory;

    @Mock
    private Actor actor, actor2;

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullActorFactory() {
        new SingleInstanceCast<>(null, Observable.<MockModel>empty());
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullModelsObservable() {
        new SingleInstanceCast<>(actorFactory, null);
    }

    @Test
    public void testCreatesAnInstance() {
        SingleInstanceActorMetadata metadata = new SingleInstanceActorMetadata("abc");
        Set<SingleInstanceActorMetadata> metadatas = Collections.singleton(metadata);

        when(actorFactory.create(metadata)).thenReturn(actor);

        SingleInstanceCast<MockModel> cast = new SingleInstanceCast<>(actorFactory, Observable.empty());
        cast.ensureCast(metadatas);

        verify(actorFactory).create(metadata);
    }

    @Test
    public void testCreatesAnInstanceOnlyOnce() {
        SingleInstanceActorMetadata metadata = new SingleInstanceActorMetadata("abc");
        Set<SingleInstanceActorMetadata> metadatas = Collections.singleton(metadata);

        when(actorFactory.create(metadata)).thenReturn(actor);

        SingleInstanceCast<MockModel> cast = new SingleInstanceCast<>(actorFactory, Observable.empty());

        cast.ensureCast(metadatas);
        cast.ensureCast(metadatas);

        verify(actorFactory).create(metadata);
    }

    @Test
    public void testCreatesSeveralClasses() {
        SingleInstanceActorMetadata metadata1 = new SingleInstanceActorMetadata("abc");
        SingleInstanceActorMetadata metadata2 = new SingleInstanceActorMetadata("def");

        Set<SingleInstanceActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata1, metadata2));

        when(actorFactory.create(any(SingleInstanceActorMetadata.class))).thenReturn(actor);

        SingleInstanceCast<MockModel> cast = new SingleInstanceCast<>(actorFactory, Observable.empty());

        cast.ensureCast(metadatas);

        verify(actorFactory).create(metadata1);
        verify(actorFactory).create(metadata2);
    }

    @Test
    public void testPropagatesModelsObservable() {
        SingleInstanceActorMetadata metadata1 = new SingleInstanceActorMetadata("abc");
        SingleInstanceActorMetadata metadata2 = new SingleInstanceActorMetadata("def");

        Set<SingleInstanceActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata1, metadata2));
        Subject<MockModel, MockModel> models = BehaviorSubject.create();

        when(actorFactory.create(metadata1)).thenReturn(actor);
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

        SingleInstanceCast<MockModel> cast = new SingleInstanceCast<>(actorFactory, models);
        cast.ensureCast(metadatas);
        models.onNext(model);

        verify(actor).subscribeTo(models);
        verify(actor2).subscribeTo(models);

        testSubscriber.assertValue(model);
        testSubscriber2.assertValue(model);
    }

    // TODO remove one actor
}