package lc.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuchaoOvO on 2019/4/1
 */
public class DataSourceTools
{

        /**
         * 获取当前连接池信息
         * @param dsName
         * @return
         */
        public static DruidDataSource getDataSource(String dsName){
            DruidDataSource ds = null ;
            for(DruidDataSource datasource : DruidDataSourceStatManager.getDruidDataSourceInstances()){
                if(dsName.equals(datasource.getName())){
                    ds = datasource ;
                    break;
                }
            }
            return ds ;
        }

        /**
         * 获取当前连接池信息
         * @param dsName
         * @return
         */
        public static Map<String,Object> getDataSourceStat(String dsName){
            DruidDataSource ds = getDataSource(dsName) ;
            return ds!=null ? ds.getStatData() : new HashMap<String , Object>() ;
        }



        /**
         * 关闭数据库连接池
         * @param dsName
         */
        public static void closeDataSource(String dsName){
            getDataSource(dsName).close() ;
        }



}
