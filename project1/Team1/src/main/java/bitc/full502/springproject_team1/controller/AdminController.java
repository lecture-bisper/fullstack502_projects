package bitc.full502.springproject_team1.controller;

import bitc.full502.springproject_team1.DTO.ProductDTO;
import bitc.full502.springproject_team1.entity.BoardEntity;
import bitc.full502.springproject_team1.entity.ColorEntity;
import bitc.full502.springproject_team1.entity.ProductEntity;
import bitc.full502.springproject_team1.entity.SizeEntity;
import bitc.full502.springproject_team1.repository.ColorRepository;
import bitc.full502.springproject_team1.repository.SizeRepository;
import bitc.full502.springproject_team1.service.BoardService;
import bitc.full502.springproject_team1.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final ProductService productService;
    private final BoardService boardService;


//    String customerId = (String) session.getAttribute("customerId");
//
//        if (customerId == null || customerId.isEmpty()) {
//        // 로그인하지 않은 상태일 경우 로그인 페이지로 리디렉션
//        return "redirect:/login";
//    }
//    admin 전체에서 로그인 세션 확인 및 해당 아이디가 admin 과 일치하는지에 대한 확인 필요 , 페이지 전체 완료까지 적용하지 않겠음

//     ==========================================어드민페이지 ========================================

    @GetMapping("/admin")
    public ModelAndView admin() {
        ModelAndView mv = new ModelAndView("admin/admin");
        return mv;
    }

    // 룩북 게시판 관리 페이지 (게시글 리스트 + 버튼)
    @GetMapping("/admin/boardlist")
    public String adminBoardList(Model model) {
        List<BoardEntity> boards = boardService.findAll();
        model.addAttribute("boards", boards);
        return "admin/adminBoardList";  // templates/admin/adminBoardList.html
    }

    // 게시글 삭제 처리 (AJAX 요청 예상)
    @PostMapping("/admin/boardlist/delete/{boardIdx}")
    @ResponseBody
    public String deleteBoard(@PathVariable Integer boardIdx) {
        boardService.deleteById(boardIdx);
        return "success";
    }

//    ========================================== 컬러 사이즈 등록 ========================================
    @GetMapping("/admin/options")
    public ModelAndView csplus() {
        ModelAndView mv = new ModelAndView("admin/csplus");
        return mv;
    }
    @PostMapping("addColorSize")
    public String addColorSize(@RequestParam("colorList[]") List<String> color , @RequestParam("sizeList[]") List<String> size) {
        System.out.println(color);
        System.out.println(size);

        List<String> colorList = color.stream()
                .map(s -> s.replace("\"", "").trim())
                .toList();
        List<String> sizeList = size.stream()
                .map(s -> s.replace("\"", "").trim())
                .toList();
        for (String c : colorList) {
            ColorEntity colorEntity = new ColorEntity();
            colorEntity.setColorName(c);
            colorRepository.save(colorEntity);  // JPA 저장
        }
        for (String s : sizeList) {
            SizeEntity sizeEntity = new SizeEntity();
            sizeEntity.setSizeName(s);
            sizeRepository.save(sizeEntity);  // JPA 저장
        }
        return "redirect:/admin";


    }


    //    ========================================== 상품 등록 ========================================

    @GetMapping("/admin/product/add")
    public ModelAndView adminregist() {
        ModelAndView mv = new ModelAndView("admin/adminregist");

        mv.addObject("colorList", colorRepository.findAll());
        mv.addObject("sizeList", sizeRepository.findAll());
        return mv;
    }

    @PostMapping("/adminRegist")
    public String adminRegist(
//    @ModelAttribute ProductDTO product,
    @RequestParam(value = "productBrand" , required = false) String productBrand,
    @RequestParam(value = "productCode" , required = false) String productCode,
    @RequestParam(value = "productName" ,required = false) String productName,
    @RequestParam(value = "productPrice" ,required = false) Integer productPrice,
    @RequestParam(value = "productColor[]", required = false) List<String> productColors,
    @RequestParam(value = "productSize[]", required = false) List<String> productSizes,
    @RequestParam(value = "productImage1", required = false) MultipartFile productImage1,
    @RequestParam(value = "productImage2", required = false) MultipartFile productImage2,
    @RequestParam(value = "productImage3", required = false) MultipartFile productImage3,
    @RequestParam(value = "productImage4", required = false) MultipartFile productImage4,
    @RequestParam(value = "uploadThumbnail", required = false) MultipartFile uploadThumbnail) throws Exception{

        ProductDTO product = new ProductDTO();

        String uploadDir = "/Users/mac/Desktop/Spring_shop_final/SpringProject_team1/src/main/resources/static/img/product/";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) uploadFolder.mkdirs();

        if (!productImage1.isEmpty()) {
            String filePath1 = saveFile(productImage1, uploadDir);
            product.setProductImage1(filePath1);
        }
        if (!productImage2.isEmpty()) {
            String filePath2 = saveFile(productImage2, uploadDir);
            product.setProductImage2(filePath2);
        }
        if (!productImage3.isEmpty()) {
            String filePath3 = saveFile(productImage3, uploadDir);
            product.setProductImage3(filePath3);
        }
        if (!productImage4.isEmpty()) {
            String filePath4 = saveFile(productImage4, uploadDir);
            product.setProductImage4(filePath4);
        }

        // 썸네일 처리
        if (!uploadThumbnail.isEmpty()) {
            String filePath = saveFile(uploadThumbnail, uploadDir);
            product.setProductThumnail(filePath);
        }

        // 컬러 및 사이즈 옵션 처리
        String productColor = (productColors != null) ? String.join(",", productColors) : "";
        String productSize = (productSizes != null) ? String.join(",", productSizes) : "";
        product.setProductColor(productColor);
        product.setProductSize(productSize);
        product.setProductBrand(productBrand);
        product.setProductCode(productCode);
        product.setProductName(productName);
        product.setProductPrice(productPrice);


        productService.saveProduct(product);

        return "redirect:admin";
    }
    private String saveFile(MultipartFile file, String uploadDir) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String safeFilename = UUID.randomUUID() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        File destFile = new File(uploadDir + safeFilename);
        file.transferTo(destFile);
        return "/img/product/" + safeFilename; // 저장된 경로 반환
    }
}
