package org.granchi.hollywood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * A Model that only contains several subModels.
 * <p>
 * It serves as a way to decompose business logic in several components.
 *
 * @author serandel
 */
public class CompositeModel extends Model {
    private List<Model> models = new ArrayList<>();

    /**
     * Constructor.
     * <p>
     * Initializes the Model with at least one subModel.
     * <p>
     * The order can be important, because actions will be applied to every subModel following the
     * same order.
     *
     * @param initialModels initial models
     */
    public CompositeModel(Model... initialModels) {
        if (initialModels == null) {
            throw new NullPointerException();
        }
        if (initialModels.length == 0) {
            throw new IllegalArgumentException();
        }

        for (Model model : initialModels) {
            if (model == null) {
                throw new NullPointerException();
            } else if (!models.contains(model)) {
                models.add(model);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Model actUpon(Action action) {
        // It's easier for the submodels if they don't have to worry about duplicates
        LinkedHashSet<Model> resultModels = new LinkedHashSet<>();

        for (Model model : models) {
            Model resultModel = model.actUpon(action);

            if (resultModel != null) {
                if (resultModel instanceof CompositeModel) {
                    ((CompositeModel) resultModel).getModels()
                                                  .stream()
                                                  .filter(m -> !resultModels.contains(m))
                                                  .forEach(resultModels::add);
                } else if (!resultModels.contains(model)) {
                    resultModels.add(resultModel);
                }
            }
        }

        return resultModels.isEmpty() ? null :
                new CompositeModel(resultModels.toArray(new Model[resultModels.size()]));
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

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> Collection<T> getSubmodelsOfType(Class<T> type) {
        if (type.isAssignableFrom(this.getClass())) {
            return Collections.singletonList((T) this);
        } else {
            ArrayList<T> resultSubModels = new ArrayList<>();

            models.stream()
                  .flatMap(model -> model.getSubmodelsOfType(type).stream())
                  .filter(model -> !resultSubModels.contains(model))
                  .forEach(resultSubModels::add);

            return resultSubModels;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeModel)) {
            return false;
        }

        CompositeModel that = (CompositeModel) o;

        return models.equals(that.models);
    }

    @Override
    public int hashCode() {
        return models.hashCode();
    }
}