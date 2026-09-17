package cloud.twoidiotz.app.api;

import io.swagger.v3.oas.annotations.Operation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
@Operation
public @interface ApiGet {
  @AliasFor(annotation = RequestMapping.class, attribute = "path")
  String[] path() default {};

  @AliasFor(annotation = Operation.class, attribute = "operationId")
  String operationId();
}
