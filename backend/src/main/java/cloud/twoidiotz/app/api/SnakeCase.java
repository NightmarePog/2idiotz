package cloud.twoidiotz.app.api;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Applies snake_case to JSON and OpenAPI for the annotated DTO. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@tools.jackson.databind.annotation.JsonNaming(
    tools.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
@com.fasterxml.jackson.databind.annotation.JsonNaming(
    com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
public @interface SnakeCase {}
