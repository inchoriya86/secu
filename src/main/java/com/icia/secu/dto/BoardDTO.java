package com.icia.secu.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@Data
@Alias("board")
public class BoardDTO {
    private int boNum;
    private String boWriter;
    private String boTitle;
    private String boContent;
    private Date boDate;
    private int boHit;

    private MultipartFile boFile;
    private String boFileName;
}
