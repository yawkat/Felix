/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.event;

import java.lang.reflect.Method;

/**
 * EventHandler strategy that looks for the guava Subscribe annotation in case someone used those accidently or they
 * want to use them for abstraction.
 *
 * @author Jonas Konrad (yawkat)
 */
class GuavaSubscribeFinderStrategy extends AnnotationEventHandlerFinderStrategy<com.google.common.eventbus.Subscribe> {
    static final EventHandlerFinderStrategy instance = new GuavaSubscribeFinderStrategy();

    GuavaSubscribeFinderStrategy() {
        super(com.google.common.eventbus.Subscribe.class);
    }

    @Override
    protected EventHandler<?> makeHandler(com.google.common.eventbus.Subscribe annotation,
                                          Object object,
                                          Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        assert parameterTypes.length == 1 : method;
        EventHandler.ThrowingConsumer<Object> consumer;
        consumer = event -> {
            synchronized (method) {
                method.invoke(object, event);
            }
        };
        return new EventHandler<>(parameterTypes[0], consumer, EventHandler.DEFAULT_PRIORITY);
    }
}
