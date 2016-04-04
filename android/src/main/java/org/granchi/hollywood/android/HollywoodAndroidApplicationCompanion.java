package org.granchi.hollywood.android;

import org.granchi.hollywood.Cast;
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
public class HollywoodAndroidApplicationCompanion extends HollywoodApplication<SingleInstanceActorMetadata> {
    /**
     * Constructor.
     *
     * @param initialModel     initial Model, can't be null
     * @param castFactory      factory for Cast, can't be null
     * @param exceptionHandler handler for Exceptions during Model.actUpon, can be null to just end the app if it
     */
    public HollywoodAndroidApplicationCompanion(Model<SingleInstanceActorMetadata> initialModel, Cast.Factory<SingleInstanceActorMetadata> castFactory, ModelExceptionHandler<SingleInstanceActorMetadata> exceptionHandler) {
        super(initialModel, castFactory, exceptionHandler);
    }

    @Override
    protected void logWarning(String msg) {
        Timber.w(msg);

    }

    @Override
    protected void logError(String msg, Throwable throwable) {
        Timber.e(throwable, msg);

        HollywoodAndroidApplication.getInstance().showError(msg);
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
