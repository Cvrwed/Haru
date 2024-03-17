package cc.unknown.event.impl.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.Event;
import cc.unknown.event.impl.EventLink;

/**
 * The EventBus class is responsible for managing and dispatching events to registered objects.
 */
public class EventBus {

    /**
     * A list of registered objects.
     */
    private CopyOnWriteArrayList<Object> registeredObjects = new CopyOnWriteArrayList<>();

    /**
     * Registers an object to receive events.
     *
     * @param object The object to register.
     */
    public void register(Object object) {
        if (registeredObjects.contains(object)) {
            return;
        }
        registeredObjects.add(object);
    }

    /**
     * Unregisters an object to stop receiving events.
     *
     * @param object The object to unregister.
     */
    public void unregister(Object object) {
        registeredObjects.remove(object);
    }

    /**
     * Posts an event to all registered objects that have methods annotated with @EventLink and matching parameter types.
     *
     * @param event The event to post.
     */
    public void post(Event event) {
        for (Object object : registeredObjects) {
            Class<?> clazz = object.getClass();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(EventLink.class) &&
                        method.getParameterCount() == 1 &&
                        method.getParameterTypes()[0] == event.getClass() &&
                        method.getDeclaringClass().isAssignableFrom(clazz)) {
                    try {
                        method.invoke(object, event);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}