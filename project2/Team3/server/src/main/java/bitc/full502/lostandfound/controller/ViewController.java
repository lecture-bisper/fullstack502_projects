package bitc.full502.lostandfound.controller;

import bitc.full502.lostandfound.util.ImageRenameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ViewController {

    @GetMapping("")
    public String index() throws Exception {
        return "index";
    }

    @GetMapping("/c")
    public String chat() throws Exception {
        return "chat";
    }

    @GetMapping("/postcode")
    public String postcode() throws Exception {
        return "postcode";
    }

    @GetMapping("/renameImg")
    @ResponseBody
    public String renameImg(@RequestParam String userId) throws Exception {
        String folderPath = "C:/fullstack502/spring/202508191439/LostAndFound/upload/rename";
        ImageRenameUtil.renameImages(folderPath, userId);
        return "SUCCESS";
    }
}
