package br.com.comunicacaoentremicrosservicos.productapi.modules.produto.dto;

import br.com.comunicacaoentremicrosservicos.productapi.modules.category.dto.CategoryResponse;
import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.model.Product;
import br.com.comunicacaoentremicrosservicos.productapi.modules.supplier.dto.SupplierResponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Integer id;
    private String name;
    private Integer quantityAvailable;
    @JsonProperty("create_at")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createAt;
    private SupplierResponse supplier;
    private CategoryResponse category;

    public static ProductResponse of(Product product) {
        return ProductResponse
                .builder()
                .id(product.getId())
                .name(product.getName())
                .quantityAvailable(product.getQuantityAvailable())
                .createAt(product.getCreateAt())
                .supplier(SupplierResponse.of(product.getSupplier()))
                .category(CategoryResponse.of(product.getCategory()))
                .build();
    }

}
