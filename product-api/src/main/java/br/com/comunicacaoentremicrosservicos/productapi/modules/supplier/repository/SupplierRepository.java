package br.com.comunicacaoentremicrosservicos.productapi.modules.supplier.repository;

import br.com.comunicacaoentremicrosservicos.productapi.modules.supplier.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    List<Supplier> findByNameIgnoreCaseContaining(String name);

}
