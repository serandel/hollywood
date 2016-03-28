package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.PrintStream;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LogAndReinstateModelExceptionHandlerTest {
    @Mock
    private Model<ActorMetadata> model;

    @Mock
    private Action action;

    @Mock
    private PrintStream stream;

    @Test
    public void testModelIsTheSame() throws Exception {
        Exception ex = new RuntimeException();

        final boolean[] logged = {false};

        LogAndReinstateModelExceptionHandler<ActorMetadata> handler = new LogAndReinstateModelExceptionHandler(
                (model1, action1, exception) -> logged[0] = true);
        Model<ActorMetadata> model2 = handler.onException(model, action, ex);

        assertThat(model).isSameAs(model2);
        assertThat(logged[0]).isTrue();
    }
}