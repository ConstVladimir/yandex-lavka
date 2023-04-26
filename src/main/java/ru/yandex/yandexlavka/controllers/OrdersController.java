package ru.yandex.yandexlavka.controllers;

import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.model.order.CompleteOrderRequestDto;
import ru.yandex.yandexlavka.model.order.CreateOrderRequest;
import ru.yandex.yandexlavka.model.order.OrderAssignResponse;
import ru.yandex.yandexlavka.model.order.OrderDto;
import ru.yandex.yandexlavka.ratelimiter.RateLimiter;
import ru.yandex.yandexlavka.services.OrdersService;

import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/orders")
public class OrdersController {
    @Autowired
    OrdersService ordersService;
    @Autowired
    RateLimiter rateLimiter;
    @PostMapping("")
    public ResponseEntity<ArrayList<OrderDto>> createOrders(@RequestBody CreateOrderRequest createOrderRequest) {
        Bucket bucket = rateLimiter.getBucket("createOrders");
        if (bucket.tryConsume(1)){
            ArrayList<OrderDto> orderDtoArrayList = ordersService.createOrders(createOrderRequest);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(orderDtoArrayList);
        }
        else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
    @GetMapping("/{order_id}")
    public ResponseEntity<?> getOrders(@PathVariable long order_id){
        Bucket bucket = rateLimiter.getBucket("getOrders1");
        if (bucket.tryConsume(1)){
            OrderDto needOrder = ordersService.GetOrderById(order_id);
            if (needOrder == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("{}");
            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(needOrder);
        }
        else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
    @GetMapping("")
    public ResponseEntity<ArrayList<OrderDto>> getOrders(@RequestParam(required = false, defaultValue = "1") Integer limit, @RequestParam(required = false, defaultValue = "0") Integer offset){
        Bucket bucket = rateLimiter.getBucket("getOrders");
        if (bucket.tryConsume(1)){
            ArrayList<OrderDto> orderDtoArrayList = ordersService.getOrdersResponse(limit, offset);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(orderDtoArrayList);
        }
        else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
    @PostMapping("/complete")
    public ResponseEntity<?> completeOrders (@RequestBody CompleteOrderRequestDto completeOrderRequestDto){
        Bucket bucket = rateLimiter.getBucket("completeOrders");
        if (bucket.tryConsume(1)){
            ArrayList<OrderDto> orderDtoArrayList = ordersService.completeOrders(completeOrderRequestDto);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(orderDtoArrayList);
        }
        else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
    @PostMapping("/assign")
    public ResponseEntity<?> ordersAssign (@RequestParam(required = false) LocalDate date){
        Bucket bucket = rateLimiter.getBucket("ordersAssign");
        if (bucket.tryConsume(1)){
            if (date == null) date = LocalDate.now();
            ArrayList<OrderAssignResponse> orderAssignResponseArrayList = new ArrayList<>();

            return ResponseEntity.accepted().body(orderAssignResponseArrayList);
        }
        else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
}
