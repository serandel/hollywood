package org.granchi.hollywood

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ReinstateModelExceptionHandlerTest {
    @Mock
    private lateinit var model: Model

    @Mock
    private lateinit var action: Action

    @Test
    fun testModelIsTheSame() {
        val model2 = ReinstateModelExceptionHandler()
                .onException(model, action, RuntimeException())

        assertThat(model).isSameAs(model2)
    }
}
