package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompositeModelTest {

    @Mock
    private Action action;

    @Mock
    private Model model1, model2, model3, model4, model5;

    @Test(expected = NullPointerException.class)
    public void testCantHaveNullSubModelsCollection() {
        new CompositeModel((Collection<Model>) null);
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveNullSubModelsVarargs() {
        new CompositeModel((Model[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveNullSubModelsInCollection() {
        new CompositeModel(Arrays.asList(model1, null));
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveNullSubModelsInVarargs() {
        new CompositeModel(model1, null);
    }

    @Test
    public void testBothConstructorsAreEquivalent() {
        assertThat(new CompositeModel(model1, model2, model3)).isEqualTo(
                new CompositeModel(Arrays.asList(
                        model1,
                        model2,
                        model3)));
    }

    @Test
    public void testOrderDoesNotMatterInConstructor() {
        assertThat(new CompositeModel(model1, model2, model3)).isEqualTo(new CompositeModel(
                model2,
                model3,
                model1));
    }

    @Test
    public void testRemovesDuplicateSubModelsInConstructor() {
        CompositeModel
                compositeModel =
                new CompositeModel(model1, model2, model1);

        compositeModel.actUpon(action);

        // Just once
        verify(model1).actUpon(action);
        verify(model2).actUpon(action);
    }

//    @Test
//    public void testPropagatesActionsStoresResultsAndGroupsActors() {
//        when(model1.actUpon(action)).thenReturn(model3);
//        when(model2.actUpon(action)).thenReturn(model4);
//
//        when(model1.getActors()).thenReturn(Arrays.asList(metadata1, metadata3));
//        when(model2.getActors()).thenReturn(Arrays.asList(metadata1, metadata2));
//
//        when(model3.actUpon(action)).thenReturn(model5);
//        when(model4.actUpon(action)).thenReturn(model5);
//
//        CompositeModel compositeModel = new CompositeModel(model1, model2);
//        Model compositeModel2 = compositeModel.actUpon(action);
//        Model compositeModel3 = compositeModel2.actUpon(action);
//        compositeModel3.actUpon(action);
//
//        assertThat(compositeModel.getActors().size()).isEqualTo(3);
//        assertThat(compositeModel.getActors()).contains(metadata1);
//        assertThat(compositeModel.getActors()).contains(metadata2);
//        assertThat(compositeModel.getActors()).contains(metadata3);
//
//        verify(model1).actUpon(action);
//        verify(model2).actUpon(action);
//
//        assertThat(compositeModel2).isInstanceOf(CompositeModel.class);
//
//        verify(model3).actUpon(action);
//        verify(model4).actUpon(action);
//        assertThat(compositeModel3).isInstanceOf(CompositeModel.class);
//
//        // Only once
//        verify(model5).actUpon(action);
//    }

    @Test
    public void testCompositeResultModelsAreAggregatedSoNoDuplications() {
        when(model1.actUpon(action)).thenReturn(new CompositeModel(new HashSet<>(Arrays.asList(
                model3,
                model4))));
        when(model2.actUpon(action)).thenReturn(new CompositeModel(new HashSet<>(Arrays.asList(
                model3,
                model5))));

        new CompositeModel(model1, model2).actUpon(action).actUpon(action);

        verify(model3).actUpon(action);
        verify(model4).actUpon(action);
        verify(model5).actUpon(action);
    }

    @Test
    public void testNoSubModelsReturnNull() {
        Model resultModel = new CompositeModel(model1, model2).actUpon(action);

        assertThat(resultModel).isNull();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetModelsCantAddMore() {
        new CompositeModel(model1).getModels().add(model2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetModelsCantRemove() {
        new CompositeModel(model1).getModels().remove(model1);
    }

    // TODO incompatible actorMetadata? single instance, same class different properties
}