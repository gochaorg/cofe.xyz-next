package xyz.cofe.text.parse.an;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Join {
    JoinOp value() default JoinOp.OR;
}
