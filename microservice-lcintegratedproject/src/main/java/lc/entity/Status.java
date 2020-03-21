package lc.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author liuchaoOvO on 2019/5/29
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class Status<T> {
    private long code;
    private String msg;
    private T data;
}
