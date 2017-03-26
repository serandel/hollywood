package org.granchi.whatnow

import org.granchi.hollywood.Action
import org.granchi.hollywood.Model

import javax.inject.Inject

/**
 * Model for the app preferences.
 */
// TODO something
class WhatNowPreferencesModel @Inject constructor() : Model() {
    override fun actUpon(action: Action): Model? {
        return this
    }
}
