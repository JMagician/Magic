package com.magician.tools.properties.load;

import com.magician.tools.properties.cache.PropertiesCacheManager;
import com.magician.tools.properties.enums.ReadMode;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

/**
 * 读取properties文件类
 */
public class LoadProperties {

    /**
     * 加载properties文件里的键值对到本地缓存中
     * @param path
     * @param readMode
     * @param charset
     * @throws Exception
     */
    public void load(String path, ReadMode readMode, String charset) throws Exception {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStream = getFileContent(path, readMode);
            inputStreamReader = new InputStreamReader(inputStream, charset);

            Properties p = new Properties();
            p.load(inputStreamReader);

            for(Object key : p.keySet()){
                String keyStr = key.toString();
                String value = p.getProperty(keyStr);
                PropertiesCacheManager.add(keyStr, value);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if(inputStreamReader != null){
                inputStreamReader.close();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
    }

    /**
     * 将properties文件读取到InputStream
     * @param path
     * @param readMode
     * @return
     * @throws Exception
     */
    private InputStream getFileContent(String path, ReadMode readMode) throws Exception {
        switch (readMode) {
            case RESOURCE:
                return LoadProperties.class.getResourceAsStream(path);
            case LOCAL:
                return new FileInputStream(path);
            case REMOTE:
                return new URL(path).openConnection().getInputStream();
        }
        return null;
    }
}
