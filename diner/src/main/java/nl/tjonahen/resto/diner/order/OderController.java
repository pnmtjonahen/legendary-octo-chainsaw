package nl.tjonahen.resto.diner.order;

import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.PathParam;
import nl.tjonahen.resto.diner.order.model.Order;
import nl.tjonahen.resto.diner.order.model.OrderItem;
import nl.tjonahen.resto.diner.order.model.OrderItemType;
import nl.tjonahen.resto.diner.order.model.OrderStatus;
import nl.tjonahen.resto.diner.order.persistence.OrderRepository;
import nl.tjonahen.resto.diner.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("api/order")
public class OderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OderController(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}/bill")
    public Bill getAllOrders(@PathVariable Long id) {
        Order order = orderRepository.getOne(id);
        List<BillItem> billItems = order.getOrderItems()
                .stream()
                .map(item -> BillItem.builder().name(orderService.getName(item)).quantity(item.getQuantity()).price(orderService.getPrice(item)).build())
                .collect(Collectors.toList());
        return Bill.builder().items(billItems).total(billItems.stream()
                .map(item -> item.getTotal())
                .reduce(0L, (a, b) -> a + b))
                .build();

    }

    @PostMapping
    public ResponseEntity placeOrder(@RequestBody final List<Item> orderItems,
            UriComponentsBuilder builder) {

        final Order order = Order.builder()
                .status(OrderStatus.INITIAL)
                .orderItems(orderItems.stream()
                        .map(item -> OrderItem.builder()
                        .ref(item.getRef())
                        .quantity(item.getQuantity())
                        .orderItemType(item.getType() == ItemType.DISH ? OrderItemType.DISH : OrderItemType.DRINK)
                        .build())
                        .collect(Collectors.toList()))
                .build();
        orderRepository.save(order);

        orderService.processDishes(order.getOrderItems()
                .stream()
                .filter(item -> item.getOrderItemType() == OrderItemType.DISH)
                .collect(Collectors.toList()));
        orderService.processDishes(order.getOrderItems()
                .stream()
                .filter(item -> item.getOrderItemType() == OrderItemType.DRINK)
                .collect(Collectors.toList()));

        UriComponents uriComponents = builder.path("/api/order/{id}").buildAndExpand(order.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }
}
