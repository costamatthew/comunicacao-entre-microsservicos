package br.com.comunicacaoentremicrosservicos.productapi.modules.produto.repository;


import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
}
