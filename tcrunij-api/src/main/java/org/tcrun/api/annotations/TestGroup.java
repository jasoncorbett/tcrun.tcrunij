package org.tcrun.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A test case can be in multiple groups and so this annotation allows for one or multiple groups.
 * @author jcorbett
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestGroup
{
	public String[] value();
}
