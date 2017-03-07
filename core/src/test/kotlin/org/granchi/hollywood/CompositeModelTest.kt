package org.granchi.hollywood

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CompositeModelTest {
    @Mock
    private lateinit var action: Action

    @Mock
    private lateinit var model1: Model
    @Mock
    private lateinit var model2: Model
    @Mock
    private lateinit var model3: Model
    @Mock
    private lateinit var model4: Model
    @Mock
    private lateinit var model5: Model

    @Test
    fun testOrderDoesMatterInConstructor() {
        assertThat(CompositeModel(model1, model2, model3))
                .isNotEqualTo(CompositeModel(model2, model3, model1))
    }

    @Test
    fun testRespectEquals() {
        assertThat(CompositeModel(model1, model2, model3))
                .isEqualTo(CompositeModel(model1, model2, model3))
    }

    @Test
    fun testRemovesDuplicateSubModelsInConstructor() {
        val compositeModel = CompositeModel(model1, model2, model1)

        compositeModel.actUpon(action)

        // Just once
        verify(model1).actUpon(action)
        verify(model2).actUpon(action)
    }

    @Test
    fun testPropagatesActionsStoresResults() {
        whenever(model1.actUpon(action)).thenReturn(model3)
        whenever(model2.actUpon(action)).thenReturn(model4)

        whenever(model3.actUpon(action)).thenReturn(model5)
        whenever(model4.actUpon(action)).thenReturn(model5)

        val compositeModel = CompositeModel(model1, model2)
        val compositeModel2 = compositeModel.actUpon(action)
        val compositeModel3 = compositeModel2?.actUpon(action)
        compositeModel3?.actUpon(action)

        verify(model1).actUpon(action)
        verify(model2).actUpon(action)

        assertThat(compositeModel2 is CompositeModel)

        verify(model3).actUpon(action)
        verify(model4).actUpon(action)
        assertThat(compositeModel3 is CompositeModel)

        // Only once
        verify(model5).actUpon(action)
    }

    @Test
    fun testCompositeResultModelsAreAggregatedSoNoDuplications() {
        whenever(model1.actUpon(action)).thenReturn(CompositeModel(model3, model4))
        whenever(model2.actUpon(action)).thenReturn(CompositeModel(model3, model5))

        CompositeModel(model1, model2).actUpon(action)?.actUpon(action)

        verify(model3).actUpon(action)
        verify(model4).actUpon(action)
        verify(model5).actUpon(action)
    }

    @Test
    fun testOrderOfTheResultsDependsOnOrderOfTheSubmodels() {
        whenever(model1.actUpon(action)).thenReturn(CompositeModel(model3, model4))
        whenever(model2.actUpon(action)).thenReturn(CompositeModel(model3, model5))

        val resultModel = CompositeModel(model1, model2).actUpon(action)

        assertThat((resultModel as CompositeModel).models)
                .containsExactly(model3, model4, model5)
                .inOrder()
    }

    @Test
    fun testNoSubModelsReturnNull() {
        val resultModel = CompositeModel(model1, model2).actUpon(action)

        assertThat(resultModel).isNull()
    }

    @Test
    fun testNoSubModelsOfType() {
        whenever(model1.getSubmodelsOfType(SubModel::class)).thenReturn(emptyList())
        whenever(model2.getSubmodelsOfType(SubModel::class)).thenReturn(emptyList())

        assertThat(CompositeModel(model1, model2)
                .getSubmodelsOfType(SubModel::class)).isEmpty()
    }

    @Test
    fun testIsSubModelOfItsType() {
        val compositeModel = CompositeModel(model1)
        assertThat(compositeModel.getSubmodelsOfType(CompositeModel::class))
                .containsExactly(compositeModel)
    }

    @Test
    fun testGetsSubModelsOfType() {
        val subModel1 = mock<SubModel>()
        val subModel2 = mock<SubModel>()
        val subModel3 = mock<SubModel>()

        whenever(model1.getSubmodelsOfType(SubModel::class)).thenReturn(listOf(subModel1,
                subModel2))
        whenever(model2.getSubmodelsOfType(SubModel::class)).thenReturn(listOf(subModel3,
                subModel2))

        assertThat(CompositeModel(model1, model2).getSubmodelsOfType(SubModel::class))
                .containsExactly(subModel1, subModel2, subModel3)
                .inOrder()
    }

    private abstract class SubModel : Model()
}
