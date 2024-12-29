package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submit_order_happy_path() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        List<Item> mockItems = Arrays.asList(new Item(), new Item());

        Cart cart = new Cart();
        cart.setItems(mockItems);
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        UserOrder order = UserOrder.createFromCart(cart);
        when(orderRepository.save(Mockito.any(UserOrder.class))).thenReturn(order);

        ResponseEntity<UserOrder> response = orderController.submit("testUser");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder returnedOrder = response.getBody();
        assertNotNull(returnedOrder);
        assertEquals(user, returnedOrder.getUser());
    }

    @Test
    public void submit_order_user_not_found() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("nonExistingUser");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void get_orders_for_user_happy_path() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        UserOrder order1 = new UserOrder();
        order1.setId(1L);
        order1.setUser(user);

        UserOrder order2 = new UserOrder();
        order2.setId(2L);
        order2.setUser(user);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order1, order2));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals(order1, orders.get(0));
        assertEquals(order2, orders.get(1));
    }

    @Test
    public void get_orders_for_user_not_found() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("nonExistingUser");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
