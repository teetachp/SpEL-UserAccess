package com.eazybytes.accounts.customer;

import com.eazybytes.accounts.config.AccessConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    private CustomerRepository repository;
    private CustomerService service;

    @BeforeEach
    public void setUp() {
        repository = mock(CustomerRepository.class);
        AccessConfig accessConfig = mock(AccessConfig.class);
        service = new CustomerService(repository, accessConfig);
    }

    @Test
    public void testGetAllCustomers() {
        Customer alice = new Customer();
        alice.setId(1L);
        alice.setName("Alice");

        Customer bob = new Customer();
        bob.setId(2L);
        bob.setName("Bob");

        when(repository.findAll()).thenReturn(Arrays.asList(alice, bob));

        List<Customer> result = service.getAllCustomers();
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
        assertEquals("Bob", result.get(1).getName());
    }

    @Test
    public void testAddCustomer() {
        Customer newCustomer = new Customer();
        newCustomer.setName("Charlie");

        when(repository.save(any(Customer.class))).thenReturn(newCustomer);

        Customer result = service.addCustomer(newCustomer);
        assertEquals("Charlie", result.getName());
        verify(repository, times(1)).save(newCustomer);
    }
}