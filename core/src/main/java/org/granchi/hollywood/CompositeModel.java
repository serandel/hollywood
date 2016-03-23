package org.granchi.hollywood;

import java.util.HashSet;
import java.util.Set;

/**
 * A Model that only contains several subModels.
 * <p/>
 * It serves as a way to decompose business logic in several components.
 *
 * @author serandel
 */
public class CompositeModel<D extends ActorMetadata> implements Model<D> {
    private Set<Model<D>> models;

    /**
     * Constructor.
     * <p/>
     * Initializes the Model with at least one subModel.
     *
     * @param initialModels initial models
     */
    public CompositeModel(Set<Model<D>> initialModels) {
        if (initialModels.size() == 0) {
            throw new IllegalArgumentException();
        }

        for (Model<D> model : initialModels) {
            if (model == null) {
                throw new NullPointerException();
            }
        }

        models = new HashSet<>(initialModels);
    }

    @Override
    public Model<D> actUpon(Action action) {
        Set<Model<D>> resultSubModels = new HashSet<>();

        for (Model<D> model : models) {
            resultSubModels.add(model.actUpon(action));
        }

        // I hate Java generics sometimes...
        return new CompositeModel<D>(resultSubModels);
    }

    @Override
    public Set<D> getActors() {
        return null;
    }
}