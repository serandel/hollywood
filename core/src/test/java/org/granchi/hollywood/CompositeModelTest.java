package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompositeModelTest {

    // TODO array null
    // TODO varargs vacio
    // TODO array con null
    // TODO no se puede modificar un getModels


    @Mock
    private Action action;

    @Mock
    private Model<ActorMetadata> model1, model2, model3, model4, model5;

    @Mock
    private ActorMetadata metadata1, metadata2, metadata3, metadata4;

    @Test(expected = NullPointerException.class)
    public void testCantHaveNullModelsSet() {
        new CompositeModel<>((Model<SingleInstanceActorMetadata>[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveNullModels() {
        new CompositeModel<>(new HashSet<>(Arrays.asList(model1, null)));
    }

    @Test
    public void testPropagatesActionsStoresResultsAndGroupsActors() {
        when(model1.actUpon(action)).thenReturn(model3);
        when(model2.actUpon(action)).thenReturn(model4);

        when(model1.getActors()).thenReturn(new HashSet<>(Arrays.asList(metadata1, metadata3)));
        when(model2.getActors()).thenReturn(new HashSet<>(Arrays.asList(metadata1, metadata2)));

        when(model3.actUpon(action)).thenReturn(model5);
        when(model4.actUpon(action)).thenReturn(model5);

        CompositeModel<ActorMetadata> compositeModel = new CompositeModel<>(
                new HashSet<>(Arrays.asList(model1, model2)));
        Model<ActorMetadata> compositeModel2 = compositeModel.actUpon(action);
        Model<ActorMetadata> compositeModel3 = compositeModel2.actUpon(action);
        compositeModel3.actUpon(action);

        verify(model1).actUpon(action);
        verify(model2).actUpon(action);

        assertThat(compositeModel2 instanceof CompositeModel);
        assertThat(compositeModel2.getActors().size() == 3);
        assertThat(compositeModel2.getActors().contains(metadata1));
        assertThat(compositeModel2.getActors().contains(metadata2));
        assertThat(compositeModel2.getActors().contains(metadata3));

        verify(model3).actUpon(action);
        verify(model4).actUpon(action);
        assertThat(compositeModel3 instanceof CompositeModel);

        // Only once
        verify(model5).actUpon(action);
    }

    @Test
    public void testCompositeResultModelsAreAggregatedSoNoDuplications() {
        when(model1.actUpon(action)).thenReturn(new CompositeModel<>(new HashSet<>(Arrays.asList(
                model3,
                model4))));
        when(model2.actUpon(action)).thenReturn(new CompositeModel<>(new HashSet<>(Arrays.asList(
                model3,
                model5))));

        CompositeModel<ActorMetadata>
                compositeModel =
                new CompositeModel<>(new HashSet<>(Arrays.asList(model1, model2)));
        compositeModel.actUpon(action).actUpon(action);

        verify(model3).actUpon(action);
        verify(model4).actUpon(action);
        verify(model5).actUpon(action);
    }

    @Test
    public void testNoSubModelsReturnNull() {
        CompositeModel<ActorMetadata> compositeModel = new CompositeModel<>(
                new HashSet<>(Arrays.asList(model1, model2)));
        Model<ActorMetadata> resultModel = compositeModel.actUpon(action);

        assertThat(resultModel).isNull();
    }

    // TODO incompatible actorMetadata? single instance, same class different properties
}