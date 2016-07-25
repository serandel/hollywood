package org.granchi.hollywood;

import rx.Observable;

/**
 * Transformer to use in Model Observable, so an Actor can receive only a class of Model he's
 * interested in.
 * <p/>
 * It is used as:
 * <pre>models.compose(modelOfType(SpecificModel.class)).subscribe(...)</pre>
 */
// TODO what if it's buried within TWO composite models?
// TODO wouldn't it be easier if if would get built as an getSubModelOfType(class) in Model
// class?
@Deprecated
public class ModelFilter {
    public static <M extends Model> Observable.Transformer<? super Model, M> modelOfType(Class<M>
                                                                                                 modelClass) {
        return observable -> observable
                .ofType(modelClass)
                .mergeWith(observable
                                   .ofType(CompositeModel.class)
                                   .flatMap(composite -> Observable.from(composite.getModels())
                                                                   .ofType(modelClass)));
    }
}