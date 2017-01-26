package ru.rus1f1kat0r.autoenum.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface AutoEnum {
    String[] value();
    String[] declaredNames() default {};
    String name() default "";
}
