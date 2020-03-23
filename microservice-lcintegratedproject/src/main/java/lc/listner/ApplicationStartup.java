/*
package lc.listner;

import lc.config.InitThreadLoadPage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

*/
/**
 * @author liuchaoOvO on 2019/4/22
 * @description 应用刷新启动执行事件方法
 *//*

public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent>
{
      @Override
      public void onApplicationEvent(ContextRefreshedEvent event) {
          ApplicationContext ac = event.getApplicationContext();
          InitThreadLoadPage StepExecutor = ac.getBean(InitThreadLoadPage.class);
          Thread thread = new Thread(StepExecutor);
          thread.start();
      }

}
*/
