package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.DTO.CartDTO;
import bitc.full502.springproject_team1.DTO.ProductDTO;
import bitc.full502.springproject_team1.DTO.WishDTO;
import bitc.full502.springproject_team1.entity.ProductEntity;
import bitc.full502.springproject_team1.service.HistoryService;
import bitc.full502.springproject_team1.service.ProductService;
import bitc.full502.springproject_team1.service.ReviewService;
import bitc.full502.springproject_team1.service.WishService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService;
    private final WishService wishService;
    private final HistoryService historyService;


    //===============================================================================================================
    //==========리스트에 상품 불러오기, 브랜드 카테고리에서 리스트로 이동 포함=====================================================
    @GetMapping("/productList")
    public ModelAndView productList(@RequestParam(defaultValue = "latest") String sort) {
        List<ProductDTO> pdto = productService.getProductListSorted(sort);
        ModelAndView mv = new ModelAndView("product/productList");
        mv.addObject("productList", pdto);
        mv.addObject("selectedSort", sort);
        return mv;
    }

    @GetMapping("/productCategoryList")
    public ModelAndView categoryList(@RequestParam("productCategory") String category,
                                     @RequestParam(name = "sort", defaultValue = "latest") String sort) {
        List<ProductDTO> pdto = productService.getCategoryProductListSorted(category, sort);
        String viewname = "product/productList";
        ModelAndView mv = new ModelAndView(viewname);
        mv.addObject("category", category);
        mv.addObject("productList", pdto);
        mv.addObject("selectedSort", sort);  // 선택한 정렬값도 뷰에 전달
        mv.addObject("selectedCategory", category);
        return mv;
    }

    @GetMapping("/productBrandList")
    public ModelAndView brandList(@RequestParam("productBrand") String brand,
                                  @RequestParam(defaultValue = "latest") String sort) {
        List<ProductDTO> pdto = productService.getBrandProductListSorted(brand, sort);
        String viewname = "brand/" + brand;
        ModelAndView mv = new ModelAndView(viewname);
        mv.addObject("brand", brand);
        mv.addObject("productList", pdto);
        mv.addObject("selectedSort", sort);
        return mv;
    }
    //  테스트 페이지, 나중에 확인 필요
    @GetMapping("/brand-test")
    @ResponseBody
    public List<ProductDTO> brandListTest(@RequestParam("brand") String brand) {
        return productService.getBrandProductList(brand);
    }


    //    ===============================================================================================================
//    ==================================================상품 검색=======================================================
    @GetMapping("/product/list")
    public String showProductList(@RequestParam(required = false) String search, Model model) {
        List<ProductDTO> products = productService.searchProducts(search);
        List<ProductDTO> allProducts = productService.getAllProducts();
        if (!products.isEmpty()) {
            model.addAttribute("productList", products);
            model.addAttribute("search", search);
            return "product/productList";
        } else {
            model.addAttribute("productList", allProducts);
            return "product/productList";
        }
    }

    @GetMapping("/productDetail")
    public ModelAndView productDetail(@RequestParam("id") int productId,
                                      HttpSession session) {
        Integer loginId = (Integer) session.getAttribute("loginId");
        ModelAndView mv = new ModelAndView("product/productDetail");

        // 기존 DTO 방식
        ProductDTO product = productService.getProductById(productId);
        List<String> imageList = productService.getProductImageList(productId);
        List<Map<String, Object>> reviewList = reviewService.getReviewList(productId);

        // 새로운 연관 상품 로직 (엔티티 기반)
        List<ProductEntity> relatedList = productService.getRelatedProducts(productId);

        // 옵션 목록 분리
        List<String> colorList = new ArrayList<>();
        if (product.getProductColor() != null && !product.getProductColor().isBlank()) {
            colorList = Arrays.asList(product.getProductColor().split(","));
        }
        List<String> sizeList = new ArrayList<>();
        if (product.getProductSize() != null && !product.getProductSize().isBlank()) {
            sizeList = Arrays.asList(product.getProductSize().split(","));
        }

        // 뷰에 모델 속성 추가
        mv.addObject("colorList", colorList);
        mv.addObject("sizeList", sizeList);
        mv.addObject("reviewList", reviewList);
        mv.addObject("imageList", imageList);
        mv.addObject("product", product);
        mv.addObject("relatedList", relatedList);
        mv.addObject("loginId", loginId);

        // 방문 기록 저장
        if (loginId != null) {
            historyService.saveHistory(loginId, productId);
        }

        return mv;
    }
}
