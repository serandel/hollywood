package org.granchi.hollywood.android.preferences;

import org.granchi.hollywood.SingleInstanceActorMetadata;
import org.granchi.hollywood.preferences.PreferencesModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * PreferencesModel backed by Android SharedPreferences.
 *
 * @author serandel
 */
public abstract class SharedPreferencesModel extends PreferencesModel<SingleInstanceActorMetadata> {
    private final SingleInstanceActorMetadata
            actorMetadata =
            new SingleInstanceActorMetadata(SharedPreferencesActor.class.getName());

    @Override
    public Set<SingleInstanceActorMetadata> getActors() {
        return new HashSet<>(Collections.singletonList(actorMetadata));
    }
}
