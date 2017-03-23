package org.granchi.whatnow

import org.granchi.hollywood.Action
import org.granchi.hollywood.Actor
import org.granchi.hollywood.Model
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import io.reactivex.Observable

/**
 * Stub Actor, just for using it so the app runs while building real ones.

 * @author serandel
 */
class StubActor : Actor {

    override val actions: Observable<Action>
        get() = Observable.never<Action>()

    override fun subscribeTo(models: Observable<Model>) {
        models.subscribe { model -> log.info("Model received: {}", model) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(StubActor::class.java)
    }
}
