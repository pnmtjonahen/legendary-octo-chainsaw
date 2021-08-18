package nl.tjonahen.resto.diner.order;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.diner.order.model.Order;
import nl.tjonahen.resto.diner.order.model.OrderItem;
import nl.tjonahen.resto.diner.order.model.OrderItemType;
import nl.tjonahen.resto.diner.order.model.OrderStatus;
import nl.tjonahen.resto.diner.order.service.OrderService;
import nl.tjonahen.resto.diner.order.status.OrderNotFoundException;
import nl.tjonahen.resto.diner.order.status.OrderStatusBroker;
import nl.tjonahen.resto.diner.persistence.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final OrderStatusBroker orderStatusBroker;

    public OrderController(final OrderRepository orderRepository, final OrderService orderService, final OrderStatusBroker orderStatusBroker) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.orderStatusBroker = orderStatusBroker;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @CrossOrigin
    @GetMapping("/{id}/bill")
    public ResponseEntity<Bill> getBill(@PathVariable Long id) {
        final var order = orderRepository.getOrderById(id);
        if (order.isPresent()) {
            final var billItems = order.get().getOrderItems()
                    .stream()
                    .map(item -> BillItem.builder().name(orderService.getName(item)).quantity(item.getQuantity()).price(orderService.getPrice(item)).build())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Bill.builder().items(billItems).total(billItems.stream()
                    .map(BillItem::getTotal)
                    .reduce(0L, (a, b) -> a + b))
                    .build());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/serve/drinks")
    public void serveDrinks(@PathVariable Long id) {
        orderRepository.getOrderById(id).ifPresentOrElse(order -> {
            try {
                log.info("Serving drinks for order {}", id);
                orderStatusBroker.sendStatusUpdate(id, order.serveDrinks().name());
                order.setStatus(OrderStatus.DRINK_SERVED);
                orderRepository.save(order);
            } catch (OrderNotFoundException ex) {
                log.error("Table for Order {} not found, drinks cannot be served", id);
                order.setStatus(OrderStatus.NO_CUSTOMER);
            } catch (IOException ex) {
                log.error("Error while updateing orderstatus {}", ex.getMessage());
            } finally {
                orderRepository.save(order);
            }
        }, () -> {
            throw new OrderNotFoundErrorException();
        });
    }

    @PostMapping("/{id}/serve/dishes")
    public void serveDishes(@PathVariable Long id) {
        orderRepository.getOrderById(id).ifPresentOrElse(order -> {
            try {
                log.info("Serving food for order {}", id);
                orderStatusBroker.sendStatusUpdate(id, order.serveFood().name());
                order.setStatus(OrderStatus.FOOD_SERVED);
            } catch (OrderNotFoundException ex) {
                log.error("Table for Order {} not found, food cannot be served", id);
                order.setStatus(OrderStatus.NO_CUSTOMER);
            } catch (IOException ex) {
                log.error("Error while updateing orderstatus {}", ex.getMessage());
            } finally {
                orderRepository.save(order);
            }
        }, () -> {
            throw new OrderNotFoundErrorException();
        });
    }

    @PostMapping("/{orderid}/serve/{dishid}")
    public void serveDish(@PathVariable Long orderid, @PathVariable Long dishid) throws IOException {
        orderRepository.getOrderById(orderid).ifPresentOrElse(order -> {
            try {
                order.getOrderItems()
                        .stream()
                        .filter(item -> item.getOrderItemType() == OrderItemType.DISH && item.getId().equals(dishid)).forEach(item -> item.setPrepared(true));
                if (order.getOrderItems()
                        .stream()
                        .filter(item -> item.getOrderItemType() == OrderItemType.DISH && !item.isPrepared()).count() == 0) {
                    log.info("Serving food for order {}", orderid);
                    orderStatusBroker.sendStatusUpdate(orderid, order.serveFood().name());
                    order.setStatus(OrderStatus.FOOD_SERVED);
                }
            } catch (OrderNotFoundException ex) {
                log.error("Table for Order {} not found, food cannot be served", orderid);
                order.setStatus(OrderStatus.NO_CUSTOMER);
            } catch (IOException ex) {
                log.error("Error while updateing orderstatus {}", ex.getMessage());
            } finally {
                orderRepository.save(order);
            }
        }, () -> { throw new OrderNotFoundErrorException(); });
    }

    @CrossOrigin
    @PostMapping("/{id}/pay")
    public void pay(@PathVariable Long id) {
        log.info("Order {} is payed", id);
    }

    @CrossOrigin
    @PostMapping
    @Transactional
    public ResponseEntity<ResponseOrder> placeOrder(@RequestBody
            final List<RequestedItem> orderItems,
            UriComponentsBuilder builder) {

        final var order = Order.builder()
                .status(OrderStatus.INITIAL)
                .orderItems(orderItems.stream()
                        .map(item -> OrderItem.builder()
                        .ref(item.getRef())
                        .quantity(item.getQuantity())
                        .orderItemType(item.getType() == RequestedItemType.DISH ? OrderItemType.DISH : OrderItemType.DRINK)
                        .build())
                        .collect(Collectors.toList()))
                .build();
        orderRepository.save(order);

        orderService.processDishes(order.getId(), order.getOrderItems()
                .stream()
                .filter(item -> item.getOrderItemType() == OrderItemType.DISH)
                .collect(Collectors.toList()));

        orderService.processDrinks(order.getId(), order.getOrderItems()
                .stream()
                .filter(item -> item.getOrderItemType() == OrderItemType.DRINK)
                .collect(Collectors.toList()));

        log.info("Accepted order {}", order.getId());
        final var uriComponents = builder.path("/api/order/{id}/bill").buildAndExpand(order.getId());
        return new ResponseEntity<>(ResponseOrder.builder().ref(order.getId()).billUrl(uriComponents.toUri().toString()).build(), HttpStatus.ACCEPTED);
    }
}
