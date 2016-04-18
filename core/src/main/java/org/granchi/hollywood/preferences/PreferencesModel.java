package org.granchi.hollywood.preferences;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.ActorMetadata;
import org.granchi.hollywood.Model;

/**
 * Model for storing and modifying a group of preferences.
 *
 * @param <D> type of ActorMetadata it uses for building and identifying Actors
 * @author serandel
 */
public abstract class PreferencesModel<D extends ActorMetadata> implements Model<D> {
    @Override
    public Model<D> actUpon(Action action) {
        return null;
    }
}
