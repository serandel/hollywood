package org.granchi.hollywood.android.preferences;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Actor;
import org.granchi.hollywood.Model;
import org.granchi.hollywood.ModelFilter;
import org.granchi.hollywood.SingleInstanceActorMetadata;
import org.granchi.hollywood.preferences.PreferencesModel;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Actor for manipulating SharedPreferences.
 *
 * @author serandel
 */
public class SharedPreferencesActor implements Actor<SingleInstanceActorMetadata> {
    private final PublishSubject<Action> actions = PublishSubject.create();

    @Override
    public Observable<Action> getActions() {
        return actions;
    }

    @Override
    public void subscribeTo(Observable<Model<SingleInstanceActorMetadata>> models) {
        // No need for observeOn, because we only emit actions back, and those are already
        // serialized in the model thread
        models.compose(ModelFilter.modelOfType(PreferencesModel.class))
              .subscribeOn(Schedulers.io())
              .subscribe(model -> {
                  actions.onNext(new Action() {
                      // TODO ejem
                  });
              });
    }
}
