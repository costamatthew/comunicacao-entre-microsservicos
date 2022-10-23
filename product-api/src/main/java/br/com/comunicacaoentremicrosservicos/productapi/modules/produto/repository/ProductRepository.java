package br.com.comunicacaoentremicrosservicos.productapi.modules.produto.repository;

import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameIgnoreCaseContaining(String name);

    List<Product> findByCategoryId(Integer id);

    List<Product> findBySupplierId(Integer id);

}
