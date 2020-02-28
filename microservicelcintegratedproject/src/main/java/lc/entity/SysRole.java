package lc.entity;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by liuchaoOvO on 2018/7/31.
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class SysRole implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
}


