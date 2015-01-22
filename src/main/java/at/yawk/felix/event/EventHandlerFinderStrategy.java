/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.event;

import java.util.stream.Stream;
import lombok.NonNull;

/**
 * Strategy to find EventHandlers in an object, for example using annotations on methods.
 *
 * @author Jonas Konrad (yawkat)
 */
public interface EventHandlerFinderStrategy {
    @NonNull
    Stream<EventHandler<?>> findEventHandlers(@NonNull Object in);
}
