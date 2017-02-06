package org.granchi.hollywood.android;

import org.granchi.hollywood.Actor;
import org.granchi.hollywood.HollywoodApplication;
import org.granchi.hollywood.Model;
import org.granchi.hollywood.ModelExceptionHandler;

import java.util.Collection;

/**
 * The real HollywoodApplication to be used by the HollywoodAndroidApplication delegate.
 *
 * @author serandel
 */
// TODO creation parameters
public class HollywoodAndroidApplicationCompanion extends
                                                  HollywoodApplication {
    // TODO keep it or not?
    private final HollywoodAndroidApplication app;

    /**
     * Constructor.
     *
     * @param app              HollywoodAndroidApplication, can't be null
     * @param initialModel     initial Model, can't be null
     * @param actors           Actors of the application, can't be null or empty
     * @param exceptionHandler handler for Exceptions during Model.actUpon, can be null to just end
     *                         the app if it
     */
    public HollywoodAndroidApplicationCompanion(HollywoodAndroidApplication app,
                                                Model initialModel,
                                                Collection<Actor> actors,
                                                ModelExceptionHandler exceptionHandler) {
        super(initialModel, actors, exceptionHandler);

        if (app == null) {
            throw new NullPointerException();
        }

        // No logError should happen in the constructor, it can wait till run()
        this.app = app;
    }
}
