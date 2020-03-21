package lc.util;


import lc.entity.DemoListTreeEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuchaoOvO on 2019/4/18
 * @description 将List集合的树形数据，转换成对应跟节点为起始节点的树形数据
 */
public class ToTree {

    /**
     * 把列表转换为树结构
     *
     * @param originalList      原始list数据
     * @param keyName           字段名称
     * @param parentFieldName   父节点字段名称
     * @param childrenFieldName 子节点字段名称
     * @return 组装后的集合
     */
    public static <T> List<T> getTree(List<T> originalList, String keyName, String parentFieldName, String childrenFieldName) throws Exception {
        Map<String, Object> map = new HashMap<>();
        for (T t : originalList) {
            String name = BeanUtils.getProperty(t, keyName);
            map.put(name, t);
        }
        // 获取根节点，即找出父节点为空的对象或父节点不在List中的对象
        List<T> topList = new ArrayList<>();
        for (T t : originalList) {
            String parent = BeanUtils.getProperty(t, parentFieldName);
            if (StringUtils.isBlank(parent)) {
                topList.add(t);
            } else {
                if (!map.containsKey(parent)) {
                    topList.add(t);
                }
            }
        }

        // 将根节点从原始list移除，减少下次处理数据
        originalList.removeAll(topList);

        // 递归封装树
        fillTree(topList, originalList, keyName, parentFieldName, childrenFieldName);

        return topList;
    }

    /**
     * 封装树
     *
     * @param parentList        要封装为树的父对象集合
     * @param originalList      原始list数据
     * @param keyName           作为唯一标示的字段名称
     * @param parentFieldName   模型中作为parent字段名称
     * @param childrenFieldName 模型中作为children的字段名称
     */
    public static <T> void fillTree(List<T> parentList, List<T> originalList, String keyName, String parentFieldName, String childrenFieldName) throws Exception {
        for (int i = 0; i < parentList.size(); i++) {
            List<T> children = fillChildren(parentList.get(i), originalList, keyName, parentFieldName, childrenFieldName);
            if (children.isEmpty()) {
                continue;
            }
            originalList.removeAll(children);
            fillTree(children, originalList, keyName, parentFieldName, childrenFieldName);
        }
    }

    /**
     * 封装子对象
     *
     * @param parent            父对象
     * @param originalList      待处理对象集合
     * @param keyName           作为唯一标示的字段名称
     * @param parentFieldName   模型中作为parent字段名称
     * @param childrenFieldName 模型中作为children的字段名称
     */
    public static <T> List<T> fillChildren(T parent, List<T> originalList, String keyName, String parentFieldName, String childrenFieldName) throws Exception {
        List<T> childList = new ArrayList<>();
        String parentId = BeanUtils.getProperty(parent, keyName);
        for (int i = 0; i < originalList.size(); i++) {
            T t = originalList.get(i);
            String childParentId = BeanUtils.getProperty(t, parentFieldName);
            if (parentId.equals(childParentId)) {
                childList.add(t);
            }
        }
        if (!childList.isEmpty()) {
            FieldUtils.writeDeclaredField(parent, childrenFieldName, childList, true);
        }
        return childList;
    }

    @Slf4j
    static class TestTreeMain {
        @Test
        @SneakyThrows
        public void main() {
            DemoListTreeEntity children1 = new DemoListTreeEntity(1L, 3L, null);
            DemoListTreeEntity children2 = new DemoListTreeEntity(2L, 4L, null);
            List prentList1 = new ArrayList<DemoListTreeEntity>();
            prentList1.add(children1);
            List prentList2 = new ArrayList<DemoListTreeEntity>();
            prentList2.add(children2);
            DemoListTreeEntity parent1 = new DemoListTreeEntity(3L, null, prentList1);
            DemoListTreeEntity parent2 = new DemoListTreeEntity(4L, null, prentList2);
            List demoListTreeEntityList = new ArrayList();
            demoListTreeEntityList.add(children1);
            demoListTreeEntityList.add(children2);
            demoListTreeEntityList.add(parent1);
            demoListTreeEntityList.add(parent2);
            demoListTreeEntityList = ToTree.getTree(demoListTreeEntityList, "id", "parentId", "childrenList");
            log.debug("demoListTreeEntityList:{}", demoListTreeEntityList);
        }
    }


}
