package lc.service.impl;


import lc.dao.quartz.QuartzTaskErrorsMapper;
import lc.entity.QuartzTaskErrors;
import lc.service.quartz.QuartzTaskErrorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName QuartzTaskErrorsServiceImpl
 * @Description TODO
 * @Author
 * @Date 2019/1/3
 * Version  1.0
 */
@Service
public class QuartzTaskErrorsServiceImpl implements QuartzTaskErrorsService {

    @Autowired
    private QuartzTaskErrorsMapper quartzTaskErrorsMapper;

    @Override
    public Integer addTaskErrorRecord(QuartzTaskErrors quartzTaskErrors) {
        return quartzTaskErrorsMapper.insert(quartzTaskErrors);
    }

    @Override
    public QuartzTaskErrors detailTaskErrors(String recordId) {
        return quartzTaskErrorsMapper.detailTaskErrors(recordId);
    }

}
