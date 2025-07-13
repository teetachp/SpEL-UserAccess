package com.eazybytes.accounts.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> getAll() {
        return service.getAllCustomers();
    }

    @PostMapping
    public Customer add(@RequestBody Customer customer) {
        return service.addCustomer(customer);
    }

    @GetMapping("/check-access")
    public ResponseEntity<Map<String, Object>> checkAccess(@RequestParam String persona,
                                              @RequestParam String function,
                                              @RequestParam String targetPersona) {
        Optional<String> actionOpt = service.getSpecialAccessAction(persona, function, targetPersona);
        Map<String, Object> response = new HashMap<>();
        response.put("persona", persona);
        response.put("function", function);
        response.put("targetPersona", targetPersona);

        if (actionOpt.isPresent()) {
            response.put("access", true);
            response.put("action", actionOpt.get());
        } else {
            response.put("access", false);
        }

        return ResponseEntity.ok(response);
    }
}