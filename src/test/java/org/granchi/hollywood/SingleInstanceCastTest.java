package org.granchi.hollywood;

import org.junit.Test;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SingleInstanceCastTest {
    interface MockModel extends Model<MockModel, SingleInstanceActorMetadata> {
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullActorFactory() {
        new SingleInstanceCast<>(null, mock(Observable.class));
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveANullModelsObservable() {
        new SingleInstanceCast<>(mock(Actor.Factory.class), null);
    }

    @Test
    public void testCreatesAnInstance() {
        SingleInstanceActorMetadata metadata = new SingleInstanceActorMetadata("abc");
        Set<SingleInstanceActorMetadata> metadatas = Collections.singleton(metadata);
        Observable<MockModel> models = mock(Observable.class);

        Actor.Factory<MockModel, SingleInstanceActorMetadata> factory = mock(Actor.Factory.class);
        when(factory.create(metadata)).thenReturn(mock(Actor.class));

        SingleInstanceCast<MockModel> cast = new SingleInstanceCast<>(factory, models);
        cast.ensureCast(metadatas);

        verify(factory).create(metadata);
    }

    @Test
    public void testCreatesAnInstanceOnlyOne() {
        SingleInstanceActorMetadata metadata = new SingleInstanceActorMetadata("abc");
        Set<SingleInstanceActorMetadata> metadatas = Collections.singleton(metadata);
        Observable<MockModel> models = mock(Observable.class);

        Actor.Factory<MockModel, SingleInstanceActorMetadata> factory = mock(Actor.Factory.class);
        when(factory.create(metadata)).thenReturn(mock(Actor.class));

        SingleInstanceCast<MockModel> cast = new SingleInstanceCast<>(factory, models);

        cast.ensureCast(metadatas);
        cast.ensureCast(metadatas);

        verify(factory).create(metadata);
    }

    @Test
    public void testCreatesSeveralClasses() {
        SingleInstanceActorMetadata metadata1 = new SingleInstanceActorMetadata("abc");
        SingleInstanceActorMetadata metadata2 = new SingleInstanceActorMetadata("def");

        Set<SingleInstanceActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata1, metadata2));
        Observable<MockModel> models = mock(Observable.class);

        Actor.Factory<MockModel, SingleInstanceActorMetadata> factory = mock(Actor.Factory.class);
        when(factory.create(any(SingleInstanceActorMetadata.class))).thenReturn(mock(Actor.class));

        SingleInstanceCast<MockModel> cast = new SingleInstanceCast<>(factory, models);

        cast.ensureCast(metadatas);

        verify(factory).create(metadata1);
        verify(factory).create(metadata2);
    }

    @Test
    public void testPropagatesModelsObservable() {
        SingleInstanceActorMetadata metadata1 = new SingleInstanceActorMetadata("abc");
        SingleInstanceActorMetadata metadata2 = new SingleInstanceActorMetadata("def");

        Set<SingleInstanceActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata1, metadata2));
        Observable<MockModel> models = BehaviorSubject.create();

        Actor.Factory<MockModel, SingleInstanceActorMetadata> factory = mock(Actor.Factory.class);
        Actor<MockModel> actor1 = mock(Actor.class);
        Actor<MockModel> actor2 = mock(Actor.class);

        when(factory.create(metadata1)).thenReturn(actor1);
        when(factory.create(metadata2)).thenReturn(actor2);

        SingleInstanceCast<MockModel> cast = new SingleInstanceCast<>(factory, models);

        cast.ensureCast(metadatas);

        // TODO this is very naive, we will have to share the observable for sure!
        verify(actor1).subscribeTo(models);
        verify(actor2).subscribeTo(models);
    }

    // TODO remove one actor
}