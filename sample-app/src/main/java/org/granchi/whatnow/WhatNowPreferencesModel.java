package org.granchi.whatnow;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Model;

import javax.inject.Inject;

/**
 * Model for the app preferences.
 *
 * @author serandel
 */
public class WhatNowPreferencesModel extends Model {
    /**
     * Constructor.
     */
    @Inject
    public WhatNowPreferencesModel() {
        // TODO something
    }

    @Override
    public Model actUpon(Action action) {
        return this;
    }
}
