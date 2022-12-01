package br.com.comunicacaoentremicrosservicos.productapi.modules.produto.service;

import br.com.comunicacaoentremicrosservicos.productapi.config.exception.SuccessResponse;
import br.com.comunicacaoentremicrosservicos.productapi.config.exception.ValidationException;
import br.com.comunicacaoentremicrosservicos.productapi.modules.category.service.CategoryService;
import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.dto.*;
import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.model.Product;
import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.repository.ProductRepository;
import br.com.comunicacaoentremicrosservicos.productapi.modules.sales.client.SalesClient;
import br.com.comunicacaoentremicrosservicos.productapi.modules.sales.dto.SalesConfirmationDTO;
import br.com.comunicacaoentremicrosservicos.productapi.modules.sales.enums.SalesStatus;
import br.com.comunicacaoentremicrosservicos.productapi.modules.sales.rabbitmq.SalesConfirmationSender;
import br.com.comunicacaoentremicrosservicos.productapi.modules.supplier.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SalesConfirmationSender salesConfirmationSender;

    @Autowired
    private SalesClient salesClient;

    public List<ProductResponse> findAll() {
        return productRepository
                .findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByName(String name) {
        if (isEmpty(name)) {
            throw new ValidationException("The product name must be informed.");
        }

        return productRepository
                .findByNameIgnoreCaseContaining(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findBySupplierId(Integer supplierId) {
        if (isEmpty(supplierId)) {
            throw new ValidationException("The product' supplier ID must be informed.");
        }

        return productRepository
                .findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer categoryId) {
        if (isEmpty(categoryId)) {
            throw new ValidationException("The product category ID must be informed.");
        }

        return productRepository
                .findByCategoryId(categoryId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse findByIdResponse(Integer id) {
        return ProductResponse.of(findById(id));
    }

    public Product findById(Integer id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no Product for the given ID."));
    }

    public ProductResponse save(ProductRequest request) {
        validateProductDataInformed(request);
        validateCategoryAndSupplierNameInformed(request);
        var supplier = supplierService.findById(request.getSupplierId());
        var category = categoryService.findById(request.getCategoryId());
        var product = productRepository.save(Product.of(request, supplier, category));

        return ProductResponse.of(product);
    }

    public ProductResponse update(ProductRequest request, Integer id) {
        validateProductDataInformed(request);
        validateCategoryAndSupplierNameInformed(request);
        var supplier = supplierService.findById(request.getSupplierId());
        var category = categoryService.findById(request.getCategoryId());
        var product = Product.of(request, supplier, category);
        product.setId(id);
        productRepository.save(product);

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

    public Boolean existsByCategoryId(Integer categoryId) {
        return productRepository.existsByCategoryId(categoryId);
    }

    public Boolean existsBySupplierId(Integer supplierId) {
        return productRepository.existsBySupplierId(supplierId);
    }

    public SuccessResponse delete(Integer id) {
        validateInformeId(id);
        productRepository.deleteById(id);
        return SuccessResponse.create("The product was deleted.");
    }

    private void validateInformeId(Integer id) {
        if(isEmpty(id)) {
            throw new ValidationException("The product ID must be informed.");
        }
    }

    public void updateProductStock(ProductStockDTO product) {
        try {
            validateStockUpdateData(product);
            updateStock(product);
        } catch (Exception ex) {
            log.error("Error while trying to update stock for message with error: {}", ex.getMessage(), ex);
            var rejectedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.REJECTED);
            salesConfirmationSender.sendSalesConfirmationMessage(rejectedMessage);
        }
    }

    private void validateStockUpdateData(ProductStockDTO product) {
        if (isEmpty(product)
                || isEmpty(product.getSalesId())) {
            throw new ValidationException("The product data and the sales ID must be informed.");
        }
        if (isEmpty(product.getProducts())) {
            throw new ValidationException("The sales products must be informed.");
        }
        product
                .getProducts()
                .forEach(salesProduct -> {
                    if (isEmpty(salesProduct.getQuantity())
                            || isEmpty(salesProduct.getProductId())) {
                        throw new ValidationException("The ProductID and the quantity must be informed.");
                    }
                });
    }

    @Transactional
    void updateStock(ProductStockDTO product) {
        var productsForUpdate = new ArrayList<Product>();

        product
                .getProducts()
                .forEach(salesProduct -> {
                    var existingProduct = findById(salesProduct.getProductId());
                    validateQuantityInStock(salesProduct, existingProduct);
                    existingProduct.updateStock(salesProduct.getQuantity());
                    productsForUpdate.add(existingProduct);
                });

        if(!isEmpty(productsForUpdate)) {
            productRepository.saveAll(productsForUpdate);
            var approvedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.APPROVED);
            salesConfirmationSender.sendSalesConfirmationMessage(approvedMessage);
        }
    }

    private void validateQuantityInStock(ProductQuantityDTO salesProduct, Product existingProduct) {
        if(salesProduct.getQuantity() > existingProduct.getQuantityAvailable()) {
            throw new ValidationException(
                    String.format("The product %s is out of stock.", existingProduct.getId())
            );
        }
    }

    public ProductSalesResponse findProductSales(Integer id) {
        var product = findById(id);
        try {
            var sales = salesClient
                    .findSalesByProductId(product.getId())
                    .orElseThrow(() -> new ValidationException("The Sales was not found by this product"));
            return ProductSalesResponse.of(product, sales.getSalesIds());
        } catch (Exception ex) {
            throw new ValidationException("There was an error trying to get the product's sales");
        }
    }
}
