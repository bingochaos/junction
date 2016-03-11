package cn.edu.buaa.nlsde.wlan.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

/**
 * FileUtil
 *
 * @author gaojie
 */
public class FileUtil {

    public static final String SDK_CHARSET = "utf-8";
    public static final Charset SDK_CHARSET_O = Charset.forName(SDK_CHARSET);

    public static List<String> readFile(File file) throws IOException {
        return Files.readLines(file, SDK_CHARSET_O);
    }

    public static String readContent(File file) throws IOException {

        return Files.readLines(file, SDK_CHARSET_O, new LineProcessor<String>() {
            private StringBuilder sb = new StringBuilder();

            @Override
            public boolean processLine(String line)  throws IOException {
                sb.append(line);
                return true;
            }

            @Override
            public String getResult() {
                return sb.toString();
            }
        });
    }

    public static boolean exist(File file) {
        return file != null && file.exists();
    }

}
