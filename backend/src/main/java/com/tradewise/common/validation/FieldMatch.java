package com.tradewise.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Class-level constraint asserting that two properties hold equal values
 * (e.g. {@code password} and {@code confirmPassword}).
 */
@Documented
@Constraint(validatedBy = FieldMatchValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Repeatable(FieldMatch.List.class)
public @interface FieldMatch {

    String message() default "Fields do not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** Name of the first property. */
    String first();

    /** Name of the second property that must equal {@link #first()}. */
    String second();

    @Documented
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @interface List {
        FieldMatch[] value();
    }
}
