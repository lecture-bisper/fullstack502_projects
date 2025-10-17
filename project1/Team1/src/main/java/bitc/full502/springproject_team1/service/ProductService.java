package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.DTO.CartDTO;
import bitc.full502.springproject_team1.DTO.ProductDTO;
import bitc.full502.springproject_team1.DTO.WishDTO;
import bitc.full502.springproject_team1.entity.ProductEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    List<ProductDTO> getProductList();

    ProductDTO getProductById(int productId);

    List<ProductDTO> searchProducts(String search);

    List<ProductDTO> getAllProducts();

    WishDTO getmywishlist(int customerId , int productId);

    void toggleWish(int customerId, int productId, int updated);

    List<ProductEntity> selectProductList() throws Exception;

    List<String> getProductImageList(int productId);

    List<ProductDTO> getRelated(int productId);

    List<ProductDTO> getBrandProductList(String productBrand);

    void saveProduct(ProductDTO product);

    String saveFile(MultipartFile productImage1) throws IOException;

    List<ProductDTO> getCategoryProductList(String category);

    ProductEntity selectProductById(int productId);

    List<ProductEntity> selectProductListByIds(List<Integer> productIds);

    void saveCart(CartDTO cartDTO);

    List<ProductDTO> getProductListSorted(String sort);

    List<ProductDTO> getCategoryProductListSorted(String category, String sort);

    List<ProductDTO> getBrandProductListSorted(String brand, String sort);

    ProductDTO convertToDTO(ProductEntity entity);

    List<ProductEntity> getRelatedProducts(int productId);
}
