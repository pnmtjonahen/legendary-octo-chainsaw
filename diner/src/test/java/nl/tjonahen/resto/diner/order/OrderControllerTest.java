package nl.tjonahen.resto.diner.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tjonahen.resto.diner.order.model.Order;
import nl.tjonahen.resto.diner.order.model.OrderItem;
import nl.tjonahen.resto.diner.order.model.OrderItemType;
import nl.tjonahen.resto.diner.order.model.OrderStatus;
import nl.tjonahen.resto.diner.order.service.OrderService;
import nl.tjonahen.resto.diner.order.status.OrderStatusBroker;
import nl.tjonahen.resto.diner.persistence.OrderRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private OrderStatusBroker orderStatusBroker;
    @InjectMocks
    private OrderController sut;
    
    @Captor
    private ArgumentCaptor<Order> orderCaptor;
    
    @Test
    public void testGetOrders() {
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());
        final List<Order> result = sut.getOrders();
        assertNotNull(result);
    }

    @Test
    void testGetBill() {
        when(orderRepository.getOrderById(1L)).thenReturn(Optional.of(newOrder()));
        final ResponseEntity<Bill> result = sut.getBill(1L);
        assertNotNull(result);
    }

    @Test
    void testGetBill_orderNotFound() {
        when(orderRepository.getOrderById(1L)).thenReturn(Optional.empty());
        final ResponseEntity<Bill> result = sut.getBill(1L);
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    private Order newOrder() {
        final Order order = Order.builder().orderItems(new ArrayList<>()).status(OrderStatus.PREPARING).id(99L).build();
        order.getOrderItems().add(OrderItem.builder().quantity(1L).orderItemType(OrderItemType.DRINK).build());
        order.getOrderItems().add(OrderItem.builder().quantity(1L).orderItemType(OrderItemType.DISH).build());
        return order;
    }


    @Test
    void testServeDrinks() throws IOException {
        when(orderRepository.getOrderById(1L)).thenReturn(Optional.of(newOrder()));
        sut.serveDrinks(1L);
        verify(orderStatusBroker).sendStatusUpdate(1L, "DRINK_SERVED");
        verify(orderRepository).save(orderCaptor.capture());
        
        assertEquals(OrderStatus.DRINK_SERVED, orderCaptor.getValue().getStatus());
    }

    @Test
    void testServeDrinks_order_not_found() {
        when(orderRepository.getOrderById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundErrorException.class, () -> sut.serveDrinks(1L));
    }

    @Test
    void testServeDrinks_error_sending_status_update() throws IOException {
        when(orderRepository.getOrderById(1L)).thenReturn(Optional.of(newOrder()));
        doThrow(new IOException()).when(orderStatusBroker).sendStatusUpdate(1L, "DRINK_SERVED");
        
        assertThrows(HttpServerErrorException.class, () -> sut.serveDrinks(1L));
        
        verify(orderRepository, never()).save(any());
    }
    
    @Test
    void testServeDishes() throws IOException {
        when(orderRepository.getOrderById(1L)).thenReturn(Optional.of(newOrder()));
        sut.serveDishes(1L);
        verify(orderStatusBroker).sendStatusUpdate(1L, "FOOD_SERVED");
        verify(orderRepository).save(orderCaptor.capture());
        
        assertEquals(OrderStatus.FOOD_SERVED, orderCaptor.getValue().getStatus());

    }

    @Test
    void testServeDishes_order_not_found() {
        when(orderRepository.getOrderById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundErrorException.class, () -> sut.serveDishes(1L));
    }

    @Test
    void testServeDishes_error_sending_status_update() throws IOException {
        when(orderRepository.getOrderById(1L)).thenReturn(Optional.of(newOrder()));
        doThrow(new IOException()).when(orderStatusBroker).sendStatusUpdate(1L, "FOOD_SERVED");
        
        assertThrows(HttpServerErrorException.class, () -> sut.serveDishes(1L));
        
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testPay() {
        sut.pay(1L);
    }

    @Test
    void testPlaceOrder() {
        final UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        final List<RequestedItem> orderItems = new ArrayList<>();
        when(orderRepository.save(any())).thenReturn(newOrder());
        
        ResponseEntity<ResponseOrder> result = sut.placeOrder(orderItems, builder);
        
        verify(orderService).processDishes(eq(99L), any());
        verify(orderService).processDrinks(eq(99L), any());
        
        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());

        assertNotNull(result.getBody());
        assertEquals(99L, result.getBody().getRef());
    }
    
}
