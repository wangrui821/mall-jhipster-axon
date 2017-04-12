package com.yonyou.mall.service.catalog.service.api;

import java.net.URISyntaxException;
import java.util.List;

import com.yonyou.mall.service.catalog.service.dto.ProductDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/**
 * Created by Administrator on 2017/4/10.
 */
public interface ProductApi {
    ResponseEntity<ProductDTO> createProduct(ProductDTO productDTO) throws URISyntaxException;

    ResponseEntity<ProductDTO> updateProduct(ProductDTO productDTO) throws URISyntaxException;

    ResponseEntity<List<ProductDTO>> getAllProducts(Pageable pageable);

    ResponseEntity<ProductDTO> getProduct(Long id);

    ResponseEntity<ProductDTO> getProductByCode(String code);

    ResponseEntity<Void> deleteProduct(Long id);
}
