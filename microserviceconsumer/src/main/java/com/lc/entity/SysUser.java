package com.lc.entity;






import com.fasterxml.jackson.annotation.JsonFormat;
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
public class SysUser implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String password;
    private int status;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date last_ver;


    public SysUser(){

    }
    public SysUser(String id, String username, String password)
    {
        this.id=id;
        this.username=username;
        this.password=password;
    }
    public SysUser(String id, String username, String password, int status, Date last_ver)
    {
        this.id=id;
        this.username=username;
        this.password=password;
        this.status=status;
        this.last_ver=last_ver;
    }
}
