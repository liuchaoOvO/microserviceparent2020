package lc.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author liuchaoOvO on 2019/9/19
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class TopicObj implements Serializable {
    private static final long serialVersionUID = 1L;

    private String topicObj_id;
    private String topicObj_name;
    private String topicObj_code;
}
