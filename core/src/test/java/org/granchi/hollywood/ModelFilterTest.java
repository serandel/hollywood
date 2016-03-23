package org.granchi.hollywood;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.Arrays;
import java.util.HashSet;

@RunWith(MockitoJUnitRunner.class)
public class ModelFilterTest {
    private interface Model1 extends Model<ActorMetadata> {
    }

    private interface Model2 extends Model<ActorMetadata> {
    }

    private interface Model3 extends Model2 {
    }

    @Mock
    private Model1 model1;

    @Mock
    private Model2 model2;

    @Mock
    private Model3 model3;

    @Mock
    private Model model4;

    private TestSubscriber<Model<ActorMetadata>> testSubscriber;

    @Before
    public void setUp() {
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void testFiltersUnwantedModels() {
        Observable.just(model1, model2, model3).compose(ModelFilter.modelOfType(Model1.class))
                  .subscribe(testSubscriber);

        testSubscriber.assertValue(model1);
    }

    @Test
    public void testNoWantedModels() {
        Observable.just(model1, model2).compose(ModelFilter.modelOfType(Model3.class))
                  .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
    }

    @Test
    public void testGetsSubclasses() {
        Observable.just(model1, model2, model3).compose(ModelFilter.modelOfType(Model2.class))
                  .subscribe(testSubscriber);

        testSubscriber.assertValues(model2, model3);
    }

    @Test
    public void testExtractsFromCompositeModels() {
        Observable.just(model2, new CompositeModel<>(new HashSet<>(
                Arrays.asList(model1, model3)))).compose(ModelFilter.modelOfType(Model2.class))
                  .subscribe(testSubscriber);

        testSubscriber.assertValues(model2, model3);
    }

    @Test
    public void testCompositeModelsCanHaveMultipleWantedModels() {
        Observable.just(new CompositeModel<>(new HashSet<>(
                Arrays.asList(model2, model3)))).compose(ModelFilter.modelOfType(Model2.class))
                  .subscribe(testSubscriber);

        testSubscriber.assertValues(model2, model3);
    }
}