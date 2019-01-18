package com.atomikos.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

 /**
  * Annotation for marking a specific method as being a dynamic proxy implementation.
  */

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface Proxied {

}
