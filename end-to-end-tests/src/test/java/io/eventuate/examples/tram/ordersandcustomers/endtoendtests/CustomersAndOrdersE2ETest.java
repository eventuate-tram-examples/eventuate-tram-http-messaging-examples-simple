package io.eventuate.examples.tram.ordersandcustomers.endtoendtests;

import io.eventuate.examples.tram.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.ordersandcustomers.commondomain.OrderState;
import io.eventuate.examples.tram.ordersandcustomers.apiweb.CreateCustomerRequest;
import io.eventuate.examples.tram.ordersandcustomers.apiweb.CreateCustomerResponse;
import io.eventuate.examples.tram.ordersandcustomers.apiweb.CreateOrderRequest;
import io.eventuate.examples.tram.ordersandcustomers.apiweb.CreateOrderResponse;
import io.eventuate.examples.tram.ordersandcustomers.apiweb.GetOrderResponse;
import io.eventuate.util.test.async.Eventually;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CustomersAndOrdersE2ETestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomersAndOrdersE2ETest{

  @Value("${DOCKER_HOST_IP:localhost}")
  private String hostName;

  private String baseUrl(String path) {
    return "http://"+hostName+":8080/" + path;
  }

  @Autowired
  RestTemplate restTemplate;

  @Test
  public void shouldApprove() {
    Long customerId = createCustomer("Fred", new Money("15.00"));
    Long orderId = createOrder(customerId, new Money("12.34"));
    assertOrderState(orderId, OrderState.APPROVED);
  }

  @Test
  public void shouldReject() {
    Long customerId = createCustomer("Fred", new Money("15.00"));
    Long orderId = createOrder(customerId, new Money("123.34"));
    assertOrderState(orderId, OrderState.REJECTED);
  }

  @Test
  public void shouldRejectForNonExistentCustomerId() {
    Long customerId = System.nanoTime();
    Long orderId = createOrder(customerId, new Money("123.34"));
    assertOrderState(orderId, OrderState.REJECTED);
  }

  private Long createCustomer(String name, Money credit) {
    return restTemplate.postForObject(baseUrl("customers"),
            new CreateCustomerRequest(name, credit), CreateCustomerResponse.class).getCustomerId();
  }

  private Long createOrder(Long customerId, Money orderTotal) {
    return restTemplate.postForObject(baseUrl("orders"),
            new CreateOrderRequest(customerId, orderTotal), CreateOrderResponse.class).getOrderId();
  }

  private void assertOrderState(Long id, OrderState expectedState) {
    Eventually.eventually(100, 400, TimeUnit.MILLISECONDS, () -> {
      ResponseEntity<GetOrderResponse> response =
              restTemplate.getForEntity(baseUrl("orders/" + id), GetOrderResponse.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());

      GetOrderResponse order = response.getBody();

      assertEquals(expectedState, order.getOrderState());
    });
  }
}
