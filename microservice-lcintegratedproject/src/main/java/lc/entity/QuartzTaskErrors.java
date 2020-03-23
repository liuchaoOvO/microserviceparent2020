package lc.entity;

import lombok.Data;

@Data
public class QuartzTaskErrors {
    private Long id;
    private String taskexecuterecordid;
    private String errorkey;
    private Long createtime;
    private Long lastmodifytime;
    private String errorvalue;
}
