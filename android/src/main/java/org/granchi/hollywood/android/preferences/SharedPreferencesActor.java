package org.granchi.hollywood.android.preferences;

import org.granchi.hollywood.Action;
import org.granchi.hollywood.Actor;
import org.granchi.hollywood.Model;
import org.granchi.hollywood.ModelFilter;
import org.granchi.hollywood.preferences.PreferencesModel;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Actor for manipulating SharedPreferences.
 *
 * @author serandel
 */
public class SharedPreferencesActor implements Actor {
    private final PublishSubject<Action> actions = PublishSubject.create();

    @Override
    public Observable<Action> getActions() {
        return actions;
    }

    @Override
    public void subscribeTo(Observable<Model> models) {
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