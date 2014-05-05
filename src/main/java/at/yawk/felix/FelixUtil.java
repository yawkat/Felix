package at.yawk.felix;

import java.util.stream.Stream;
import javax.annotation.Nullable;

/**
 * @author Jonas Konrad (yawkat)
 * @deprecated Internal use only.
 */
@Deprecated
public class FelixUtil {
    /**
     * Return all superclasses and interfaces of the given type, including the type itself.
     */
    public static Stream<Class<?>> getSuperClasses(@Nullable Class<?> of) {
        // Classes like Object might be in there multiple times so we filter them
        return getSuperClasses0(of).distinct();
    }

    /**
     * Return all superclasses and interfaces of the given type, including the type itself. May contain duplicates.
     */
    @SuppressWarnings("deprecation")
    private static Stream<Class<?>> getSuperClasses0(@Nullable Class<?> of) {
        if (of == null) { return Stream.empty(); }
        // self
        Stream<Class<?>> self = Stream.of(of);
        // recursively return the tree of parent classes
        Stream<Class<?>> sup = Stream.concat(Stream.of(of.getSuperclass()), Stream.of(of.getInterfaces()))
                                     .flatMap(FelixUtil::getSuperClasses0);
        return Stream.concat(self, sup);
    }
}
