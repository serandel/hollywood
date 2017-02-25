package org.granchi.hollywood

/**
 * Actions are emitted by actors, as the only way to affect the model.
 *
 * All actions are consumed by the model in a single thread, so they are inherently enqueued.
 */
interface Action
