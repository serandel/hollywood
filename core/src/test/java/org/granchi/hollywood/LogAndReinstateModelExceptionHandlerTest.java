package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.PrintStream;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LogAndReinstateModelExceptionHandlerTest {
    @Mock
    private Model model;

    @Mock
    private Action action;

    @Mock
    private PrintStream stream;

    @Test
    public void testModelIsTheSame() throws Exception {
        Exception ex = new RuntimeException();

        final boolean[] logged = {false};

        LogAndReinstateModelExceptionHandler
                handler =
                new LogAndReinstateModelExceptionHandler(
                        (model1, action1, exception) -> logged[0] = true);
        Model model2 = handler.onException(model, action, ex);

        assertThat(model).isSameAs(model2);
        assertThat(logged[0]).isTrue();
    }
}