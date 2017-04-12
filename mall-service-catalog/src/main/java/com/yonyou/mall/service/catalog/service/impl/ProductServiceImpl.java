package com.yonyou.mall.service.catalog.service.impl;

import com.yonyou.mall.service.catalog.command.CreateProductCommand;
import com.yonyou.mall.service.catalog.command.DeleteProductCommand;
import com.yonyou.mall.service.catalog.command.UpdateProductCommand;
import com.yonyou.mall.service.catalog.domain.Product;
import com.yonyou.mall.service.catalog.repository.ProductRepository;
import com.yonyou.mall.service.catalog.repository.search.ProductSearchRepository;
import com.yonyou.mall.service.catalog.service.ProductService;
import com.yonyou.mall.service.catalog.service.dto.ProductDTO;
import com.yonyou.mall.service.catalog.service.mapper.ProductMapper;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing Product.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final ProductSearchRepository productSearchRepository;

    @Autowired
    private CommandGateway commandGateway;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper,
        ProductSearchRepository productSearchRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productSearchRepository = productSearchRepository;
    }

    /**
     * Save a product.
     *
     * @param productDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ProductDTO save(ProductDTO productDTO) {
        log.debug("Request to save Product : {}", productDTO);
        Product product = productMapper.productDTOToProduct(productDTO);
//        product = productRepository.save(product);
//        productSearchRepository.save(product);

        // Send command to command bus
        if (null == product.getId()) {
            // create
            CreateProductCommand command = new CreateProductCommand(productDTO.getCode(), productDTO.getName(),
                productDTO.getPrice(), productDTO.getInventory(), productDTO.getDescription());
            commandGateway.sendAndWait(command);
        }
        else {
            // update
            UpdateProductCommand command = new UpdateProductCommand(productDTO.getCode(), productDTO.getName(),
                productDTO.getPrice(), productDTO.getInventory(), productDTO.getDescription());
            commandGateway.sendAndWait(command);
        }

        ProductDTO result = productMapper.productToProductDTO(product);
        return result;
    }

    /**
     * Get all the products.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Products");
        Page<Product> result = productRepository.findAll(pageable);
        return result.map(product -> productMapper.productToProductDTO(product));
    }

    /**
     * Get one product by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ProductDTO findOne(Long id) {
        log.debug("Request to get Product : {}", id);
        Product product = productRepository.findOne(id);
        ProductDTO productDTO = productMapper.productToProductDTO(product);
        return productDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO findByCode(String code) {
        log.debug("Request to get Product : {}", code);
        Product product = productRepository.findByCode(code);
        ProductDTO productDTO = productMapper.productToProductDTO(product);
        return productDTO;
    }

    /**
     * Delete the  product by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Product : {}", id);

//        productRepository.delete(id);
//        productSearchRepository.delete(id);

        // Send command to command bus
        Product product = productRepository.findOne(id);
        DeleteProductCommand command = new DeleteProductCommand(product.getCode());
        commandGateway.send(command);
    }

    /**
     * Search for the product corresponding to the query.
     *
     * @param query    the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Products for query {}", query);
        Page<Product> result = productSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(product -> productMapper.productToProductDTO(product));
    }
}
