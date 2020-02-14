package lc.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import lc.entity.SysVersion;
import lc.dao.updateSql.DbUpdateDao;
import lc.service.updateSqlScript.DbUpdateService;
import lc.util.FileExtFilter;
import lc.util.RedisUtil;
import lc.util.UnicodeBOMInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.CannotReadScriptException;
import org.springframework.jdbc.datasource.init.ScriptStatementFailedException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.datasource.init.UncategorizedScriptException;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * @author liuchaoOvO on 2019/4/1
 */

@Service
public class DbUpdateServiceImpl implements DbUpdateService
{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *sql语句分隔符
     */
    private final String DEFAULT_STATEMENT_SEPARATOR = ";";
    /**
     *换行符号
     */
    private final String FALLBACK_STATEMENT_SEPARATOR = "\n";
    /**
     * 行注释标识
     */
    private final String DEFAULT_COMMENT_PREFIX = "--";
    /**
     * 批量注释开始符号
     */
    private  final String DEFAULT_BLOCK_COMMENT_START_DELIMITER = "/*";
     /**
     * 批量注释结束符号
     */
    private final String DEFAULT_BLOCK_COMMENT_END_DELIMITER = "*//*";
    /**
     *  接收通过递归方式获取到的中间目录的全路径
     */
    private String StaticfullPath="";
     /**
     * 脚本路径
     */
    private final String scriptPath = "classpath:db";
     /**
     * 数据库连接
     */
    private Connection conn;
    /**
     * 分布式 redis锁：防止集群环境下并发问题
     * redisKey 格式：年度：区划：业务具体意义名
     */
    private  volatile String  redisKey="2019:1234:doSqlScript";
    /**
     * 过期时间
     */
    private final Long timeout = 60L;
    @Autowired
    private VersionService versionService;
    @Autowired
    private DruidDataSource druidDataSource;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DbUpdateDao dbUpdateDao;
    /**
     * 存储过程类型的脚本执行程序
     */
    private ScriptRunner runner;
    @Override
    public void doSqlScript() throws Exception
    {
        try{
            conn = druidDataSource.getConnection();
            runner = getScriptRunner();
            // 设置手动提交
            conn.setAutoCommit(false);
            // 首先判断是否有其他服务正在执行升级脚本
            if (redisUtil.exists(redisKey)) {
               logger.info("其他服务正在执行升级，请稍后尝试");
                return;
            }else {
            redisUtil.set(redisKey, "1", timeout);
            }
            // 脚本目录不存在直接跳出
            try {
                 ResourceUtils.getURL(scriptPath);
            } catch (FileNotFoundException e) {
                logger.info("脚本目录不存在");
            return;
            }
            List<String> fileList = versionService.getFileMap(conn);
            URL url = ResourceUtils.getURL(scriptPath);
            List<String> firstPaths = sortPath(getFolder(url.toURI()));
            for (String firstPath : firstPaths) {
            firstPath = "/" + firstPath;
            //flag标识 用于判断是否用新方式执行初始化init下的文件夹文件
            boolean flag=false;
            try {
                //1,判断是否执行同步和更新脚本的总开关  info.properties文件中取字段excute判断
                String excute=getProperties("excute",firstPath);
                if(excute==null||excute.equalsIgnoreCase("")||!excute.equalsIgnoreCase("1"))
                    continue;
                String no_init_case=getProperties("no_init_case",firstPath);
                if(no_init_case==null||no_init_case.equalsIgnoreCase("")) {
                    flag=true;
                }else {
                    try {
                        String sql = "select * from "+no_init_case+" where 1=2";
                        dbUpdateDao.judgeInitSql(sql);
                        flag=false;
                    }catch (Exception e) {
                        flag=true;
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            String endToDbFile=scriptPath+firstPath+getDbPath();
            //每次执行其他操作前，都先判断子系统_数据库类型.log日志文件大小，是否删除对应的子系统_数据库类型.log日志文件。
            try {
                String targetfile =firstPath.substring(1,firstPath.length()) +"_"+getDbPath().substring(1,getDbPath().length())+".log";
                deleteErrorSqlLogFile(targetfile);
            }catch(Exception e) {
                if(e instanceof FileNotFoundException) {
                    System.out.println(endToDbFile+"路径下没有记录sql执行错误的log文件");
                }
                else {
                    System.out.println("deleteErrorSqlLogFile 方法报错。");
                }
            }
            //递归执行endToDbFile文件夹下到init或update中的所有文件，是否执行init下的文件 用flag标识位判断。是否用老方法执行init 用oldmethodInit判断。
            RecursiveExecution(endToDbFile,firstPath,endToDbFile,fileList,flag);
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            logger.error(errorMessage);
        } finally {
             redisUtil.remove(redisKey);
             runner.closeConnection();
        try{
             if (null != conn){
                 conn.close();
                 conn=null;
            }
        }catch(Exception ignore){
             logger.info("数据库连接未关闭！");
        }
        }
    }

    /**
     * 获取一级目录文件夹
     * @param  path
     * @return
     * @throws Exception
     */

    private List<String> getFolder(URI path) throws Exception {
        List<String> folders = new ArrayList<>();
        File srcDir = new File(path);
        if (!srcDir.exists()) {
            throw new Exception("目录" + path.getPath() + "不存在！");
        } else if (!srcDir.isDirectory()) {
            throw new Exception(path.getPath() + "不是目录！");
        }
        for (File folder : srcDir.listFiles()) {
            if (folder.isDirectory()) {
                folders.add(folder.getName());
            }
        }
        return folders;
    }

    /**
     * 根据配置文件设置ScriptRunner
     *
     * @return
     * @throws Exception
     */

    private ScriptRunner getScriptRunner() throws Exception {

        ScriptRunner runner = new ScriptRunner(conn);
        // 这个设置为false是为了能够识别plsql的代码块，否则无法执行pl/sql的脚本，只能执行正常的sql语句。
        runner.setEscapeProcessing(false);
        // 这个设置为true是为了读取脚本所有语句，否则会按行读取进行执行，导致脚本解析错误，无法正常执行分行的begin end代码块。
        runner.setSendFullScript(true);
        runner.setStopOnError(false);
        runner.setErrorLogWriter(null);
        runner.setLogWriter(null);
        return runner;
    }
    /**
     * 先执行fap、ap产品目录，然后按顺序执行其他产品目录
     */

    private List<String> sortPath(List<String> firstPaths) {

        if (firstPaths.contains("ap")) {
            firstPaths.remove("ap");
            firstPaths.add(0, "ap");
        }
        if (firstPaths.contains("portal")) {
            firstPaths.remove("portal");
            firstPaths.add(0, "portal");
        }
        if (firstPaths.contains("fap")) {
            firstPaths.remove("fap");
            firstPaths.add(0, "fap");
        }
        return firstPaths;

    }
     /**
     * 执行目录下的脚本
     *
     * @param
     */

    private void executeScript(String firstPath, String thirdPath, String fourthPath, Boolean isSp, List<String> fileList) {
        try {
            // 目录不存在直接跳出
            try {
                ResourceUtils.getURL(scriptPath + firstPath + getDbPath() + thirdPath + fourthPath);
            } catch (Exception e) {
                return;
            }
            String targetfile =firstPath.substring(1,firstPath.length()) +"_"+getDbPath().substring(1,getDbPath().length())+".log";
            URL url = ResourceUtils.getURL(scriptPath + firstPath + getDbPath() + thirdPath + fourthPath);
            File[] files = readSricpts(url.toURI());
            fileSort(files);
            for (File file : files) {
                String fileName = firstPath + thirdPath + fourthPath + "/" + file.getName();
                if (!fileList.contains(fileName)) {
                    SysVersion version = new SysVersion();
                    version.setSqlFileName(fileName);
                    version.setSubsyscode(firstPath.substring(1,firstPath.length()));
                    try (InputStream inputStream = new FileInputStream(file);
                         // 处理有签名格式的脚本
                         UnicodeBOMInputStream bomInputStream = new UnicodeBOMInputStream(inputStream);
                         OutputStream outStream = new ByteArrayOutputStream();
                         PrintWriter writer = new PrintWriter(outStream)) {
                        String encoding = UniversalDetector.detectCharset(file);
                        encoding = (encoding != null && encoding.indexOf("UTF") > -1) ? "UTF-8" : "GBK";
                        if (!isSp) {
                            String name=file.getPath();
                            InputStream in = new FileInputStream(name);
                            String result=executeSqlScript(conn,new EncodedResource(new InputStreamResource(in), encoding),fileName,targetfile);
                            conn.commit();
                            version.setRemark(result);
                            logger.debug(fileName + result);
                        } else {
                            // 跳过文件头的bom信息
                            bomInputStream.skipBOM();
                            runner.setErrorLogWriter(writer);
                            runner.runScript(new InputStreamReader(bomInputStream, encoding));
                            conn.commit();
                            String info = outStream.toString().trim();
                            if (StringUtils.isEmpty(info)) {
                                version.setRemark("执行成功");
                            } else
                                throw new Exception(info);
                        }
                    } catch (Exception e) {
                        conn.rollback();
                        String errorMessage = e.getMessage();
                        logger.error(fileName + "执行失败:" + errorMessage);
                        if (errorMessage.length() > 1000) {
                            errorMessage = errorMessage.substring(0, 500)
                                    + errorMessage.substring(errorMessage.length() - 500);
                        }
                        version.setRemark("执行失败:" + errorMessage);
                    }
                    version.setExecTime(getCurTime());
                    versionService.saveSysVersion(version,conn);
                }
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            logger.error(errorMessage);
            return;
        }
    }

     /**
     * 执行大字段（z_lob）目录下的脚本 需要解析文件名称 ：物理表名称、字段名、关键字
      *
      *  1、大文本文件命名规范：日期和顺序号;物理表名称;字段名;关键字1=条件值1;关键字2=条件值2.clob
      *  例如：20180809;AGCFS_DW_FILE;c_lob;SET_YEAR=2018;RG_CODE=87;ID=12345667788900.clob
      *  2、大二进制文件命名规范：日期和顺序号;物理表名称;字段名;关键字1=条件值1;关键字2=条件值2.blob
      *  例如：20180809;AGCFS_DW_FILE;b_lob;ID=12345667788900;DATA_ID=123232323232323.blob
     */

    private void executeLobScript(String firstPath, String thirdPath, String fourthPath, List<String> fileList) {
        try {
            try {
                ResourceUtils.getURL(scriptPath + firstPath + getDbPath() + thirdPath + fourthPath);
            } catch (Exception e) {
                return;
            }
            URL url = ResourceUtils.getURL(scriptPath + firstPath + getDbPath() + thirdPath + fourthPath);
            File[] files = readLobSricpts(url.toURI());
            fileSort(files);

            for (File file : files) {

                String fileName = firstPath + thirdPath + fourthPath + '/' + file.getName();
                if (!fileList.contains(fileName)) {
                    SysVersion version = new SysVersion();
                    version.setSqlFileName(fileName);
                    try {
                        // 解析文件名
                        String lobName = StringUtils.substringBefore(file.getName(), ".");
                        String lobType = StringUtils.substringAfter(file.getName(), ".");
                        String dateOrder = StringUtils.substringBefore(lobName, ";");
                        String tableName = StringUtils.substringBefore(StringUtils.substringAfter(lobName, ";"), ";");
                        String fieldName = StringUtils
                                .substringBefore(lobName.replace(dateOrder + ";" + tableName + ";", ""), ";");
                        String keyWords = lobName.replace(dateOrder + ";" + tableName + ";" + fieldName + ";", "");
                        // 拼写sql语句
                        String sqlWhere = keyWords.replaceAll("=", "='").replaceAll(";", "' and ");
                        String sql = "select " + fieldName + " from " + tableName + " where " + sqlWhere + "' for update";
                        Statement st = conn.createStatement();
                        ResultSet rs = st.executeQuery(sql);

                        if (rs.next()) {
                            if (lobType.equals("blob")) {
                                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                                OutputStream out = rs.getBlob(1).setBinaryStream(1L);
                                int c;
                                // 将实际文件中的内容以二进制的形式来输出到blob对象对应的输出流中
                                while ((c = in.read()) != -1) {
                                    out.write(c);
                                }
                                in.close();
                                out.close();
                            } else {
                                String encoding = UniversalDetector.detectCharset(file);
                                encoding = (encoding != null && encoding.indexOf("UTF") > -1) ? "UTF-8" : "GBK";

                                InputStreamReader in = new InputStreamReader(new FileInputStream(file), encoding);
                                Writer out = rs.getClob(1).setCharacterStream(1L);
                                int c;
                                while ((c = in.read()) != -1) {
                                    out.write(c);
                                }
                                in.close();
                                out.close();
                            }
                            version.setRemark("执行成功");
                        } else {
                            version.setRemark("执行失败：数据库中未找到对应记录");
                        }
                        conn.commit();
                        rs.close();
                        st.close();
                    } catch (Exception e) {
                        conn.rollback();
                        String errorMessage = e.getMessage();
                        logger.error(fileName + "执行失败:" + errorMessage);
                        if (errorMessage.length() > 1000) {
                            errorMessage = errorMessage.substring(0, 500)
                                    + errorMessage.substring(errorMessage.length() - 500);
                        }
                        version.setRemark("执行失败:" + errorMessage);
                    }
                    version.setExecTime(getCurTime());
                    versionService.saveSysVersion(version,conn);
                }
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            logger.error(errorMessage);
            return;
        }
    }


/**
     * 根据数据库类型获取二级目录
     */

    private String getDbPath() {
        String path = "";
        if(path!=null)
        {
        path = "/mysql";
        }
        return path;
    }

/**
     * 读sql文件列表
     *
     * @param path
     * @return
     * @throws Exception
     */

    private File[] readSricpts(URI path) throws Exception {
        File srcDir = new File(path);
        if (!srcDir.exists()) {
            throw new Exception("目录" + path.getPath() + "不存在！");
        } else if (!srcDir.isDirectory()) {
            throw new Exception(path.getPath() + "不是目录！");
        }

        return srcDir.listFiles(new FileExtFilter(".sql"));
    }

/**
     * 插入排序算法对文件进行排序
     *
     * @param files
     */

    private void fileSort(File[] files) {
        int i, j;
        int n = files.length;
        File target;

        for (i = 1; i < n; i++) {
            j = i;
            target = files[i];

            while (j > 0 && target.getName().compareToIgnoreCase(files[j - 1].getName()) < 0) {
                files[j] = files[j - 1];
                j--;
            }

            files[j] = target;
        }
    }
    //lc  递归执行
    private void RecursiveExecution(String  urlversionfile,String firstPath,String endToDbFile,List<String> fileList,Boolean flag) throws URISyntaxException, Exception {
        URL url = ResourceUtils.getURL(urlversionfile);
        List<String> paths =getFolder(url.toURI());
        for(String path:paths) {
            String fullPath="";
            path="/"+path;
            if(path.equals("/init")&&flag==true) {
                StaticfullPath=urlversionfile;
                String partPath=StaticfullPath.substring(endToDbFile.length(),StaticfullPath.length());

                executeScript(firstPath, partPath+"/init", "/a_table", false, fileList);
                executeScript(firstPath, partPath+"/init", "/b_view", false, fileList);
                executeScript(firstPath, partPath+"/init", "/c_sp", true, fileList);
                executeScript(firstPath, partPath+"/init", "/d_insert", false, fileList);
                executeLobScript(firstPath, partPath+"/init", "/z_lob", fileList);
            }
            if(path.equals("/update")){
                StaticfullPath=urlversionfile;
                String partPath=StaticfullPath.substring(endToDbFile.length(),StaticfullPath.length());

                executeScript(firstPath, partPath+"/update", "/a_table", false, fileList);
                executeScript(firstPath, partPath+"/update", "/b_view", false, fileList);
                executeScript(firstPath, partPath+"/update", "/c_sp", true, fileList);
                executeScript(firstPath, partPath+"/update", "/d_insert", false, fileList);
                executeLobScript(firstPath, partPath+"/update", "/z_lob", fileList);
                continue;
            }
            if(!path.equals("/init")&&!path.equals("/update")) {
                fullPath=fullPath+urlversionfile+path;
                RecursiveExecution(fullPath,firstPath,endToDbFile,fileList,flag);
            }
        }
    }
    //lc  删除sql执行错误的自定义日志文件
    private void deleteErrorSqlLogFile(String targetfile) throws FileNotFoundException, URISyntaxException
    {
        File file=null;
        String url = ResourceUtils.getURL("classpath:").toString();
        url=url+"log/"+targetfile;
        file=new File(new URI(url));
        File fileParent = file.getParentFile();
        //判断是否存在
        if (!fileParent.exists()) {
            //创建父目录文件
            fileParent.mkdirs();
        }
        //如果文件大小大于1024*1024*5则删除掉这个文件，防止文件过大
        if (file.exists()&&file.length()>1024*1024*5)
            file.delete();
    }
    //lc
    private String getProperties(String key,String firstPath) {
        String val = "";
        Properties prop = new Properties();
        try {
            // 装载配置文件
            prop.load(this.getClass().getResourceAsStream("/db"+firstPath+"/info.properties"));
            val = prop.getProperty(key);
            if (val == null) {
                val = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回获取的值
        return val;
    }


    /**
     * 获取当前时间
     */

    private String getCurTime () {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return df.format(new Date());
            }
    private String readScript (EncodedResource resource, String commentPrefix, String separator)throws IOException {
                LineNumberReader lnr = new LineNumberReader(resource.getReader());
                try
                {
                    return ScriptUtils.readScript(lnr, commentPrefix, separator);
                } finally
                {
                    lnr.close();
                }
            }
    public static String buildErrorMessage (String stmt,int stmtNumber, EncodedResource encodedResource){
                return String.format("Failed to execute SQL script statement #%s of %s: %s", stmtNumber, encodedResource, stmt);
            }

/**
 * 读lob文件列表
 *
 * @param  path
 * @return
 * @throws Exception
 */

    private File[] readLobSricpts (URI path) throws Exception {
                File srcDir = new File(path);
                if (!srcDir.exists())
                {
                    throw new Exception("目录" + path.getPath() + "不存在！");
                } else if (!srcDir.isDirectory())
                {
                    throw new Exception(path.getPath() + "不是目录！");
                }

                return srcDir.listFiles(new FileExtFilter("lob"));
            }
    private String executeSqlScript(Connection connection, EncodedResource resource,String fileName,String targetfile) throws ScriptException {
        String result="执行成功";
        String separator = DEFAULT_STATEMENT_SEPARATOR;
        String errSQLExcepStr="";
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Executing SQL script from " + resource);
            }
            long startTime = System.currentTimeMillis();

            String script;
            try {
                script = readScript(resource, DEFAULT_COMMENT_PREFIX, separator);
            }
            catch (IOException ex) {
                throw new CannotReadScriptException(resource, ex);
            }


            if (!ScriptUtils.containsSqlScriptDelimiters(script, separator)) {
                separator = FALLBACK_STATEMENT_SEPARATOR;
            }

            List<String> statements = new LinkedList<String>();
            ScriptUtils.splitSqlScript(resource, script, separator, DEFAULT_COMMENT_PREFIX, DEFAULT_BLOCK_COMMENT_START_DELIMITER,
                    DEFAULT_BLOCK_COMMENT_END_DELIMITER, statements);

            int stmtNumber = 0;
            //记录插入数据的记录条数，保持10条数据更新一次redis的redisKey = "UFGOV_INSTALL"的过期时间
            int countNum=0;
            Statement stmt = connection.createStatement();
            try {
                for (String statement : statements) {
                    stmtNumber++;
                    countNum++;
//                    if(countNum>10) {
//                        if (!redisUtil.exist(redisKey)) {
//                            redisUtil.put(redisKey, "1");
//                        }
//                        redisUtil.expire(redisKey, timeout, TimeUnit.SECONDS);
//                        countNum=0;
//                    }
                    try {
                        stmt.execute(statement);
                        int rowsAffected = stmt.getUpdateCount();
                        if (logger.isDebugEnabled()) {
                            logger.debug(rowsAffected + " returned as update count for SQL: " + statement);
                            SQLWarning warningToLog = stmt.getWarnings();
                            while (warningToLog != null) {
                                logger.debug("SQLWarning ignored: SQL state '" + warningToLog.getSQLState() +
                                        "', error code '" + warningToLog.getErrorCode() +
                                        "', message [" + warningToLog.getMessage() + "]");
                                warningToLog = warningToLog.getNextWarning();
                            }
                        }
                        conn.commit();
                    }
                    catch (SQLException ex) {
                        conn.rollback();
                        errSQLExcepStr="#文件名为:"+fileName+"中的第["+stmtNumber+"]条记录:"+statement+"存在异常错误:"+ex.getMessage();
                        logErrorSqlExc(errSQLExcepStr,targetfile);

                        logger.error(ScriptStatementFailedException.buildErrorMessage(statement, stmtNumber, resource), ex);
                        result="执行失败："+ex.getMessage();
                    }
                }
            }
            finally {
                try {
                    stmt.close();
                }
                catch (Throwable ex) {
                    logger.debug("Could not close JDBC Statement", ex);
                }
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            if (logger.isInfoEnabled()) {
                logger.info("Executed SQL script from " + resource + " in " + elapsedTime + " ms.");
            }
        }
        catch (Exception ex) {
            if (ex instanceof ScriptException) {
                throw (ScriptException) ex;
            }
            throw new UncategorizedScriptException(
                    "Failed to execute database script from resource [" + resource + "]", ex);
        }
        return result;
    }

    //记录错误日志信息到文件。
    private void logErrorSqlExc(String Str,String targetfile) throws IOException {
        SimpleDateFormat dFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DataOutputStream outputStream  = null;
        OutputStream outputStream1=null;
        File file=null;
        Str=dFormat.format(new Date())+Str;
        try {
            String url = ResourceUtils.getURL("classpath:").toString();
            url=url+"log/"+targetfile;
            file=new File(new URI(url));
            File fileParent = file.getParentFile();
            //判断是否存在
            if (!fileParent.exists()) {
                //创建父目录文件
                fileParent.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();

		/* file=new File(new URI(targetfile1));
			if (!file.exists())
				file.createNewFile();
			*/
            //文件追加的方式写入文件
            outputStream1 = new FileOutputStream(file,true);
            outputStream = new DataOutputStream(outputStream1);
            int len = Str.getBytes().length;
            //判断长度是否大于1M
            if (len <= 1024 * 1024) {
                byte[] bytes = new byte[len];
                outputStream.write(Str.getBytes(),0,len);
                //文件换行
                outputStream.write("\r\n".getBytes());
            } else {
                int byteCount = 0;
                //1M逐个读取
                byte[] bytes = new byte[1024*1024];
                bytes=Str.getBytes();
                while ((byteCount = Str.getBytes().length) != -1){
                    outputStream.write(bytes, 0, byteCount);
                    //文件换行
                    outputStream.write("\r\n".getBytes());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            outputStream.flush();
            outputStream.close();

            outputStream1.flush();
            outputStream1.close();
        }
    }
}

