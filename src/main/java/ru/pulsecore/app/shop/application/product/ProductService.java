package ru.pulsecore.app.shop.application.product;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.CreateProductRequest;
import ru.pulsecore.app.shop.api.dto.response.ProductCardDto;
import ru.pulsecore.app.shop.api.dto.response.ProductCreateResponse;
import ru.pulsecore.app.shop.api.dto.response.ProductDetailDto;
import ru.pulsecore.app.shop.application.category.CategoryService;
import ru.pulsecore.app.shop.application.image.ProductImageService;
import ru.pulsecore.app.shop.application.mapping.ProductMapper;
import ru.pulsecore.app.shop.domain.Category;
import ru.pulsecore.app.shop.domain.Product;
import ru.pulsecore.app.shop.domain.ProductImage;
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
    private final CategoryService categoryService;
    private final ProductImageService productImageService;
    private final FileStorageService fileStorageService;

    @Transactional
    public void  deleteProductById(Long productId){
        Product product = getById(productId);
        List<String>urls = product.getImages().stream()
                .map(ProductImage::getUrl)
                .toList();
        productRepository.delete(product);
        fileStorageService.deleteAll(urls);


    }


    public ProductDetailDto getProductById(Long productId){
        Product product = getById(productId);
        return productMapper.toDetailDto(product);
    }

    public Product getById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(()-> new ProductNotFoundException(productId));
    }


    public List<ProductCardDto> getByCategory(Long categoryId) {
    return productRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
            .map(productMapper::toCardDto)
            .toList();
}


    public List<ProductCardDto> getAllActive() {
        return productRepository.findByActiveTrue().stream()
                .map(productMapper::toCardDto)
                .toList();
    }

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
}
