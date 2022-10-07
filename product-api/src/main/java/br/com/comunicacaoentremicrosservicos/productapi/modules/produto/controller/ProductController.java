package br.com.comunicacaoentremicrosservicos.productapi.modules.produto.controller;

import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.dto.ProductRequest;
import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.dto.ProductResponse;
import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ProductResponse save(@RequestBody ProductRequest request) {
        return productService.save(request);
    }

}
