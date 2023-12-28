package webdoc.authentication.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import webdoc.authentication.domain.response.CodeMessageResponse;

@Slf4j
@Controller
public class TestController {
    @ResponseBody
    @GetMapping("/test")
    public CodeMessageResponse testResponse(){
        return new CodeMessageResponse("접근성공",200,null);
    }

    @GetMapping("/file/upload")
    public String uploadForm(){
        return "/test/upload";
    }
}
