/*
package lc.multidatasourceconfig.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.impl.SessionFactoryImpl;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

*/
/**
 * @author liuchaoOvO on 2019/10/30
 * HikariDataSource
 * 用于改造jdbc、数据源、多数据源 用外部配置中心配置的方式
 *//*

@Slf4j
@Configuration
@EnableConfigurationProperties (YYDataSourceConfig.class)
public class CoreConfiguration implements InitializingBean, ApplicationContextAware {
    @Autowired
    @Qualifier ("YYDataSourceConfig")
    private YYDataSourceConfig YYDataSourceConfig;
    //用于存储单个BasicDataSource
    public static volatile ConcurrentHashMap<String, HikariDataSource> dataSourceMap = new ConcurrentHashMap();
    //用于存储MultiDataSource
    public static volatile ConcurrentHashMap<String, MultiDataSource> mulDataSourceMap = new ConcurrentHashMap();
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        log.debug(">>>multi region hikari datasource init begin----------");
        log.debug(">>>multi region hikari datasource【通过外部配置的方式创建数据源 ---  start】");
        AnnotationConfigEmbeddedWebApplicationContext ctx = (AnnotationConfigEmbeddedWebApplicationContext) applicationContext;
        List<DatasourceProp> list = YYDataSourceConfig.getDatasources();
        for (DatasourceProp datasourceProp : list) {
            log.debug(">>>multi region hikari datasource【[==数据源的datasourceProp参数信息id:{},username:{},jdbcUrl:{}==]】", datasourceProp.getId(), datasourceProp.getUsername(), datasourceProp.getJdbcUrl());
        }

        MultiDataSourceProp multiDataSourceProp = YYDataSourceConfig.getMultiDataSource();
        log.debug(">>>multi region hikari datasource【multiDataSourceProp参数信息:{}】", multiDataSourceProp.toString());
        //构建每一个HikariDataSource数据源BeanDefinition
        for (int i = 0, len = list.size(); i < len; i++) {
            String beanId = list.get(i).getId();
            HikariConfig hikariConfig = list.get(i);

            //创建HikariDataSource BeanDefinition
            BeanDefinitionBuilder b = BeanDefinitionBuilder.rootBeanDefinition(HikariDataSource.class)
                    .addConstructorArgValue(hikariConfig);
                   */
