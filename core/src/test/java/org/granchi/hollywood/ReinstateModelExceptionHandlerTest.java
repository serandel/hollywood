package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ReinstateModelExceptionHandlerTest {
    @Mock
    private Model model;

    @Mock
    private Action action;

    @Test
    public void testModelIsTheSame() throws Exception {
        Model model2 = new ReinstateModelExceptionHandler()
                .onException(model, action, new RuntimeException());

        assertThat(model).isSameAs(model2);
    }
}
