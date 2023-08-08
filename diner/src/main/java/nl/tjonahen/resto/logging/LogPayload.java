package nl.tjonahen.resto.logging;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class LogPayload {

  @Around("@annotation(Logged)")
  public Object logAction(ProceedingJoinPoint joinPoint) throws Throwable {

    final var result = joinPoint.proceed();
    final var sb = new StringBuilder();

    log.info(
        sb.append("Action:")
            .append(joinPoint.getSignature().getName())
            .append("{")
            .append(
                Stream.concat(
                        Arrays.stream(joinPoint.getArgs()).flatMap(this::logActionFields),
                        logActionFields(result))
                    .collect(Collectors.joining(", ")))
            .append("}")
            .toString());

    return result;
  }

  private Stream<String> logActionFields(final Object target) {
    return Arrays.stream(target.getClass().getDeclaredFields())
        .filter(f -> f.isAnnotationPresent(LogField.class))
        .map(f -> logField(f, target));
  }

  @SuppressWarnings("java:S3011") // setAccessible needed to log provate attributes
  private String logField(Field f, final Object target) {

    f.setAccessible(true);
    try {
      return new StringBuilder()
          .append(f.getName())
          .append(":")
          .append(f.get(target).toString())
          .toString();
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      return "";
    }
  }
}
