package org.granchi.hollywood;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class SingleInstanceCrewTest {
//    @Mock
//    private Model<ActorMetadata> model;
//    @Mock
//    private Actor.Factory<SingleInstanceActorMetadata> actorFactory;
//    @Mock
//    private ActorSub1 actor;
//    @Mock
//    private ActorSub2 actor2;
//    private SingleInstanceActorMetadata metadata, metadata2;
//
//    @Before
//    public void setUp() {
//        metadata = new SingleInstanceActorMetadata(actor.getClass().getName());
//        metadata2 = new SingleInstanceActorMetadata(actor2.getClass().getName());
//
//        when(actor.getActions()).thenReturn(Observable.empty());
//        when(actor2.getActions()).thenReturn(Observable.empty());
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void testCantHaveANullActorFactory() {
//        new SingleInstanceCrew(null, Observable.<Model<SingleInstanceActorMetadata>>empty());
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void testCantHaveANullModelsObservable() {
//        new SingleInstanceCrew(actorFactory, null);
//    }
//
//    @Test
//    public void testCreatesAnInstance() {
//        SingleInstanceActorMetadata metadata = new SingleInstanceActorMetadata("abc");
//        Set<SingleInstanceActorMetadata> metadatas = Collections.singleton(metadata);
//
//        when(actorFactory.create(metadata)).thenReturn(actor);
//
//        SingleInstanceCrew cast = new SingleInstanceCrew(actorFactory, Observable.empty());
//
//        assertThat(!cast.containsActorFrom(metadata));
//
//        cast.ensureCrew(metadatas);
//
//        verify(actorFactory).create(metadata);
//
//        assertThat(cast.containsActorFrom(metadata));
//    }
//
//    @Test
//    public void testCreatesAnInstanceOnlyOnce() {
//        SingleInstanceActorMetadata metadata = new SingleInstanceActorMetadata(actor.getClass()
// .getName());
//        Set<SingleInstanceActorMetadata> metadatas = Collections.singleton(metadata);
//
//        when(actorFactory.create(metadata)).thenReturn(actor);
//
//        SingleInstanceCrew cast = new SingleInstanceCrew(actorFactory, Observable.empty());
//
//        cast.ensureCrew(metadatas);
//        cast.ensureCrew(metadatas);
//
//        // Just once
//        verify(actorFactory).create(metadata);
//
//        assertThat(cast.containsActorFrom(metadata));
//    }
//
//    @Test
//    public void testCreatesSeveralClasses() {
//        Set<SingleInstanceActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata,
// metadata2));
//
//        when(actorFactory.create(metadata)).thenReturn(actor);
//        when(actorFactory.create(metadata2)).thenReturn(actor2);
//
//        SingleInstanceCrew cast = new SingleInstanceCrew(actorFactory, Observable.empty());
//
//        cast.ensureCrew(metadatas);
//
//        verify(actorFactory).create(metadata);
//        verify(actorFactory).create(metadata2);
//
//        assertThat(cast.containsActorFrom(metadata)).isTrue();
//        assertThat(cast.containsActorFrom(metadata2)).isTrue();
//    }
//
//    @Test
//    public void testRemoveUnwantedClasses() {
//        Set<SingleInstanceActorMetadata> metadatas = new HashSet<>(Arrays.asList(metadata,
// metadata2));
//
//        when(actorFactory.create(metadata)).thenReturn(actor);
//        when(actorFactory.create(metadata2)).thenReturn(actor2);
//
//        SingleInstanceCrew cast = new SingleInstanceCrew(actorFactory, Observable.empty());
//
//        cast.ensureCrew(metadatas);
//
//        verify(actorFactory).create(metadata);
//        verify(actorFactory).create(metadata2);
//
//        assertThat(cast.containsActorFrom(metadata)).isTrue();
//        assertThat(cast.containsActorFrom(metadata2)).isTrue();
//
//        metadatas.remove(metadata2);
//        cast.ensureCrew(metadatas);
//
//        assertThat(cast.containsActorFrom(metadata)).isTrue();
//        assertThat(cast.containsActorFrom(metadata2)).isFalse();
//    }
//
//    private enum TestRoster extends Roster {
//        ACTOR1, ACTOR2;
//    }
//
//    // Different subclasses, so the instances get registered correctly
//    private interface ActorSub1 extends Actor<SingleInstanceActorMetadata> {
//    }
//
//    private interface ActorSub2 extends Actor<SingleInstanceActorMetadata> {
//    }
//
//    // TODO same class, different parameters, exception
}