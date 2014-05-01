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
