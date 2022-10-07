package br.com.comunicacaoentremicrosservicos.productapi.modules.produto.service;

import br.com.comunicacaoentremicrosservicos.productapi.config.exception.ValidationException;
import br.com.comunicacaoentremicrosservicos.productapi.modules.category.service.CategoryService;
import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.dto.ProductRequest;
import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.dto.ProductResponse;
import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.model.Product;
import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.repository.ProductRepository;
import br.com.comunicacaoentremicrosservicos.productapi.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private CategoryService categoryService;

    public ProductResponse save(ProductRequest request) {
        validateProductDataInformed(request);
        validateCategoryAndSupplierNameInformed(request);
        var supplier = supplierService.findById(request.getSupplierId());
        var category = categoryService.findById(request.getCategoryId());
        var product = productRepository.save(Product.of(request, supplier, category));

        return ProductResponse.of(product);
    }

    private void validateProductDataInformed(ProductRequest request) {
        if(isEmpty(request.getName())) {
            throw new ValidationException("The product's name was not informed");
        }
        if(isEmpty(request.getQuantityAvailable())) {
            throw new ValidationException("The product's quantity was not informed");
        }
        if(request.getQuantityAvailable() <= ZERO) {
            throw new ValidationException("The quantity should not be less or equal than zero");
        }
    }

    private void validateCategoryAndSupplierNameInformed(ProductRequest request) {
        if(isEmpty(request.getCategoryId())) {
            throw new ValidationException("The category ID was not informed");
        }
        if(isEmpty(request.getSupplierId())) {
            throw new ValidationException("The supplier ID was not informed");
        }
    }

}
