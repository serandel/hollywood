package org.granchi.hollywood.android;

import org.granchi.hollywood.Crew;
import org.granchi.hollywood.HollywoodApplication;
import org.granchi.hollywood.Model;
import org.granchi.hollywood.ModelExceptionHandler;
import org.granchi.hollywood.SingleInstanceActorMetadata;

import timber.log.Timber;

/**
 * The real HollywoodApplication to be used by the HollywoodAndroidApplication delegate.
 *
 * @author serandel
 */
// TODO creation parameters
public class HollywoodAndroidApplicationCompanion extends
                                                  HollywoodApplication<SingleInstanceActorMetadata> {
    private final HollywoodAndroidApplication app;

    /**
     * Constructor.
     *
     * @param app              HollywoodAndroidApplication, can't be null
     * @param initialModel     initial Model, can't be null
     * @param crewFactory      factory for Crew, can't be null
     * @param exceptionHandler handler for Exceptions during Model.actUpon, can be null to just end
     *                         the app if it
     */
    public HollywoodAndroidApplicationCompanion(HollywoodAndroidApplication app,
                                                Model<SingleInstanceActorMetadata> initialModel,
                                                Crew.Factory<SingleInstanceActorMetadata>
                                                        crewFactory,
                                                ModelExceptionHandler<SingleInstanceActorMetadata> exceptionHandler) {
        super(initialModel, crewFactory, exceptionHandler);

        if (app == null) {
            throw new NullPointerException();
        }

        // No logError should happen in the constructor, it can wait till run()
        this.app = app;
    }

    @Override
    protected void logWarning(String msg) {
        Timber.w(msg);

    }

    @Override
    protected void logError(String msg, Throwable throwable) {
        Timber.e(throwable, msg);

        app.showError(msg);
    }

    @Override
    protected void logInfo(String msg) {
        Timber.i(msg);
    }

    @Override
    protected void logDebug(String msg) {
        Timber.d(msg);
    }
}
