package com.mk4pustin.tacocloud.controllers;

import com.mk4pustin.tacocloud.data.order.TacoOrder;
import com.mk4pustin.tacocloud.repository.tacoorder.OrderRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
public class OrderController {
    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/current")
    public String orderForm() {
        return "orderForm";
    }

    @PostMapping
    public String processOrder(
            @Valid TacoOrder order, Errors errors,
            SessionStatus status) {
        if (errors.hasErrors()) return "orderForm";

        orderRepository.save(order);
        log.info("Order submitted: {}", order);
        status.setComplete();

        return "redirect:/";
    }
}
