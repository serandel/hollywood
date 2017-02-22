package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompositeModelTest {
    @Mock
    private Action action;

    @Mock
    private Model model1, model2, model3, model4, model5;

    @Test(expected = NullPointerException.class)
    public void testCantHaveNullSubModelsArray() {
        new CompositeModel((Model[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void testCantHaveNullSubModels() {
        new CompositeModel(model1, null);
    }

    @Test
    public void testOrderDoesMatterInConstructor() {
        assertThat(new CompositeModel(model1, model2, model3))
                .isNotEqualTo(new CompositeModel(model2, model3, model1));
    }

    @Test
    public void testRespectEquals() {
        assertThat(new CompositeModel(model1, model2, model3))
                .isEqualTo(new CompositeModel(model1, model2, model3));
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

    @Test
    public void testPropagatesActionsStoresResults() {
        when(model1.actUpon(action)).thenReturn(model3);
        when(model2.actUpon(action)).thenReturn(model4);

        when(model3.actUpon(action)).thenReturn(model5);
        when(model4.actUpon(action)).thenReturn(model5);

        CompositeModel compositeModel = new CompositeModel(model1, model2);
        Model compositeModel2 = compositeModel.actUpon(action);
        Model compositeModel3 = compositeModel2.actUpon(action);
        compositeModel3.actUpon(action);

        verify(model1).actUpon(action);
        verify(model2).actUpon(action);

        assertThat(compositeModel2).isInstanceOf(CompositeModel.class);

        verify(model3).actUpon(action);
        verify(model4).actUpon(action);
        assertThat(compositeModel3).isInstanceOf(CompositeModel.class);

        // Only once
        verify(model5).actUpon(action);
    }

    @Test
    public void testCompositeResultModelsAreAggregatedSoNoDuplications() {
        when(model1.actUpon(action)).thenReturn(new CompositeModel(model3, model4));
        when(model2.actUpon(action)).thenReturn(new CompositeModel(model3, model5));

        new CompositeModel(model1, model2).actUpon(action).actUpon(action);

        verify(model3).actUpon(action);
        verify(model4).actUpon(action);
        verify(model5).actUpon(action);
    }

    @Test
    public void testOrderOfTheResultsDependsOnOrderOfTheSubmodels() {
        when(model1.actUpon(action)).thenReturn(new CompositeModel(model3, model4));
        when(model2.actUpon(action)).thenReturn(new CompositeModel(model3, model5));

        Model resultModel = new CompositeModel(model1, model2).actUpon(action);

        assertThat(resultModel).isInstanceOf(CompositeModel.class);

        assertThat(((CompositeModel) resultModel).getModels())
                .containsExactly(model3, model4, model5)
                .inOrder();
    }

    @Test
    public void testNoSubModelsReturnNull() {
        Model resultModel = new CompositeModel(model1, model2).actUpon(action);

        assertThat(resultModel).isNull();
    }

    @Test
    public void testNoSubModelsOfType() {
        when(model1.getSubmodelsOfType(SubModel.class)).thenReturn(Collections.emptyList());
        when(model2.getSubmodelsOfType(SubModel.class)).thenReturn(Collections.emptyList());

        assertThat(new CompositeModel(model1, model2)
                           .getSubmodelsOfType(SubModel.class)).isEmpty();
    }

    @Test
    public void testIsSubModelOfItsType() {
        CompositeModel compositeModel = new CompositeModel(model1);
        assertThat(compositeModel.getSubmodelsOfType(CompositeModel.class))
                .containsExactly(compositeModel);
    }

    @Test
    public void testIsSubModelOfAParentType() {
        SubCompositeModel compositeModel = new SubCompositeModel(model1);
        assertThat(compositeModel.getSubmodelsOfType(CompositeModel.class)).containsExactly(
                compositeModel);
    }

    @Test
    public void testGetsSubModelsOfType() {
        SubModel subModel1 = mock(SubModel.class);
        SubModel subModel2 = mock(SubModel.class);
        SubModel subModel3 = mock(SubModel.class);

        when(model1.getSubmodelsOfType(SubModel.class)).thenReturn(Arrays.asList(subModel1,
                                                                                 subModel2));
        when(model2.getSubmodelsOfType(SubModel.class)).thenReturn(Arrays.asList(subModel3,
                                                                                 subModel2));

        assertThat(new CompositeModel(model1, model2).getSubmodelsOfType(SubModel.class))
                .containsExactly(subModel1, subModel2, subModel3)
                .inOrder();
    }

    private static abstract class SubModel extends Model {
    }

    private static class SubCompositeModel extends CompositeModel {
        SubCompositeModel(Model... initialModels) {
            super(initialModels);
        }
    }
}
