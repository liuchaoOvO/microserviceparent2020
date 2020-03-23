package lc.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 按扩展名过滤
 *
 * @author liuchaoOvO
 */
public class FileExtFilter implements FilenameFilter {
    private String fileExt;

    public FileExtFilter(String fileExt) {
        this.fileExt = fileExt;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(fileExt);
    }
}
