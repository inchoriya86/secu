package com.icia.secu.controller;

import com.icia.secu.dto.MemberDTO;
import com.icia.secu.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class MemberController {

    private ModelAndView mav;

    private final MemberService msvc;

    // ABCDEFGHI
    // abcdefghi


    // mJoin(GET) : 회원가입 페이지 요청
    @GetMapping("/mJoin")
    public String mJoin(){
        return "mJoin";
    }

    // mJoin(POST) : 회원가입
    @PostMapping("/mJoin")
    public ModelAndView mJoin(@ModelAttribute MemberDTO member) throws IOException {

        System.out.println("[1]contorller member : " + member);
        return msvc.mJoin(member);
    }

    // mLogin(GET) : 로그인 페이지 요청
    @GetMapping("/mLogin")
    public String mLogin(){
        return "mLogin";
    }

    // mLogin (post) : 로그인
    @PostMapping("/mLogin")
    public ModelAndView mLogin(@ModelAttribute MemberDTO member){
        return msvc.mLogin(member);
    }

    // mLogout (get) : 로그아웃
    @GetMapping("/mLogout")
    public String mLogout(HttpSession session){

        session.invalidate();
        return "index";
    }

    // mList(get) : 회원목록 페이지 요청
    @GetMapping("/mList")
    public String mListPage(){
        return "mList";
    }

    // mList(POST) : 회원목록 출력
    @PostMapping("/mList")
    public @ResponseBody List<MemberDTO> mList(){
        return msvc.mList();
    }

    // sendEmail(get) : 전체메일 보내기 페이지 요청
    @GetMapping("/sendEmail")
    public String sendEmailPage(){
        return "sendEmail";
    }

    // sendEmail(POST) : 전체메일 전송
    @PostMapping("/sendEmail")
    public ModelAndView sendEmail(@RequestParam("title") String title,
                                  @RequestParam("str") String str){
        return msvc.sendEmail(title, str);
    }

    // mView(GET) : 회원상세보기
    @GetMapping("/mView")
    public ModelAndView mView(@RequestParam("memId") String memId){
        return msvc.mView(memId);
    }





}
