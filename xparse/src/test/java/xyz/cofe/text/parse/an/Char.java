package xyz.cofe.text.parse.an;

import xyz.cofe.text.parse.CharPredicates;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Char {
    CharPredicates cls() default CharPredicates.UNDEFINED;
}
