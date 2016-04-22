package org.granchi.hollywood;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A Model that only contains several subModels.
 * <p>
 * It serves as a way to decompose business logic in several components.
 *
 * @param <D> type of ActorMetadata it uses for building and identifying Actors
 * @author serandel
 */
public class CompositeModel<D extends ActorMetadata> implements Model<D> {
    private List<Model<D>> models;


    /**
     * Constructor.
     * <p>
     * Initializes the Model with at least one subModel.
     * <p>
     * It's just a gentler syntax for the other constructor.
     *
     * @param initialModels initial models, the order is irrelevant
     */
    public CompositeModel(Model<D>... initialModels) {
        this(Arrays.asList(initialModels));
    }

    /**
     * Constructor.
     * <p>
     * Initializes the Model with at least one subModel.
     *
     * @param initialModels initial models
     */
    public CompositeModel(Collection<Model<D>> initialModels) {
        if (initialModels == null || initialModels.size() == 0) {
            throw new IllegalArgumentException();
        }

        for (Model<D> model : initialModels) {
            if (model == null) {
                throw new NullPointerException();
            }
        }

        models = new ArrayList<>(initialModels);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Model<D> actUpon(Action action) {
        // It's easier for the submodels if they don't have to worry about duplicates
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
        return Collections.unmodifiableCollection(models);
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