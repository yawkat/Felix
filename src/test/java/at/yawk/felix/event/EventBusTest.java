package at.yawk.felix.event;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class EventBusTest {
    @Test
    public void testRegister() {
        EventBus bus = EventBus.create();
        AtomicInteger callCount = new AtomicInteger(0);
        bus.post(new Object());
        assertEquals(0, callCount.get());
        bus.subscribe(Object.class, event -> callCount.incrementAndGet(), 0);
        bus.post(new Object());
        assertEquals(1, callCount.get());
    }

    @Test
    public void testPriority() {
        EventBus bus = EventBus.create();
        AtomicInteger calledPriority = new AtomicInteger();
        AtomicBoolean failed = new AtomicBoolean(false);
        for (int i = 1; i < 5; i++) {
            int priority = i;
            bus.subscribe(Object.class, event -> {
                failed.compareAndSet(false, calledPriority.getAndSet(priority) < priority);
            }, priority);
        }
        assertFalse(failed.get());
    }

    @Test
    public void testSubscribe() {
        EventBus bus = EventBus.create();
        AtomicBoolean called = new AtomicBoolean(false);
        bus.subscribe(new Object() {
            @Subscribe
            public void onEvent(Object event) {
                called.set(true);
            }
        });
        bus.post(new Object());
        assertTrue(called.get());
    }

    @Test
    public void testUnsubscribe() {
        EventBus bus = EventBus.create();
        AtomicBoolean called = new AtomicBoolean(false);
        bus.subscribe(new Object() {
            @Subscribe
            public void onEvent(Object event) {
                called.set(false);
            }
        }).unsubscribe();
        bus.post(new Object());
        assertFalse(called.get());
    }

    @Test
    public void testSubscribeGuava() {
        EventBus bus = EventBus.create();
        AtomicBoolean called = new AtomicBoolean(false);
        bus.subscribe(new Object() {
            @com.google.common.eventbus.Subscribe
            public void onEvent(Object event) {
                called.set(true);
            }
        });
        bus.post(new Object());
        assertTrue(called.get());
    }

    @Test
    public void testInheritance1() {
        testInheritance(new Handler(), true);
    }

    @Test
    public void testInheritance2() {
        testInheritance(new Handler() {}, true);
    }

    @Test
    public void testInheritance3() {
        testInheritance(new Handler() {
            @Override
            public void onEvent(Object event) {
                super.onEvent(event);
            }
        }, true);
    }

    @Test
    public void testInheritance4() {
        testInheritance(new Handler() {
            @Override
            public void onEvent(Object event) {}
        }, false);
    }

    @Test
    public void testInheritance5() {
        testInheritance(new Handler() {
            @Override
            @Subscribe
            public void onEvent(Object event) {}
        }, false);
    }

    private void testInheritance(Handler handler, boolean result) {
        EventBus bus = EventBus.create();
        bus.subscribe(handler);
        bus.post(new Object());
        assertEquals(result, handler.called);
    }

    private class Handler {
        boolean called = false;

        @Subscribe
        public void onEvent(Object event) {
            called = true;
        }
    }
}
