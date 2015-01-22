/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.event;

/**
 * @author Jonas Konrad (yawkat)
 */
public interface SubscribeHandle {
    public static SubscribeHandle join(Iterable<SubscribeHandle> handles) {
        return () -> handles.forEach(SubscribeHandle::unsubscribe);
    }

    void unsubscribe();
}
