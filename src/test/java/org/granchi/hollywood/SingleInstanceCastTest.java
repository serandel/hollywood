package org.granchi.hollywood;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SingleInstanceCastTest {
    private interface MockModel extends Model<MockModel, SingleInstanceActorMetadata> {
    }

    // Different subclasses, so the instances get registered correctly
    private interface ActorSub1 extends Actor<MockModel> {
    }

    private interface ActorSub2 extends Actor<MockModel> {
    }

    @Mock
    private MockModel model;

    @Mock
    private Actor.Factory<MockModel, SingleInstanceActorMetadata> actorFactory;

    @Mock
    private ActorSub1 actor;
    @Mock
    private ActorSub2 actor2;

    private SingleInstanceActorMetadata metadata, metadata2;

    @Before
    public void setUp() {
        metadata = new SingleInstanceActorMetadata(actor.getClass().getName());
        metadata2 = new SingleInstanceActorMetadata(actor2.getClass().getName());
    }

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

        assertThat(!cast.containsActorFrom(metadata));

        cast.ensureCast(metadatas);

        verify(actorFactory).create(metadata);

        assertThat(cast.containsActorFrom(metadata));
    }

    @Test
    public void testCreatesAnInstanceOnlyOnce() {
        SingleInstanceActorMetadata metadata = new SingleInstanceActorMetadata(actor.getClass().getName());
        Set<SingleInstanceActorMetadata> metadatas = Collections.singleton(metadata);

        when(actorFactory.create(metadata)).thenReturn(actor);

        SingleInstanceCast<MockModel> cast = new SingleInstanceCast<>(actorFactory, Observable.empty());

        cast.ensureCast(metadatas);
        cast.ensureCast(metadatas);

        // Just once
        verify(actorFactory).create(metadata);

        assertThat(cast.containsActorFrom(metadata));
    }

    @Test
    public void testCreatesSeveralClasses() {
        Set<SingleInstanceActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata, metadata2));

        when(actorFactory.create(metadata)).thenReturn(actor);
        when(actorFactory.create(metadata2)).thenReturn(actor2);

        SingleInstanceCast<MockModel> cast = new SingleInstanceCast<>(actorFactory, Observable.empty());

        cast.ensureCast(metadatas);

        verify(actorFactory).create(metadata);
        verify(actorFactory).create(metadata2);

        assertThat(cast.containsActorFrom(metadata)).isTrue();
        assertThat(cast.containsActorFrom(metadata2)).isTrue();
    }

    @Test
    public void testRemoveUnwantedClasses() {
        Set<SingleInstanceActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata, metadata2));

        when(actorFactory.create(metadata)).thenReturn(actor);
        when(actorFactory.create(metadata2)).thenReturn(actor2);

        SingleInstanceCast<MockModel> cast = new SingleInstanceCast<>(actorFactory, Observable.empty());

        cast.ensureCast(metadatas);

        verify(actorFactory).create(metadata);
        verify(actorFactory).create(metadata2);

        assertThat(cast.containsActorFrom(metadata)).isTrue();
        assertThat(cast.containsActorFrom(metadata2)).isTrue();

        metadatas.remove(metadata2);
        cast.ensureCast(metadatas);

        assertThat(cast.containsActorFrom(metadata)).isTrue();
        assertThat(cast.containsActorFrom(metadata2)).isFalse();
    }
}