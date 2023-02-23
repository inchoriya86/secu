package com.icia.secu.dao;

import com.icia.secu.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberDAO {

    String idoverlap(String memId);

    void mJoin(MemberDTO member);

    List<MemberDTO> mList();

    MemberDTO mView(String memId);
}
