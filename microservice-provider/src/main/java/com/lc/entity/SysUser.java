package com.lc.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * @author liuchaoOvO on 2019/4/16
 */


@Setter
@Getter
@ToString
@EqualsAndHashCode
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String password;
    private int status;
    @DateTimeFormat (pattern = "yyyy-MM-dd HH:mm:ss")
    private Date last_ver;


}
