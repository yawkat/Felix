/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.event;

import lombok.NonNull;
import lombok.Value;

/**
 * @author Jonas Konrad (yawkat)
 */
@Value
public final class EventHandler<Event> implements Comparable<EventHandler<?>> {
    public static final int DEFAULT_PRIORITY = 0;

    private final Class<Event> type;
    private final ThrowingConsumer<? super Event> handler;
    private final int priority;

    @Override
    public int compareTo(@NonNull EventHandler<?> o) {
        return o.priority - this.priority;
    }

    public static interface ThrowingConsumer<T> {
        void consume(T value) throws Throwable;
    }
}
