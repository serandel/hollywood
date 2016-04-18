package org.granchi.whatnow;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Model;
import org.granchi.hollywood.SingleInstanceActorMetadata;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

/**
 * Model for existing tasks.
 *
 * @author serandel
 */
public class TasksModel implements Model<SingleInstanceActorMetadata> {
    public final List<Task> tasks;

    @Inject
    public TasksModel() {
        this.tasks = Collections.unmodifiableList(Collections.emptyList());
    }

    @Override
    public Model<SingleInstanceActorMetadata> actUpon(Action action) {
        return this;
    }

    @Override
    public Set<SingleInstanceActorMetadata> getActors() {
        return null;
    }
}
