package org.granchi.hollywood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

/**
 * Stores all the active Actors, creating or removing them according to the ActorMetadata that the
 * model provides.
 *
 * @param <R> type of Roster that defines all the possible types of Actors
 * @param <D> type of ActorMetadata it uses for building and identifying Actors
 */
public abstract class Crew<R extends Roster, D extends ActorMetadata<R>> {
    // It's not necessary to share because it's hot
    private final Observable<Model<D>> models;

    private final PublishSubject<Action> actions;
    private final Map<Actor<D>, Subscription> actionsSubscriptions;

    /**
     * Constructor.
     * <p>
     * Warning: the Observable for Models should be hot, because it will be subscribed by every
     * possible future Actor
     *
     * @param models Observable for Models
     */
    protected Crew(Observable<Model<D>> models) {
        if (models == null) {
            throw new NullPointerException();
        }

        this.models = models;

        actions = PublishSubject.create();
        actionsSubscriptions = new HashMap<>();
    }

    /**
     * Build a new Actor.
     *
     * @param metadata metadata for creating the actor
     */
    protected abstract Actor<D> buildActorFrom(D metadata);

    /**
     * Check if there is already an Actor built from an specific ActorMetadata.
     *
     * @param metadata metadata
     * @return if there is an Actor from this metadata
     */
    protected abstract boolean containsActorFrom(D metadata);

    /**
     * Ensures the cast contains every requested Actor and no more.
     * <p>
     * If an Actor has to be created, it is subscribed to the Model Observable.
     *
     * @param metadatas metadata for all the desired Actors
     */
    void ensureCrew(Collection<D> metadatas) {
        for (D metadata : metadatas) {
            if (!containsActorFrom(metadata)) {
                Actor<D> actor = buildActorFrom(metadata);

                actor.subscribeTo(models);

                // Can't subscribe directly the subject, or it will complete itself with the
                // first onCompleted
                Subscription subscription = actor.getActions().subscribe(actions::onNext,
                                                                         actions::onError);
                actionsSubscriptions.put(actor, subscription);
            }
        }

        // Remove unwanted ones
        Collection<Actor<D>> actors = getActors();
        // To avoid ConcurrentModificationException
        List<Actor<D>> unwanteds = new ArrayList<>();
        if (actors.size() != metadatas.size()) {
            for (Actor<D> actor : actors) {
                boolean wanted = false;

                for (D metadata : metadatas) {
                    if (isActorFrom(actor, metadata)) {
                        wanted = true;
                        break;
                    }
                }

                if (!wanted) {
                    unwanteds.add(actor);
                }
            }

            for (Actor<D> actor : unwanteds) {
                actionsSubscriptions.get(actor).unsubscribe();
                actionsSubscriptions.remove(actor);

                remove(actor);
            }
        }

        // TODO take into account actors that are created or destroyed by the system, not Crew
    }

    /**
     * Returns the collection of created Actors.
     *
     * @return Actors
     */
    protected abstract Collection<Actor<D>> getActors();

    /**
     * Says if an Actor has been created from a specific Metadata.
     *
     * @param actor    Actor
     * @param metadata Metadata
     * @return if the Actor was created from the Metadata
     */
    protected abstract boolean isActorFrom(Actor<D> actor, D metadata);

    /**
     * Removes an Actor from the Crew.
     *
     * @param actor Actor
     */
    protected abstract void remove(Actor<D> actor);

    /**
     * Gives an Observable with Actions from any active Actor in the Crew.
     *
     * @return Observable for Actions
     */
    Observable<Action> getActions() {
        return actions;
    }

    /**
     * Factory for creating Casts.
     * <p>
     * It is needed, because we want the application to create the Models observable to give to the
     * Crew in its constructor.
     *
     * @param <R> type of Roster that defines all the possible Actors
     * @param <D> type of ActorMetadata it uses for building and identifying Actors
     */
    @FunctionalInterface
    public interface Factory<R extends Roster, D extends ActorMetadata<R>> {
        /**
         * Create a Crew, subscribed to an Observable of Models.
         *
         * @param models Observable of Models
         * @return Crew
         */
        Crew<R, D> build(Observable<Model<D>> models);
    }
}