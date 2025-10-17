package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.DTO.CartDTO;
import bitc.full502.springproject_team1.DTO.ProductDTO;
import bitc.full502.springproject_team1.DTO.WishDTO;
import bitc.full502.springproject_team1.entity.CartEntity;
import bitc.full502.springproject_team1.entity.ProductEntity;
import bitc.full502.springproject_team1.entity.WishEntity;
import bitc.full502.springproject_team1.repository.CartRepository;
import bitc.full502.springproject_team1.repository.ProductRepository;
import bitc.full502.springproject_team1.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final WishRepository wishRepository;
    private final CartRepository cartRepository;

    public ProductServiceImpl(ProductRepository productRepository, WishRepository wishRepository, CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.wishRepository = wishRepository;
        this.cartRepository = cartRepository;
    }





    //===============================================================================================================
    //=========================================리스트에 상품 불러오기=====================================================
    @Override
    public List<ProductDTO> getProductList() {
        List<ProductEntity> pent = productRepository.findAll();
        //entity 에서 가져온 정보들 중 이름컬럼과 썸네일컬럼만 저장
        //DTO 리스트를 만들어서 필요한 컬럼만 저장한다고 함
        List<ProductDTO> pdtoList = new ArrayList<>();
        for(ProductEntity entity : pent) {
            ProductDTO inpdto = new ProductDTO();
            inpdto.setProductId(entity.getProductId());
            inpdto.setProductName(entity.getProductName());
            inpdto.setProductThumnail(entity.getProductThumnail());
            inpdto.setProductPrice(entity.getProductPrice());
            pdtoList.add(inpdto);
        }

        return pdtoList;
    }

    @Override
    public ProductDTO getProductById(int productId) {
        Optional<ProductEntity> pent = productRepository.findById(productId);
        ProductDTO pdto = new ProductDTO();
        pdto.setProductId(productId);
        pdto.setProductName(productRepository.findById(productId).get().getProductName());
        pdto.setProductThumnail(productRepository.findById(productId).get().getProductThumnail());
        pdto.setProductPrice(productRepository.findById(productId).get().getProductPrice());
        pdto.setProductColor(productRepository.findById(productId).get().getProductColor());
        pdto.setProductCode(productRepository.findById(productId).get().getProductCode());
        pdto.setProductBrand(productRepository.findById(productId).get().getProductBrand());
        pdto.setProductSize(productRepository.findById(productId).get().getProductSize());
        pdto.setProductImage1(productRepository.findById(productId).get().getProductImage1());
        pdto.setProductImage2(productRepository.findById(productId).get().getProductImage2());
        pdto.setProductImage3(productRepository.findById(productId).get().getProductImage3());
        pdto.setProductImage4(productRepository.findById(productId).get().getProductImage4());
        return pdto;
    }


    //===============================================================================================================
    //==================================================상품 검색=======================================================
    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        List<ProductEntity> result = productRepository.findByProductNameContainingIgnoreCase(keyword);

        return result.stream().map(entity -> {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(entity.getProductId());
            dto.setProductName(entity.getProductName());
            dto.setProductPrice(entity.getProductPrice());
            dto.setProductBrand(entity.getProductBrand());
            dto.setProductCode(entity.getProductCode());
            dto.setProductColor(entity.getProductColor());
            dto.setProductSize(entity.getProductSize());
//            컬러 사이즈 땨로 리스트로 받아와야됨 메소드 하나 더 구현 필요
//            컬럼 내에 들어간 스트링 타입을 리스트화 시켜서 째야도미낟고 이해함 split
//            리스트화시킬때 string join 메소드 사용해서 사이에 , 넣고 나중에 스플릿 하면됨
            dto.setProductThumnail(entity.getProductThumnail());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getAllProducts() {

        List<ProductDTO> pdtoList = new ArrayList<>();
        List<ProductEntity> result = productRepository.findAll();
        for(ProductEntity entity : result) {
            ProductDTO inpdto = new ProductDTO();
            inpdto.setProductId(entity.getProductId());
            inpdto.setProductName(entity.getProductName());
            inpdto.setProductThumnail(entity.getProductThumnail());
            inpdto.setProductPrice(entity.getProductPrice());
            inpdto.setProductColor(entity.getProductColor());
            inpdto.setProductSize(entity.getProductSize());
            inpdto.setProductThumnail(entity.getProductThumnail());
            pdtoList.add(inpdto);
        }
        return pdtoList;
    }




    //===============================================================================================================
    //==================================================주문 결제=======================================================
    @Override
    public List<ProductEntity> selectProductList() throws Exception {
        return productRepository.findAllByOrderByProductId();
    }

    //===============================================================================================================
    //==================================================찜목록 추가하기=======================================================
    @Override
    public WishDTO getmywishlist(int customerId, int productId) {
        Optional<WishEntity> wishentity = wishRepository.findByProduct_ProductIdAndCustomerId(customerId ,productId);
        WishDTO wishdto = new WishDTO();
        wishdto.setWishIdx(wishentity.get().getWishIdx());
        wishdto.setProductId(wishentity.get().getProduct().getProductId());
        wishdto.setCustomerId(wishentity.get().getCustomerId());
        wishdto.setWishCheck(wishentity.get().getWishCheck());
        wishdto.setWishColor(wishentity.get().getWishColor());
        wishdto.setWishSize(wishentity.get().getWishSize());

        return wishdto;
    }

    @Override
    public void toggleWish(int customerId, int productId, int updated) {
        WishEntity wish = wishRepository.findByCustomerIdAndProduct_ProductId(customerId, productId)
                .orElse(new WishEntity());
        wishRepository.save(wish);
    }


    //===============================================================================================================
    //================================디테일 페이지 상품정보 연관상품 리뷰 호출 메소드===========================================

    @Override
    public List<String> getProductImageList(int productId) {
        Optional<ProductEntity> pent = productRepository.findById(productId);

        List<String> imageList = new ArrayList<>();
        if (pent.isPresent()) {
            ProductEntity entity = pent.get();

            if (entity.getProductImage1() != null) imageList.add(entity.getProductImage1());
            if (entity.getProductImage2() != null) imageList.add(entity.getProductImage2());
            if (entity.getProductImage3() != null) imageList.add(entity.getProductImage3());
            if (entity.getProductImage4() != null) imageList.add(entity.getProductImage4());
        }

        return imageList;
    }

    @Override
    public List<ProductDTO> getRelated(int productId) {
        ProductEntity pent1 = productRepository.findByProductId(productId);
        String code = pent1.getProductCode();
        String brand = pent1.getProductBrand();

        List<ProductEntity> relatedEntity = productRepository.findByProductBrandAndProductCode(code , brand);

        List<ProductDTO> relatedDTO = new ArrayList<>();
        for (ProductEntity entity : relatedEntity) {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(entity.getProductId());
            dto.setProductName(entity.getProductName());
            dto.setProductBrand(entity.getProductBrand());
            dto.setProductCode(entity.getProductCode());
            dto.setProductPrice(entity.getProductPrice());
            dto.setProductThumnail(entity.getProductThumnail());
            relatedDTO.add(dto);
        }
        return relatedDTO;
    }

    @Override
    public List<ProductDTO> getBrandProductList(String productBrand) {

        List<ProductEntity> pent = productRepository.findByProductBrand(productBrand);
        List<ProductDTO> pdto = new ArrayList<>();
        for(ProductEntity entity : pent){
            ProductDTO dto = new ProductDTO();
            dto.setProductId(entity.getProductId());
            dto.setProductName(entity.getProductName());
            dto.setProductBrand(entity.getProductBrand());
            dto.setProductCode(entity.getProductCode());
            dto.setProductPrice(entity.getProductPrice());
            dto.setProductThumnail(entity.getProductThumnail());
            pdto.add(dto);
        }
        return pdto;
    }

    @Override
    public void saveProduct(ProductDTO product) {
        ProductEntity entity = new ProductEntity();
        entity.setProductName(product.getProductName());
        entity.setProductPrice(product.getProductPrice());
        entity.setProductBrand(product.getProductBrand());
        entity.setProductCode(product.getProductCode());
        entity.setProductColor(product.getProductColor());
        entity.setProductSize(product.getProductSize());
        entity.setProductImage1(product.getProductImage1());
        entity.setProductImage2(product.getProductImage2());
        entity.setProductImage3(product.getProductImage3());
        entity.setProductImage4(product.getProductImage4());
        entity.setProductThumnail(product.getProductThumnail());

        productRepository.save(entity);
    }


    private static final String UPLOAD_DIR = "/Users/mac/Desktop/Spring_shop_final/SpringProject_team1/src/main/resources/static/img/product/";

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        String safeFilename = UUID.randomUUID() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        File destFile = new File(UPLOAD_DIR + safeFilename);
        file.transferTo(destFile);
        return "/img/product/" + safeFilename;  // URL 경로를 반환
    }

    @Override
    public List<ProductDTO> getCategoryProductList(String category) {

        List<ProductEntity> pent = productRepository.findByProductCode(category);
        List<ProductDTO> pdto = new ArrayList<>();
        for(ProductEntity entity : pent){
            ProductDTO dto = new ProductDTO();
            dto.setProductId(entity.getProductId());
            dto.setProductName(entity.getProductName());
            dto.setProductBrand(entity.getProductBrand());
            dto.setProductCode(entity.getProductCode());
            dto.setProductPrice(entity.getProductPrice());
            dto.setProductThumnail(entity.getProductThumnail());
            pdto.add(dto);
        }
        return pdto;
    }

    @Override
    public ProductEntity selectProductById(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
    }

    @Override
    public List<ProductEntity> selectProductListByIds(List<Integer> productIds) {
        return productRepository.findAllById(productIds);
    }

    @Override
    public void saveCart(CartDTO cartDTO) {
        CartEntity newCart = new CartEntity();
        newCart.setCartColor(cartDTO.getCartColor());
        newCart.setCartSize(cartDTO.getCartSize());
        newCart.setCustomerId(cartDTO.getCustomerId());

        ProductEntity product = new ProductEntity();
        product.setProductId(cartDTO.getProductId());
        newCart.setProduct(product);

        cartRepository.save(newCart);
    }

    @Override
    public List<ProductDTO> getProductListSorted(String sort) {
        List<ProductEntity> productEntities;

        switch (sort) {
            case "oldest":
                productEntities = productRepository.findAllByOrderByProductIdAsc();
                break;
            case "lowprice":
                productEntities = productRepository.findAllByOrderByProductPriceAsc();
                break;
            case "highprice":
                productEntities = productRepository.findAllByOrderByProductPriceDesc();
                break;
            default:
                productEntities = productRepository.findAllByOrderByProductIdDesc(); // 최신순
        }

        return productEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getCategoryProductListSorted(String category, String sort) {
        List<ProductEntity> entities;

        switch (sort) {
            case "oldest":
                entities = productRepository.findByProductCodeOrderByProductIdAsc(category);
                break;
            case "lowprice":
                entities = productRepository.findByProductCodeOrderByProductPriceAsc(category);
                break;
            case "highprice":
                entities = productRepository.findByProductCodeOrderByProductPriceDesc(category);
                break;
            default:
                entities = productRepository.findByProductCodeOrderByProductIdDesc(category);
        }

        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getBrandProductListSorted(String brand, String sort) {
        List<ProductEntity> productEntities;

        switch (sort) {
            case "oldest":
                productEntities = productRepository.findByProductBrandOrderByProductIdAsc(brand);
                break;
            case "lowprice":
                productEntities = productRepository.findByProductBrandOrderByProductPriceAsc(brand);
                break;
            case "highprice":
                productEntities = productRepository.findByProductBrandOrderByProductPriceDesc(brand);
                break;
            default:
                productEntities = productRepository.findByProductBrandOrderByProductIdDesc(brand); // 최신순
        }

        return productEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public ProductDTO convertToDTO(ProductEntity entity) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(entity.getProductId());
        dto.setProductPrice(entity.getProductPrice());
        dto.setProductName(entity.getProductName());
        dto.setProductCode(entity.getProductCode());
        dto.setProductBrand(entity.getProductBrand());
        dto.setProductThumnail(entity.getProductThumnail());
        dto.setProductColor(entity.getProductColor());
        dto.setProductSize(entity.getProductSize());

        // 이미지들
        dto.setProductImage1(entity.getProductImage1());
        dto.setProductImage2(entity.getProductImage2());
        dto.setProductImage3(entity.getProductImage3());
        dto.setProductImage4(entity.getProductImage4());

        return dto;
    }

    @Override
    public List<ProductEntity> getRelatedProducts(int productId) {
        // 기준 상품 조회
        ProductEntity me = selectProductById(productId);
        // 같은 productCode, 같은 productBrand, 자신 제외, productId 내림차순 5개 조회
        return productRepository
                .findTop5ByProductCodeAndProductBrandAndProductIdNotOrderByProductIdDesc(
                        me.getProductCode(),
                        me.getProductBrand(),
                        me.getProductId()
                );
    }

}

