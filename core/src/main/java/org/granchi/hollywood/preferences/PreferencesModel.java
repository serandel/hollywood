package org.granchi.hollywood.preferences;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Model;

/**
 * Model for storing and modifying a group of preferences.
 *
 * @author serandel
 */
// TODO do we really want a preferences model or get them integrated into the business logic?
public abstract class PreferencesModel implements Model {
    @Override
    public Model actUpon(Action action) {
        return null;
    }
}
