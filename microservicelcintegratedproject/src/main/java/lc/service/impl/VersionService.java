package lc.service.impl;

import lc.dao.updateSql.DbUpdateDao;
import lc.entity.SysVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class VersionService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DbUpdateDao dbUpdateDao;

    public List<String> getFileMap(Connection conn) throws SQLException {
        List<String> fileList = new ArrayList<>();
        try {
            List<Map<String, String>> resultList = dbUpdateDao.getFileMap(conn);
            for (Map<String, String> result : resultList) {
                fileList.add(result.get("sql_file_name"));
            }
        } catch (Exception e) {
            try {
                // 表不存在时就先创建
                String createTableSql = "create table SYS_INSTALL_SQL( ID varchar(36) not null, SQL_FILE_NAME varchar(1000), EXEC_TIME varchar(20), REMARK varchar(4000),subsyscode VARCHAR(64),primary key (ID) );";
                PreparedStatement ps = conn.prepareStatement(createTableSql);
                Boolean result = ps.execute();
                ps.close();
                conn.commit();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                conn.rollback();
            }
        }
        return fileList;

    }

    public void saveSysVersion(SysVersion sysVersion, Connection conn) throws SQLException {
        try {
            String sql = "insert into SYS_INSTALL_SQL (id,sql_file_name, exec_time, remark,subsyscode) values(?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, UUID.randomUUID().toString());
            ps.setObject(2, sysVersion.getSqlFileName());
            ps.setObject(3, sysVersion.getExecTime());
            ps.setObject(4, sysVersion.getRemark());
            ps.setObject(5, sysVersion.getSubsyscode());
            Boolean result = ps.execute();
            ps.close();
            conn.commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
            conn.rollback();
        }

    }

    /*
     * 判断是否执行init目录下的脚本
     * @param  path
     * @return
     * @throws Exception
     */

    public boolean isInit(String path) {
        try {
            ResourceUtils.getURL(path + "/no_init.sql");
        } catch (Exception e) {
            return true;
        }

        try {
            URL url = ResourceUtils.getURL(path + "/no_init.sql");
            File file = new File(url.toURI());
            InputStream inputStream = new FileInputStream(file);
            try {
                byte[] bytes = new byte[0];
                bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                String tableName = new String(bytes);
                String sql = "select * from " + tableName.trim() + " where 1=2";
                //dao.findBySql(sql);
                dbUpdateDao.judgeInitSql(sql);
                return true;
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {
            return false;
        }
    }

}
