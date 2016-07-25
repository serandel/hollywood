package org.granchi.hollywood;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A Model that only contains several subModels.
 * <p>
 * It serves as a way to decompose business logic in several components.
 *
 * @author serandel
 */
public class CompositeModel implements Model {
    private Set<Model> models;


    /**
     * Constructor.
     * <p>
     * Initializes the Model with at least one subModel.
     * <p>
     * It's just a gentler syntax for the other constructor.
     *
     * @param initialModels initial models, the order is irrelevant
     */
    public CompositeModel(Model... initialModels) {
        this(Arrays.asList(initialModels));
    }

    /**
     * Constructor.
     * <p>
     * Initializes the Model with at least one subModel.
     *
     * @param initialModels initial models
     */
    public CompositeModel(Collection<Model> initialModels) {
        if (initialModels == null) {
            throw new NullPointerException();
        }
        if (initialModels.size() == 0) {
            throw new IllegalArgumentException();
        }

        for (Model model : initialModels) {
            if (model == null) {
                throw new NullPointerException();
            }
        }

        models = new HashSet<>(initialModels);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Model actUpon(Action action) {
        // It's easier for the submodels if they don't have to worry about duplicates
        Set<Model> resultModels = new HashSet<>();

        for (Model model : models) {
            Model resultModel = model.actUpon(action);

            if (resultModel != null) {
                if (resultModel instanceof CompositeModel) {
                    resultModels.addAll(((CompositeModel) resultModel).getModels());
                } else {
                    resultModels.add(resultModel);
                }
            }
        }

        return resultModels.isEmpty() ? null : new CompositeModel(resultModels);
    }

    /**
     * Gets all subModels.
     * <p>
     * Only intended to flatten CompositeModel results.
     *
     * @return Models
     */
    Collection<Model> getModels() {
        return Collections.unmodifiableCollection(models);
    }

    // TODO getSubmodelsOfType?

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeModel)) {
            return false;
        }

        CompositeModel that = (CompositeModel) o;

        return models != null ? models.equals(that.models) : that.models == null;
    }

    @Override
    public int hashCode() {
        return models != null ? models.hashCode() : 0;
    }
}