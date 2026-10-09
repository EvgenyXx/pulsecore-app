package ru.pulsecore.app.shop.application.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.CreateProductRequest;
import ru.pulsecore.app.shop.api.dto.request.ProductUpdateRequest;
import ru.pulsecore.app.shop.api.dto.response.ProductCardDto;
import ru.pulsecore.app.shop.api.dto.response.ProductCreateResponse;
import ru.pulsecore.app.shop.api.dto.response.ProductDetailDto;
import ru.pulsecore.app.shop.application.mapping.ProductMapper;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductImage;
import ru.pulsecore.app.shop.domain.entity.ProductVariant;
import ru.pulsecore.app.shop.infrastructure.exception.ProductNotFoundException;
import ru.pulsecore.app.shop.infrastructure.repository.ProductRepository;
import ru.pulsecore.app.shop.infrastructure.storage.FileStorageService;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;
    private final ProductCreationService productCreationService;
    private final ProductUpdateService productUpdateService;


    // ===== ЧТЕНИЕ =====

    @Transactional(readOnly = true)
    public Page<ProductCardDto> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.search(query, pageable)
                .map(productMapper::toCardDto);
    }

    @Transactional(readOnly = true)
    public Page<ProductCardDto> getAllActive(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByActiveTrue(pageable)
                .map(productMapper::toCardDto);
    }

    @Transactional(readOnly = true)
    public Page<ProductCardDto> getByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(productMapper::toCardDto);
    }

    @Transactional(readOnly = true)
    public ProductDetailDto getProductById(Long productId) {
        Product product = getById(productId);
        return productMapper.toDetailDto(product);
    }

    @Transactional(readOnly = true)
    public Product getById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }


    // ===== ЗАПИСЬ =====

    @Transactional
    public ProductCreateResponse createProduct(CreateProductRequest request) {
        Product product = productCreationService.create(request);
        return productMapper.toDto(product);
    }

    @Transactional
    public ProductDetailDto updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = getById(productId);
        return productMapper.toDetailDto(productUpdateService.update(product,request));
    }

    @Transactional
    public void deleteProductById(Long productId) {
        Product product = getById(productId);

        List<String> urls = product.getVariants().stream()
                .map(ProductVariant::getColor)
                .filter(c -> c != null && c.getImages() != null)
                .flatMap(c -> c.getImages().stream())
                .map(ProductImage::getUrl)
                .distinct()
                .toList();

        productRepository.delete(product);
        fileStorageService.deleteAll(urls);
    }
}