package org.granchi.hollywood.android.preferences

import org.granchi.hollywood.Action
import org.granchi.hollywood.Actor
import org.granchi.hollywood.Model

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * [Actor] for manipulating SharedPreferences.
 */
class SharedPreferencesActor : Actor {
    override val actions = PublishSubject.create<Action>()

    override fun subscribeTo(models: Observable<Model>) {
        // No need for observeOn, because we only emit actions back, and those are already
        // serialized in the model thread
        // TODO figure out this
        models //.flatMapIterable<Any> { m -> m.getSubmodelsOfType(PreferencesModel::class.java!!) }
                .subscribeOn(Schedulers.io())
                .subscribe { model ->
                    actions.onNext(object : Action {

                        // TODO ejem
                    })
                }
    }
}