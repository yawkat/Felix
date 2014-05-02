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
