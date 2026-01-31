package com.gaguena.demo.product;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductRest {

    @GetMapping("/")
    public List<?> get() {
        return List.of(new ProductData(1, "Batata"), new ProductData(2, "Arroz"), new ProductData(3, "Feijao"));
    }

}
