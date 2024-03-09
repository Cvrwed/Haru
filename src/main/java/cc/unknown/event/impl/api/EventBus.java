package cc.unknown.event.impl.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.Event;
import cc.unknown.event.impl.EventLink;

public class EventBus {

	private CopyOnWriteArrayList<Object> r = new CopyOnWriteArrayList<>();
	
	public void register(Object o) {
		if(r.contains(o)) return;
		r.add(o);
	}
	
	public void unregister(Object o) {
		r.remove(o);
	}
	
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