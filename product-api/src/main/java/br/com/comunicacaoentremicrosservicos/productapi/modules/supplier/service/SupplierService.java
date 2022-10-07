package br.com.comunicacaoentremicrosservicos.productapi.modules.supplier.service;

import br.com.comunicacaoentremicrosservicos.productapi.config.exception.ValidationException;
import br.com.comunicacaoentremicrosservicos.productapi.modules.supplier.dto.SupplierRequest;
import br.com.comunicacaoentremicrosservicos.productapi.modules.supplier.dto.SupplierResponse;
import br.com.comunicacaoentremicrosservicos.productapi.modules.supplier.model.Supplier;
import br.com.comunicacaoentremicrosservicos.productapi.modules.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public Supplier findById(Integer id) {
        return supplierRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no supplier for the given ID."));
    }

    public SupplierResponse save(SupplierRequest request) {
        validateCategoryNameInformed(request);
        var supplier = supplierRepository.save(Supplier.of(request));

        return SupplierResponse.of(supplier);
    }

    private void validateCategoryNameInformed(SupplierRequest request) {
        if(isEmpty(request.getName())) {
            throw new ValidationException("The suppler's name was not informed");
        }
    }

}
