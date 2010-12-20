package org.tcrun.api;
import java.lang.annotation.*;

/**
 *
 * @author jcorbett
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ImplementsPlugin
{
	Class<? extends Plugin>[] value();
}
