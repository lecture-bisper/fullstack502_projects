package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.entity.ProductEntity;
import bitc.full502.springproject_team1.repository.ColorRepository;
import bitc.full502.springproject_team1.repository.ProductRepository;
import bitc.full502.springproject_team1.repository.SizeRepository;
import bitc.full502.springproject_team1.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/product")
public class AdminProductController {

    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;

    // ✅ 상품 목록 조회 + 정렬
    @GetMapping("/revision")
    public String showProductList(@RequestParam(defaultValue = "latest") String sort, Model model) {
        List<ProductEntity> productList;

        switch (sort) {
            case "oldest":
                productList = productRepository.findAllByOrderByProductIdAsc();
                break;
            case "highPrice":
                productList = productRepository.findAllByOrderByProductPriceDesc();
                break;
            case "lowPrice":
                productList = productRepository.findAllByOrderByProductPriceAsc();
                break;
            default:
                productList = productRepository.findAllByOrderByProductIdDesc(); // 최신순
        }

        model.addAttribute("productList", productList);
        model.addAttribute("sort", sort); // 현재 선택된 정렬값
        return "admin/adminproductrevision";
    }

    // ✅ 상품 삭제
    @PostMapping("/delete/{productId}")
    @Transactional
    public String deleteProduct(@PathVariable int productId) {
        productRepository.deleteById(productId);
        return "redirect:/admin/product/revision";
    }

    // ✅ 상품 수정 폼 이동
    @GetMapping("/edit/{productId}")
    public String editForm(@PathVariable int productId, Model model) {

        ProductEntity product = productRepository.findById(productId).orElseThrow();
        model.addAttribute("product", product);

        // --------------------------------- 정환 수정 ----------------------------------------
        List<String> allColors = colorRepository.findAll()
                .stream()
                .map(c -> c.getColorName())
                .collect(Collectors.toList());

        List<String> allSizes = sizeRepository.findAll()
                .stream()
                .map(s -> s.getSizeName())
                .collect(Collectors.toList());

        // 상품에 저장된 컬러 리스트로 파싱
        List<String> productColors = product.getProductColor() != null
                ? Arrays.stream(product.getProductColor().split(","))
                .map(String::trim)
                .collect(Collectors.toList())
                : List.of();

        // 상품에 저장된 사이즈 리스트로 파싱
        List<String> productSizes = product.getProductSize() != null
                ? Arrays.stream(product.getProductSize().split(","))
                .map(String::trim)
                .collect(Collectors.toList())
                : List.of();

        // 전체 컬러 중 상품에 포함된 컬러만 필터링
        List<String> filteredColors = allColors.stream()
                .filter(productColors::contains)
                .collect(Collectors.toList());

        // 전체 사이즈 중 상품에 포함된 사이즈만 필터링
        List<String> filteredSizes = allSizes.stream()
                .filter(productSizes::contains)
                .collect(Collectors.toList());

        model.addAttribute("colorList", filteredColors);
        model.addAttribute("sizeList", filteredSizes);

        // ———————————————— 정환 수정 ————————————————————

        return "admin/adminrevision";
    }

    // ✅ 상품 수정 처리 + 이미지 업로드 + 삭제 처리
    @PostMapping("/edit")
    public String updateProduct(
            @ModelAttribute ProductEntity product,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile productImage1,
            @RequestParam(required = false) MultipartFile productImage2,
            @RequestParam(required = false) MultipartFile productImage3,
            @RequestParam(required = false) MultipartFile productImage4,
            @RequestParam(required = false) MultipartFile productThumnail,
            @RequestParam(defaultValue = "false") boolean deleteImage1,
            @RequestParam(defaultValue = "false") boolean deleteImage2,
            @RequestParam(defaultValue = "false") boolean deleteImage3,
            @RequestParam(defaultValue = "false") boolean deleteImage4,
            @RequestParam(defaultValue = "false") boolean deleteThumbnail,
            @RequestParam(required = false, name = "productColor") String[] productColorArr,
            @RequestParam(required = false, name = "productSize") String[] productSizeArr,
            Model model
    ) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);
            model.addAttribute("productColorList", product.getProductColor() != null ? product.getProductColor().split(",") : new String[]{});
            model.addAttribute("productSizeList", product.getProductSize() != null ? product.getProductSize().split(",") : new String[]{});
            return "admin/adminrevision";
        }

        ProductEntity saved = productRepository.findById(product.getProductId()).orElseThrow();

        // ✅ 삭제 요청 처리
        if (deleteImage1) saved.setProductImage1(null);
        if (deleteImage2) saved.setProductImage2(null);
        if (deleteImage3) saved.setProductImage3(null);
        if (deleteImage4) saved.setProductImage4(null);
        if (deleteThumbnail) saved.setProductThumnail(null);

        // ✅ 파일 업로드 처리
        if (productImage1 != null && !productImage1.isEmpty()) saved.setProductImage1(saveFile(productImage1));
        if (productImage2 != null && !productImage2.isEmpty()) saved.setProductImage2(saveFile(productImage2));
        if (productImage3 != null && !productImage3.isEmpty()) saved.setProductImage3(saveFile(productImage3));
        if (productImage4 != null && !productImage4.isEmpty()) saved.setProductImage4(saveFile(productImage4));
        if (productThumnail != null && !productThumnail.isEmpty()) saved.setProductThumnail(saveFile(productThumnail));

        // ✅ 컬러/사이즈
        saved.setProductColor(productColorArr != null ? Arrays.stream(productColorArr).distinct().collect(Collectors.joining(",")) : null);
        saved.setProductSize(productSizeArr != null ? Arrays.stream(productSizeArr).distinct().collect(Collectors.joining(",")) : null);

        // ✅ 기본 정보 수정
        saved.setProductName(product.getProductName());
        saved.setProductPrice(product.getProductPrice());
        saved.setProductBrand(product.getProductBrand());
        saved.setProductCode(product.getProductCode());

        productRepository.save(saved);
        return "redirect:/admin/product/revision";
    }

    // ✅ 파일 저장 로직
    private String saveFile(MultipartFile file) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + file.getOriginalFilename();
        Path path = Paths.get("src/main/resources/static/img/product", fileName);
        Files.createDirectories(path.getParent());
        file.transferTo(path.toFile());
        return "/img/product/" + fileName;
    }
}
