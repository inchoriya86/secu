package com.icia.secu.service;

import com.icia.secu.dto.MemberDTO;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

public interface MemberService {
    String idoverlap(String memId);

    ModelAndView mJoin(MemberDTO member) throws IOException;

    ModelAndView mLogin(MemberDTO member);

    List<MemberDTO> mList();

    ModelAndView sendEmail(String title, String str);

    ModelAndView mView(String memId);

    String previewImage(MemberDTO member) throws IOException;

    Boolean checkImg(String imgSrc);
}
