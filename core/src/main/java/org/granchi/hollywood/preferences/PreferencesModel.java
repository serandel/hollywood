package org.granchi.hollywood.preferences;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Model;

/**
 * Model for storing and modifying a group of preferences.
 *
 * @author serandel
 */
public abstract class PreferencesModel implements Model {
    @Override
    public Model actUpon(Action action) {
        return null;
    }
}