/*.addPropertyValue("username", hikariConfig.getUsername())
                    .addPropertyValue("password", hikariConfig.getPassword())
                    .addPropertyValue("jdbcUrl", hikariConfig.getJdbcUrl())
                    .addPropertyValue("driverClassName", hikariConfig.getDriverClassName());*//*

            if (i == 0) {
                //设置优先级 防止getBean返回多个
                b.getBeanDefinition().setPrimary(true);
            }
            ctx.registerBeanDefinition(beanId, b.getBeanDefinition());
            HikariDataSource dataSource = (HikariDataSource) ctx.getBean(beanId);

            log.debug(">>>datasource info---【dataSource.Username:{},dataSource.Url:{},dataSource.ConnectTimeOut:{},dataSource.IdleTimeout:{},dataSource.MaxLifetime:{},dataSource.MaximumPoolSize:{},dataSource.MinimumIdle:{}】",
                    dataSource.getUsername(), dataSource.getJdbcUrl(), dataSource.getConnectionTimeout(), dataSource.getIdleTimeout()
                    , dataSource.getMaxLifetime(), dataSource.getMaximumPoolSize(), dataSource.getMinimumIdle());

            dataSourceMap.put(beanId, dataSource);
            log.debug(">>>multi region hikari datasource【获取的数据源beanId:{}，dataSource.userName:{}，dataSource.URL:{}】", beanId, dataSource.getUsername(), dataSource.getJdbcUrl());
        }

        //构建多数据源BeanDefinition
        String id = multiDataSourceProp.getId();
        String dbType = multiDataSourceProp.getDbType();
        String defaultDataSource = multiDataSourceProp.getDefaultDataSource();
        HikariDataSource defaultHikariDataSource = (HikariDataSource) dataSourceMap.get(defaultDataSource);
        log.debug(">>>multi region hikari datasource【dataSourceMap中get的defaultDataSource.userName:{},defaultDataSource.URL:{}】", defaultHikariDataSource.getUsername(), defaultHikariDataSource.getJdbcUrl());
        List<YYDataSource> listYYDataSource = multiDataSourceProp.getDataSources();
        Map<String, HikariDataSource> muldataSources = new LinkedHashMap();
        for (YYDataSource yyDataSource : listYYDataSource) {
            if (dataSourceMap.get(yyDataSource.getBeanId()) != null) {
                muldataSources.put(yyDataSource.getKey(), (HikariDataSource) dataSourceMap.get(yyDataSource.getBeanId()));
            }
        }
        for (Map.Entry muldataSource : muldataSources.entrySet()) {
            log.debug(">>>multi region hikari datasource【muldataSource--KEY:{},muldataSource.userName:{},muldataSource.Url:{}】", muldataSource.getKey(), ((HikariDataSource) muldataSource.getValue()).getUsername(), ((HikariDataSource) muldataSource.getValue()).getJdbcUrl());
        }

        BeanDefinitionBuilder b = BeanDefinitionBuilder.rootBeanDefinition(MultiDataSource.class)
                .addPropertyValue("dbType", dbType)
                .addPropertyValue("defaultDataSource", defaultHikariDataSource)
                .addPropertyValue("dataSources", muldataSources);
        ctx.registerBeanDefinition(id, b.getBeanDefinition());
        MultiDataSource multiDataSource = (MultiDataSource) ctx.getBean(id);
        mulDataSourceMap.put(id, multiDataSource);

        Iterator<Map.Entry> it = multiDataSource.getDataSources().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, HikariDataSource> entry = (Map.Entry) it.next();
            log.debug(">>>multi region hikari datasource【获取的多数据源beanId:{}，每个dataSource-Key:{},dataSource-userName:{},dataSource-Url:{}】", id, entry.getKey(), ((HikariDataSource) entry.getValue()).getUsername(), ((HikariDataSource) entry.getValue()).getJdbcUrl());
        }
        log.debug(">>>multi region hikari datasource=====registerSessionFactory===");
        registerSessionFactory(ctx, multiDataSource);
        SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) ctx.getBean("mysessionFactory");
        log.debug("通过bean 反射取到sessionFactoryImpl:{}===", sessionFactoryImpl);
        log.debug(">>>multi region hikari datasource=====registerHibernateTemplate===");
        registerHibernateTemplate(ctx, sessionFactoryImpl);
        log.debug(">>>multi region hikari datasource=====registerHibernateTransManager===");
        registerHibernateTransManager(ctx, sessionFactoryImpl);
        log.debug(">>>multi region hikari datasource=====registerJdbcTemplate===");
        registerJdbcTemplate(ctx, defaultHikariDataSource);
        log.debug(">>>multi region hikari datasource=====registerSqlSessionFactory===");
        registerSqlSessionFactory(ctx, defaultHikariDataSource);
        log.debug(">>>multi region hikari datasource=====registerSqlSessionTemplate===");
        registerSqlSessionTemplate(ctx, defaultHikariDataSource);
        log.debug(">>>multi region hikari datasource【通过外部配置的方式创建数据源 ---  end】");
    }

    private void registerSessionFactory(AnnotationConfigEmbeddedWebApplicationContext ctx, MultiDataSource multiDataSource) {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle9Dialect");
        hibernateProperties.setProperty("hibernate.show_sql", "false");
        hibernateProperties.setProperty("hibernate.c3p0.min_size", "10");
        hibernateProperties.setProperty("hibernate.c3p0.max_size", "20");
        hibernateProperties.setProperty("hibernate.c3p0.max_statements", "100");
        hibernateProperties.setProperty("hibernate.c3p0.idle_test_period", "3000");
        hibernateProperties.setProperty("hibernate.c3p0.acquire_increment", "2");
        hibernateProperties.setProperty("hibernate.c3p0.timeout", "10000");
        hibernateProperties.setProperty("hibernate.jdbc.fetch_size", "150");
        hibernateProperties.setProperty("hibernate.jdbc.batch_size", "20");
        hibernateProperties.setProperty("hibernate.cache.use_query_cache", "false");
        hibernateProperties.setProperty("hibernate.query.factory_class", "org.hibernate.hql.classic.ClassicQueryTranslatorFactory");
        hibernateProperties.setProperty("javax.persistence.validation.mode", "none");
        BeanDefinitionBuilder b = BeanDefinitionBuilder.rootBeanDefinition(LocalSessionFactoryBean.class)
                .addPropertyValue("dataSource", multiDataSource)
                .addPropertyValue("hibernateProperties", hibernateProperties);
        ctx.registerBeanDefinition("mysessionFactory", b.getBeanDefinition());
    }

    private void registerHibernateTemplate(AnnotationConfigEmbeddedWebApplicationContext ctx, SessionFactoryImpl sessionFactory) {
        BeanDefinitionBuilder b = BeanDefinitionBuilder.rootBeanDefinition(GovHibernateTemplate.class)
                .addPropertyValue("sessionFactory", sessionFactory);
        ctx.registerBeanDefinition("hibernateTemplate", b.getBeanDefinition());
    }

    private void registerSqlSessionFactory(AnnotationConfigEmbeddedWebApplicationContext ctx, HikariDataSource dataSource) {
        BeanDefinitionBuilder b = BeanDefinitionBuilder.rootBeanDefinition(SqlSessionFactoryBean.class)
                .addPropertyValue("dataSource", dataSource)
                .addPropertyValue("mapperLocations", "classpath:gov/df/fap/service/portal/sqlMap/*.xml");
        ctx.registerBeanDefinition("sqlSessionFactory", b.getBeanDefinition());
    }

    private void registerSqlSessionTemplate(AnnotationConfigEmbeddedWebApplicationContext ctx, HikariDataSource dataSource) {
        BeanDefinitionBuilder b = BeanDefinitionBuilder.rootBeanDefinition(SqlSessionTemplate.class)
                .addConstructorArgReference("sqlSessionFactory");
        ctx.registerBeanDefinition("sqlSessionTemplate", b.getBeanDefinition());
    }

    private void registerJdbcTemplate(AnnotationConfigEmbeddedWebApplicationContext ctx, HikariDataSource dataSource) {
        BeanDefinitionBuilder b = BeanDefinitionBuilder.rootBeanDefinition(JdbcTemplate.class)
                .addPropertyValue("dataSource", dataSource);
        ctx.registerBeanDefinition("jdbcTemplate", b.getBeanDefinition());
    }

    private void registerHibernateTransManager(AnnotationConfigEmbeddedWebApplicationContext ctx, SessionFactoryImpl sessionFactory) {
        BeanDefinitionBuilder b = BeanDefinitionBuilder.rootBeanDefinition(HibernateTransactionManager.class)
                .addPropertyValue("sessionFactory", sessionFactory);
        ctx.registerBeanDefinition("hibernateTransManager", b.getBeanDefinition());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static void setDataSourceMap(ConcurrentHashMap dataSourceMap) {
        CoreConfiguration.dataSourceMap = dataSourceMap;
    }

    public static void setMulDataSourceMap(ConcurrentHashMap mulDataSourceMap) {
        CoreConfiguration.mulDataSourceMap = mulDataSourceMap;
    }

    public ConcurrentHashMap getDataSourceMap() {
        return dataSourceMap;
    }

    public ConcurrentHashMap getMulDataSourceMap() {
        return mulDataSourceMap;
    }
}




*/
