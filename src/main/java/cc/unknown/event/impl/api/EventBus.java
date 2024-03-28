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
	private CopyOnWriteArrayList<Object> r = new CopyOnWriteArrayList<>();

    /**
     * Registers an object to receive events.
     *
     * @param object The object to register.
     */
	public void register(Object o) {
		if(r.contains(o)) return;
		r.add(o);
	}

    /**
     * Unregisters an object to stop receiving events.
     *
     * @param object The object to unregister.
     */
	public void unregister(Object o) {
		r.remove(o);
	}

    /**
     * Posts an event to all registered objects that have methods annotated with @EventLink and matching parameter types.
     *
     * @param event The event to post.
     */
	public void post(Event e) {
		for(Object o : r) {
			Class<?> c = o.getClass();
			Method[] m = c.getDeclaredMethods();
			for(Method me : m) {
				if(me.isAnnotationPresent(EventLink.class) &&
						me.getParameterCount() == 1 &&
						me.getParameterTypes()[0] == e.getClass() &&
						me.getDeclaringClass().isAssignableFrom(c)) {
					try {
						me.invoke(o, e);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
}