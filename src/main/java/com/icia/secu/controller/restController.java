package com.icia.secu.controller;

import com.icia.secu.dto.MemberDTO;
import com.icia.secu.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// RestController는 ResponseBody를 작성하지 않아도
// ajax 함수를 처리할 수 있다.
// Controller - modelAndView / RestController - data 처리
@RestController
@RequiredArgsConstructor
public class restController {

    private final MemberService msvc;

    // idoverlap(POST) : 아이디 중복체크
    @PostMapping("/idoverlap")
    public String idoverlap(@RequestParam("memId") String memId){
        System.out.println("[1]controller : " + memId);
        return msvc.idoverlap(memId);
    }

    // previewImage : 이미지 미리보기
    @PostMapping("/previewImage")
    public String previewImage(@ModelAttribute MemberDTO member) throws IOException {
        System.out.println(member);

        String memProfile = msvc.previewImage(member);

        boolean check = true;

        Path path = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/profile");;
        String searchImg = path + "/" + memProfile;
        File searchFile = new File(searchImg);


        while(check){
            int i = 0;

            if(searchFile.exists()){
                return memProfile;
            }

            System.out.println(i);
            i++;
        }

        return "로딩";

    }


}
