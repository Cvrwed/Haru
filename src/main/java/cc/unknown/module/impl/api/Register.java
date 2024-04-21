package cc.unknown.module.impl.api;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.lwjgl.input.Keyboard;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Register {

	String name();
	
	Category category();
	
	int key() default Keyboard.KEY_NONE;
	
	boolean enable() default false;
	
}
