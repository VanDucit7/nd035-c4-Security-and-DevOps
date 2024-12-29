package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItems_happy_path() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item2");

        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals("Item1", items.get(0).getName());
        assertEquals("Item2", items.get(1).getName());
    }

    @Test
    public void getItemById_happy_path() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item returnedItem = response.getBody();
        assertNotNull(returnedItem);
        assertEquals("Item1", returnedItem.getName());
    }

    @Test
    public void getItemById_not_found() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemsByName_happy_path() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");

        when(itemRepository.findByName("Item1")).thenReturn(Arrays.asList(item));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item1");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Item1", items.get(0).getName());
    }

    @Test
    public void getItemsByName_not_found() {
        when(itemRepository.findByName("NonExistingItem")).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("NonExistingItem");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
