package io.eventuate.examples.tram.ordersandcustomers.customers.web;

import io.eventuate.examples.tram.ordersandcustomers.customers.domain.Customer;
import io.eventuate.examples.tram.ordersandcustomers.customers.service.CustomerService;
import io.eventuate.examples.tram.ordersandcustomers.apiweb.CreateCustomerRequest;
import io.eventuate.examples.tram.ordersandcustomers.apiweb.CreateCustomerResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

import javax.inject.Inject;

@Controller
public class CustomerController {

  @Inject
  private CustomerService customerService;

  @Post(value = "/customers")
  public CreateCustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest) {
    Customer customer = customerService.createCustomer(createCustomerRequest.getName(), createCustomerRequest.getCreditLimit());
    return new CreateCustomerResponse(customer.getId());
  }
}
