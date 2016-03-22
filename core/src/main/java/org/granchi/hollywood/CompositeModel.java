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
public class CompositeModel<M extends Model<M, D>, D extends ActorMetadata> implements Model<M, D> {
    private Set<Model<? super M, ? super D>> models;

    /**
     * Constructor.
     * <p/>
     * Initializes the Model with at least one subModel.
     *
     * @param initialModels initial models
     */
    public CompositeModel(Set<Model<? super M, ? super D>> initialModels) {
        if (initialModels.size() == 0) {
            throw new IllegalArgumentException();
        }

        models.addAll(initialModels);
    }

    @Override
    public M actUpon(Action action) {
        Set<Model<? super M, ? super D>> resultSubModels = new HashSet<>();

        return new CompositeModel<M, D>(resultSubModels);
    }

    @Override
    public Set<D> getActors() {
        return null;
    }
}
