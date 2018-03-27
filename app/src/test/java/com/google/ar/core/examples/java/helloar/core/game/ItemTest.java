package com.google.ar.core.examples.java.helloar.core.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class ItemTest {
    private Item item;
    private Gson gson;

    @Before
    public void setUp() {
        item = new Item(1, "name", "descr", "model", "texture");
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    @Test
    public void testSerialization() {
        String serItem = gson.toJson(item);
        Item another = gson.fromJson(serItem, Item.class);
        assertEquals(item.getDescription(), another.getDescription());
    }
}