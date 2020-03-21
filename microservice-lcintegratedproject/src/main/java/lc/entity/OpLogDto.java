package lc.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuchaoOvO on 2019/4/11
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class OpLogDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String logId;
    private String system;
    private String module;
    private String menu;
    private String function;
    private String region;
    private String agencycode;
    private String agencyname;
    private String usercode;
    private String username;
    private String role;
    private String transDate;
    private String ipAddress;
    private String url;
    private int status;
    private Date recordTime;
    private String message;
}
