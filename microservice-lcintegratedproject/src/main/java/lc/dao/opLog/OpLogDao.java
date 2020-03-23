package lc.dao.opLog;

import lc.entity.OpLogDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liuchaoOvO on 2019/4/11
 */
@Mapper
public interface OpLogDao {
    boolean saveOpLogToDB(OpLogDto logDto);
}
