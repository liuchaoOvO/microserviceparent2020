package lc.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author liuchaoOvO on 2019/4/1
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class SysVersion {
    private static final long serialVersionUID = 1L;

    public SysVersion() {
    }

    private String id;
    private String sqlFileName;
    private String execTime;
    private String remark;
    private String subsyscode;
}
