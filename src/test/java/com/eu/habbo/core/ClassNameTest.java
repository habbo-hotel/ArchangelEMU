package com.eu.habbo.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ClassNameTest {

    @Test
    public void test() {
        assertEquals("Hello, World!", "Hello, World!");
    }
}
