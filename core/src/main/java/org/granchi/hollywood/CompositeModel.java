package org.granchi.hollywood;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A Model that only contains several subModels.
 * <p>
 * It serves as a way to decompose business logic in several components.
 *
 * @param <D> type of ActorMetadata it uses for building and identifying Actors
 *
 * @author serandel
 */
public class CompositeModel<D extends ActorMetadata> implements Model<D> {
    private Set<Model<D>> models;

    /**
     * Constructor.
     * <p>
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
        Set<Model<D>> resultModels = new HashSet<>();

        for (Model<D> model : models) {
            Model<D> resultModel = model.actUpon(action);

            if (resultModel != null) {
                if (resultModel instanceof CompositeModel) {
                    resultModels.addAll(((CompositeModel<D>) resultModel).getModels());
                } else {
                    resultModels.add(resultModel);
                }
            }
        }

        return resultModels.isEmpty() ? null : new CompositeModel<>(resultModels);
    }

    /**
     * Gets all subModels.
     * <p>
     * Only intended to flatten CompositeModel results.
     *
     * @return Models
     */
    Collection<? extends Model<D>> getModels() {
        return models;
    }

    @Override
    public Set<D> getActors() {
        Set<D> actors = new HashSet<>();

        for (Model<D> model : models) {
            actors.addAll(model.getActors());
        }

        return actors;
    }
}