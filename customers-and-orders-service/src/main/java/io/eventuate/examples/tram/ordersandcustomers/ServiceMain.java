package io.eventuate.examples.tram.ordersandcustomers;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@OpenAPIDefinition
public class ServiceMain {
  public static void main(String[] args) {
    Micronaut.run(ServiceMain.class);
  }
}
