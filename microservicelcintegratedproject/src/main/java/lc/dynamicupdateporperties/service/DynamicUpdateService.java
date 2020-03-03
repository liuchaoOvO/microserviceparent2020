package lc.dynamicupdateporperties.service;


import lc.dynamicupdateporperties.FileListener;
import lc.dynamicupdateporperties.FileMonitor;
import lc.dynamicupdateporperties.utils.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuchaoOvO
 * @date 2019/3/22 下午10:17
 */
@Slf4j
@Service
public class DynamicUpdateService implements BeanPostProcessor {
    //本地缓存变量，存放配置表中的所有键值对
    private static final ConcurrentHashMap<String, String> iniMap = new ConcurrentHashMap<String, String>();

    @Value ("${spring.application.name}")
    private String serviceId;

    public DynamicUpdateService() {
        log.debug("DynamicUpdateService.Constructions....");
    }

   /* @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        log.debug("DynamicUpdateService.postProcessAfterInitialization->to do minor method...");
        minor();
        return bean;
    }*/
    @PostConstruct
    public void minor() {
        log.debug("DynamicUpdateService.minor....");
        refresh();
        //需要监听的配置文件dirpath
        serviceId = serviceId.replace("-", ""); //文件夹中的microservicecloud-lcintegratedproject  - 不显示
        String dir = System.getProperty("user.dir") + "\\" + serviceId + "/src/main/resources/lcpersionalized.properties";
        dir = dir.replace("\\", "/");
        log.debug("需要监听的配置文件的dir:{}", dir);
        FileMonitor fileMonitor = new FileMonitor(dir, 60);
        fileMonitor.registerFileListener(dir, new FileListener() {
            //当文件改变时 将触发此方法,重新刷新配置
            @Override
            public void onFileChange(File file) {
                try {
                    log.debug("fileListener.onFileChange...");
                    InputStream inputStream = new FileInputStream(file);
                    refresh(inputStream);
                } catch (Exception e) {
                    log.debug("fail to execute file change because of:{}", e);
                }

            }

            @Override
            public void onFileCreate(File file) {

            }

            @Override
            public void onFileDelete(File file) {

            }

            @Override
            public String name() {
                return "fileListener";
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public boolean accept() {
                return false;
            }
        });
        fileMonitor.start();

    }


    public void refresh() {
        log.debug("refresh->start to load persionalized config start...");
        try {
            refresh(ResourceUtils.getResourceAsStream("lcpersionalized.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("fail to refresh because of:{}", e);
        }
        log.debug("refresh->start to load persionalized config end....");
    }


    private void refresh(InputStream in) {
        log.debug("refresh->start to load lcpersionalized.properties config start...");
        Properties props = new Properties();
        try {
            props.load(in);
            for (Map.Entry entry : props.entrySet()) {
                iniMap.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (IOException e) {
            log.debug("fail to load because :{}", e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {

            }
        }
        log.debug("refresh->start to load lcpersionalized.properties config end....");
    }


    /**
     * @param name  配置项的名，不支持空值
     * @return 配置项的值
     */
    public String getIniValue(String name) {
        if (Strings.isBlank(name)) return null;
        return iniMap.get(name);
    }


}
