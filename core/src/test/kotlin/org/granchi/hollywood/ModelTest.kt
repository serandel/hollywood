package org.granchi.hollywood

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ModelTest {
    @Test
    fun testGetSubmodelsOfSameType() {
        val model = ParentModel()

        assertThat(model.getSubmodelsOfType(ParentModel::class)).containsExactly(model)
    }

    @Test
    fun testGetSubmodelsOfParentType() {
        val model = ChildModel()

        assertThat(model.getSubmodelsOfType(ParentModel::class)).containsExactly(model)
    }

    @Test
    fun testGetSubmodelsOfChildType() {
        val model = ParentModel()

        assertThat(model.getSubmodelsOfType(ChildModel::class)).isEmpty()
    }

    @Test
    fun testGetSubmodelsOfUnrelatedType() {
        val model = ParentModel()

        assertThat(model.getSubmodelsOfType(OtherParentModel::class)).isEmpty()
    }

    private open class ParentModel : Model() {
        override fun actUpon(action: Action): Model? {
            TODO("not implemented")
        }
    }

    private class ChildModel : ParentModel()

    class OtherParentModel : Model() {
        override fun actUpon(action: Action): Model? {
            TODO("not implemented")
        }
    }
}