package org.granchi.hollywood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ModelTest {
    @Test
    public void testGetSubmodelsOfSameType() throws Exception {
        ParentModel model = new ParentModel();

        assertThat(model.getSubmodelsOfType(ParentModel.class)).containsExactly(model);
    }

    @Test
    public void testGetSubmodelsOfParentType() throws Exception {
        ChildModel model = new ChildModel();

        assertThat(model.getSubmodelsOfType(ParentModel.class)).containsExactly(model);
    }

    @Test
    public void testGetSubmodelsOfChildType() throws Exception {
        ParentModel model = new ParentModel();

        assertThat(model.getSubmodelsOfType(ChildModel.class)).isEmpty();
    }

    @Test
    public void testGetSubmodelsOfUnrelatedType() throws Exception {
        ParentModel model = new ParentModel();

        assertThat(model.getSubmodelsOfType(OtherParentModel.class)).isEmpty();
    }

    private class ParentModel extends Model {
        @Override
        protected Model actUpon(Action action) {
            return null;
        }
    }

    private class ChildModel extends ParentModel {
        @Override
        protected Model actUpon(Action action) {
            return null;
        }
    }

    public class OtherParentModel extends Model {
        @Override
        protected Model actUpon(Action action) {
            return null;
        }
    }
}