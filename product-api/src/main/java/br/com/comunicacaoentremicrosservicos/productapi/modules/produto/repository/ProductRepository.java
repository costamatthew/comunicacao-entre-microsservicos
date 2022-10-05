package br.com.comunicacaoentremicrosservicos.productapi.modules.produto.repository;

import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
