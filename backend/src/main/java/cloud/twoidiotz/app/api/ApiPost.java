package cloud.twoidiotz.app.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(
    method = RequestMethod.POST,
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.CREATED)
@Operation
@ApiResponse(responseCode = "201", description = "Created")
public @interface ApiPost {
  @AliasFor(annotation = RequestMapping.class, attribute = "path")
  String[] value() default {};

  @AliasFor(annotation = RequestMapping.class, attribute = "path")
  String[] path() default {};

  @AliasFor(annotation = Operation.class, attribute = "operationId")
  String operationId() default "";

  @AliasFor(annotation = Operation.class, attribute = "summary")
  String summary() default "";

  @AliasFor(annotation = Operation.class, attribute = "description")
  String description() default "";

  @AliasFor(annotation = ApiResponse.class, attribute = "description")
  String responseDescription() default "Created";
}
