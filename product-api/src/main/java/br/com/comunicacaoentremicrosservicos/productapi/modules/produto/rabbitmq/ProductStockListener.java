package br.com.comunicacaoentremicrosservicos.productapi.modules.produto.rabbitmq;

import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.dto.ProductStockDTO;
import br.com.comunicacaoentremicrosservicos.productapi.modules.produto.service.ProductService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductStockListener {

    @Autowired
    private ProductService productService;

    @RabbitListener(queues = "${app-config.rabbit.queue.product-stock}")
    public void recieveProductStockMessage(ProductStockDTO product) {
        productService.updateProductStock(product);
    }

}
