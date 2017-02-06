package org.granchi.whatnow;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Model;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Model for existing tasks.
 *
 * @author serandel
 */
public class TasksModel implements Model {
    public final List<Task> tasks;

    @Inject
    public TasksModel() {
        this.tasks = Collections.unmodifiableList(Collections.emptyList());
    }

    @Override
    public Model actUpon(Action action) {
        return this;
    }
}
