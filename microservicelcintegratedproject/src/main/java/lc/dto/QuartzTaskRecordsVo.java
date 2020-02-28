package lc.dto;

/**
 *@author liuchaoOvO on 2019/5/23
 */

import lombok.Data;

@Data
public class QuartzTaskRecordsVo {
    private Long id;
    private String taskno;
    private String timekeyvalue;
    private Long executetime;
    private String taskstatus;
    private Integer failcount;
    private String failreason;
    private Long createtime;
    private Long lastmodifytime;
    private Long time;
}
