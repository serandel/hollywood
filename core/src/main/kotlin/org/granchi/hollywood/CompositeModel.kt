package org.granchi.hollywood

import kotlin.reflect.KClass

/**
 * A Model that only contains several submodels.
 *
 * Their order can be important, because actions will be applied to every subModel following the
 * same order.
 *
 * It serves as a way to decompose business logic in several components.
 */
class CompositeModel(private var models: List<Model>) : Model() {
    constructor(vararg model: Model) : this(listOf(* model))

    init {
        if (models.isEmpty()) {
            throw IllegalArgumentException()
        } else {
            models = models.distinct()
        }
    }

    override fun actUpon(action: Action): Model? {
        val resultModels = models
                .map {
                    it.actUpon(action)
                }
                .filterNotNull()
                .flatMap {
                    (it as? CompositeModel)?.models ?: listOf(it)
                }
                .distinct()

        return if (resultModels.isEmpty()) {
            null
        } else {
            CompositeModel(resultModels)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Model> getSubmodelsOfType(type: KClass<out T>): Collection<T> {
        return if (type.isInstance(this)) {
            listOf(this as T)
        } else {
            models
                    .flatMap { it.getSubmodelsOfType(type) }
                    .distinct()
        }
    }
}
