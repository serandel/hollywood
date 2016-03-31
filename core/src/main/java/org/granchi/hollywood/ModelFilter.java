package org.granchi.hollywood;

import rx.Observable;

/**
 * Transformer to use in Model Observable, so an Actor can receive only a class of Model he's interested in.
 * <p/>
 * It is used as:
 * <pre>models.compose(modelOfType(SpecificModel.class)).subscribe(...)</pre>
 */
public class ModelFilter {
    public static <M extends Model<D>, D extends ActorMetadata> Observable.Transformer<? super Model<D>, M> modelOfType(
            Class<M> modelClass) {
        return observable -> observable
                .ofType(modelClass)
                .mergeWith(observable
                                   .ofType(CompositeModel.class)
                                   .flatMap(composite -> Observable.from(composite.getModels())
                                                                   .ofType(modelClass)));
    }
}
