package org.granchi.whatnow.framework.dagger

import android.app.Application
import dagger.Module
import dagger.Provides
import org.granchi.hollywood.*
import org.granchi.whatnow.StubActor
import org.granchi.whatnow.TasksModel
import org.granchi.whatnow.WhatNowApplication
import org.granchi.whatnow.WhatNowPreferencesModel
import java.util.*

/**
 * Dagger module for the app.
 *
 * It's open to let a debug module inherit from it.
 */
@Module
open class WhatNowModule(private val app: WhatNowApplication) {
    @Provides
    fun provideWhatNowApplication(): WhatNowApplication {
        return app
    }

    @Provides
    fun provideApplication(): Application {
        return app
    }

    @Provides
    @JvmSuppressWildcards
            // Needed so the stubs aren't generated having a Collection<? extends Actor>, for some reason
    fun provideHollywoodApp(initialModel: Model,
                            actors: Collection<Actor>,
                            exceptionHandler: ModelExceptionHandler): HollywoodApplication {
        return HollywoodApplication(initialModel,
                actors,
                exceptionHandler)
    }

    @Provides
    fun provideInitialModel(tasksModel: TasksModel,
                            preferencesModel: WhatNowPreferencesModel): Model {
        return CompositeModel(tasksModel, preferencesModel)
    }

    @Provides
    fun provideActors(): Collection<Actor> {
        return Arrays.asList<Actor>(StubActor())
    }

    @Provides
    fun provideModelExceptionHandler(): ModelExceptionHandler {
        return ReinstateModelExceptionHandler()
    }
}
