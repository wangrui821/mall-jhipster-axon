package com.yonyou.mall.service.catalog.service.api;

import java.net.URISyntaxException;
import java.util.List;
import javax.validation.Valid;

import com.yonyou.mall.service.catalog.service.dto.ProductDTO;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by Administrator on 2017/4/10.
 */
public interface ProductApi {
    ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) throws URISyntaxException;

    ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO) throws URISyntaxException;

    ResponseEntity<List<ProductDTO>> getAllProducts(@ApiParam Pageable pageable);

    ResponseEntity<ProductDTO> getProduct(@PathVariable Long id);

    ResponseEntity<Void> deleteProduct(@PathVariable Long id);
}
