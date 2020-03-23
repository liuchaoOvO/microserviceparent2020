package lc.dao.updateSql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.sql.Connection;
import java.util.List;
import java.util.Map;


/**
 * @author liuchaoOvO on 2019/4/1
 */

@Mapper
public interface DbUpdateDao {
    @Select ("select sql_file_name  from SYS_INSTALL_SQL")
    List<Map<String, String>> getFileMap(Connection conn);

    void judgeInitSql(String sql);
}

