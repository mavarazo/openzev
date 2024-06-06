package com.mav.openzev.helper;

import com.mav.openzev.api.model.InvoiceDirection;
import com.mav.openzev.api.model.InvoiceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

public class RequiredArgumentsProvider
    implements ArgumentsProvider, AnnotationConsumer<RequiredSource> {

  private static final Map<Class<?>, Object> RANDOM_VALUES;

  static {
    final SecureRandom random = new SecureRandom();

    final Map<Class<?>, Object> values = new HashMap<>();
    values.put(BigDecimal.class, BigDecimal.valueOf(random.nextDouble()));
    values.put(Boolean.class, Boolean.TRUE);
    values.put(Float.class, random.nextFloat());
    values.put(InvoiceDirection.class, InvoiceDirection.OUTGOING);
    values.put(InvoiceStatus.class, InvoiceStatus.DRAFT);
    values.put(LocalDate.class, LocalDate.now());
    values.put(String.class, UUID.randomUUID().toString());
    values.put(UUID.class, UUID.randomUUID());
    RANDOM_VALUES = values;
  }

  private RequiredSource requiredSource;

  @Override
  public void accept(final RequiredSource requiredSource) {
    this.requiredSource = requiredSource;
  }

  @Override
  public Stream<? extends Arguments> provideArguments(final ExtensionContext context)
      throws Exception {
    final List<Arguments> arguments = new ArrayList<>();

    final List<String> requiredProperties = getRequiredProperties();

    for (int i = 0; i < requiredProperties.size(); i++) {
      final Object o = requiredSource.value().getConstructor().newInstance();
      for (int j = 0; j < requiredProperties.size(); j++) {
        final PropertyDescriptor propertyDescriptor =
            new PropertyDescriptor(requiredProperties.get(j), requiredSource.value());

        final Optional<Class<?>> optionalParameter =
            Arrays.stream(propertyDescriptor.getWriteMethod().getParameterTypes()).findFirst();

        if (i == j) {
          propertyDescriptor.getWriteMethod().invoke(o, new Object[] {null});
        } else {
          if (optionalParameter.isPresent()) {
            if (!RANDOM_VALUES.containsKey(optionalParameter.get())) {
              throw new IllegalArgumentException(
                  "missing random value for class '%s'"
                      .formatted(optionalParameter.get().getSimpleName()));
            }
            final Object randomValue = RANDOM_VALUES.get(optionalParameter.get());
            propertyDescriptor.getWriteMethod().invoke(o, randomValue);
          }
        }
      }
      arguments.add(Arguments.of(o));
    }
    return arguments.stream();
  }

  private List<String> getRequiredProperties() {
    return Arrays.stream(requiredSource.value().getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(Schema.class))
        .map(method -> method.getAnnotation(Schema.class))
        .filter(schema -> schema.requiredMode() == Schema.RequiredMode.REQUIRED)
        .map(Schema::name)
        .toList();
  }
}
