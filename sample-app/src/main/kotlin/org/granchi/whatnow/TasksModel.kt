package org.granchi.whatnow

import org.granchi.hollywood.Action
import org.granchi.hollywood.Model
import javax.inject.Inject

/**
 * Model for existing tasks.
 */
class TasksModel @Inject constructor() : Model() {
    val tasks = emptyList<Task>()

    override fun actUpon(action: Action): Model? {
        return this
    }
}
