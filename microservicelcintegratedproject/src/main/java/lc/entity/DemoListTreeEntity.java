package lc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuchaoOvO on 2020/3/2
 * @Description 封装DemoListTreeEntity
 */
@AllArgsConstructor
@Data
public class DemoListTreeEntity implements Serializable {
    private Long id;
    private Long parentId;
    private List<DemoListTreeEntity> childrenList;
}
