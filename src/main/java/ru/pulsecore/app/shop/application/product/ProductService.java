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
import ru.pulsecore.app.shop.application.category.CategoryService;
import ru.pulsecore.app.shop.application.image.ProductImageService;
import ru.pulsecore.app.shop.application.mapping.ProductMapper;
import ru.pulsecore.app.shop.domain.entity.Category;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductImage;
import ru.pulsecore.app.shop.infrastructure.exception.ProductNotFoundException;
import ru.pulsecore.app.shop.infrastructure.repository.ProductImagesRepository;
import ru.pulsecore.app.shop.infrastructure.repository.ProductRepository;
import ru.pulsecore.app.shop.infrastructure.storage.FileStorageService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final ProductImageService productImageService;
    private final FileStorageService fileStorageService;
    private final ProductImagesRepository productImageRepository;


    // ===== ЧТЕНИЕ =====

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
        Category category = categoryService.getCategoryById(request.categoryId());

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        List<ProductImage> images = request.images().stream()
                .map(imgReq -> productImageService.create(product, imgReq))
                .toList();
        product.setImages(images);

        Product saved = productRepository.save(product);

        return productMapper.toDto(saved);
    }

    @Transactional
    public ProductDetailDto updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = getById(productId);

        productMapper.updateProduct(request, product);

        if (request.categoryId() != null) {
            Category category = categoryService.getCategoryById(request.categoryId());
            product.setCategory(category);
        }

        if (request.images() != null) {
            replaceImages(product, request.images());
        }

        Product saved = productRepository.save(product);
        log.debug("Продукт успешно обновлён: id={}", saved.getId());

        return productMapper.toDetailDto(saved);
    }

    @Transactional
    public void deleteProductById(Long productId) {
        Product product = getById(productId);
        List<String> urls = product.getImages().stream()
                .map(ProductImage::getUrl)
                .toList();
        productRepository.delete(product);
        fileStorageService.deleteAll(urls);
    }


    // ===== ПРИВАТНЫЕ =====

    private void replaceImages(Product product, List<ProductUpdateRequest.ImageRequest> images) {
        List<String> oldUrls = productImageRepository.findUrlsByProductId(product.getId());

        productImageRepository.deleteByProductId(product.getId());

        List<ProductImage> newImages = images.stream()
                .map(img -> ProductImage.builder()
                        .product(product)
                        .url(img.url())
                        .main(img.main())
                        .sortOrder(img.sortOrder())
                        .build())
                .toList();
        productImageRepository.saveAll(newImages);

        Set<String> newUrls = newImages.stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toSet());

        List<String> toDelete = oldUrls.stream()
                .filter(url -> !newUrls.contains(url))
                .toList();

        if (!toDelete.isEmpty()) {
            fileStorageService.deleteAll(toDelete);
        }
    }
}