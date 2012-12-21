package org.tcrun.api.annotations;

import java.lang.annotation.*;

/**
 * Apply a test case name via annotation.
 *
 * @author jcorbett
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface TestName
{
	public String value();
}
