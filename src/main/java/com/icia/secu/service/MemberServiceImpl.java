package com.icia.secu.service;

import com.icia.secu.dao.MemberDAO;
import com.icia.secu.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberDAO mdao;

    private final PasswordEncoder pwEnc;

    private final JavaMailSender mailSender;

    private final HttpSession session;

    private ModelAndView mav;

    @Override
    public String idoverlap(String memId) {
        String result = mdao.idoverlap(memId);

        if (result != null) {
            // 아이디 존재(중복o)
            return "NO";
        } else {
            // 아이디 존재x (중복x)
            return "OK";
        }

    }


    @Override
    public ModelAndView mJoin(MemberDTO member) throws IOException {
        System.out.println("[2]service member : " + member);
        mav = new ModelAndView();

        // 비밀번호 암호화
        // [1] 입력한 비밀번호 가져오기 : member.getMemPw()
        // [2] 입력한 비밀번호 암호화 : pwEnc.encode()
        // [3] 암호화 된 비밀번호 저장 : member.setMemPw()

        member.setMemPw(pwEnc.encode(member.getMemPw()));

        System.out.println("암호화 된 비밀번호 : " + member.getMemPw());

        // 파일업로드 설정
        MultipartFile memProfile = member.getMemProfile();

        if (!memProfile.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/profile");

            String uuid = UUID.randomUUID().toString().substring(0, 8);

            String originalFileName = memProfile.getOriginalFilename();

            String memProfileName = uuid + "_" + originalFileName;

            member.setMemProfileName(memProfileName);

            String savePath = path + "/" + memProfileName;

            memProfile.transferTo(new File(savePath));

        }


        // 주소 api 결합(나중에 따로 해보기)
        String addr1 = member.getAddr1();
        String addr2 = member.getAddr2();
        String addr3 = member.getAddr3();

        String addr = "(" + addr1 + ") " + addr2 + ", " + addr3;
        member.setMemAddr(addr);

        try {
            // 회원가입 성공시 (에러나 예외처리가 없을 경우)
            mdao.mJoin(member);

            // 가입성공 메일 보내기
            MimeMessage mail = mailSender.createMimeMessage();

            String str = "<h2>안녕하세요. 인천일보 아카데미 입니다.</h2>" +
                    "<p>회원가입을 진심으로 축하드립니다!</p>" +
                    "<p>로그인 후 이용이 가능합니다. 로그인 페이지로 이동하시겠습니까?</p>" +
                    "<p><a href='http://192.168.0.57:9090/mLogin'>로그인 하기</a></p>";

            mail.setSubject("인천일보 아카데미 회원가입");
            mail.setText(str, "UTF-8", "html");
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress(member.getMemEmail()));

            mailSender.send(mail);


            mav.setViewName("index");
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("회원가입 실패!");
            // 예외처리 발생 시 
            mav.setViewName("mJoin");
            // 파일 삭제
            Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/profile");
            String deletePath = path + "/" + member.getMemProfileName();

            File deleteFile = new File(deletePath);

            if (deleteFile.exists()) {
                deleteFile.delete();
            }
        }

        return mav;
    }

    @Override
    public ModelAndView mLogin(MemberDTO member) {
        mav = new ModelAndView();

        MemberDTO loginMember = mdao.mView(member.getMemId());

        // member 객체 : memId, memPw(입력한값)

        // loginMember 객체 : memId, memPw(암호화o), ........ all

        //입력한 비밀번호 : getMemPw()와 암호화 된 비밀번호 : loginMember.getMemPw()를
        // pwEnc.mathces()로 비교하여서 일치하면 true값, 불일치하면 false값을 반환한다.
        if(pwEnc.matches(member.getMemPw(), loginMember.getMemPw())){
            session.setAttribute("login", loginMember);
            mav.setViewName("index");
        } else {
            mav.setViewName("mLogin");
        }

        return mav;
    }

    @Override
    public List<MemberDTO> mList() {
        return mdao.mList();
    }

    @Override
    public ModelAndView sendEmail(String title, String str) {
        mav = new ModelAndView();

        List<MemberDTO> memberList = mdao.mList();

        MimeMessage mail = mailSender.createMimeMessage();

        try {
            mail.setSubject(title);
            mail.setText(str, "UTF-8", "html");

            // 보낼 메일 제목, 내용 준비
            // 받을 사람
            for(MemberDTO member : memberList){
                 mail.addRecipient(Message.RecipientType.TO, new InternetAddress(member.getMemEmail()));
                 mailSender.send(mail);

                 System.out.println(member.getMemEmail() + "으로 메일 전송!");
            }

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


        mav.setViewName("sendEmail");
        return mav;
    }

    @Override
    public ModelAndView mView(String memId) {
        mav = new ModelAndView();
        MemberDTO member = mdao.mView(memId);

        mav.setViewName("mView");
        mav.addObject("view", member);

        return mav;
    }

    @Override
    public String previewImage(MemberDTO member) throws IOException{
        // 파일업로드 설정
        MultipartFile memProfile = member.getMemProfile();

        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String originalFileName = memProfile.getOriginalFilename();
        String memProfileName = uuid + "_" + originalFileName;

        if (!memProfile.isEmpty()) {
            Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/profile");

            member.setMemProfileName(memProfileName);

            String savePath = path + "/" + memProfileName;

            memProfile.transferTo(new File(savePath));

        }

        return memProfileName;
    }

    @Override
    public Boolean checkImg(String imgSrc) {

        Path path = Paths.get(System.getProperty("user.dir"),"src/main/resources/static/profile");;

        // 파일 찾기
        String searchImg = path + "/" + imgSrc;
        File searchFile = new File(searchImg);

        if(!searchFile.exists()){
            return true;
        } else {
            return false;
        }
    }


}
