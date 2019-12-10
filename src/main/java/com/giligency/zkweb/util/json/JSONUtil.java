package com.giligency.zkweb.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JSONUtil {
    private JSONUtil() {

    }

    /**
     * 将JSON数据格式化并保存到文件中
     *
     * @param jsonData 需要输出的json数
     * @param filePath 输出的文件地址
     * @return 创建文件是否成功
     */
    public static boolean createJsonFile(Object jsonData, String filePath) {

        String content = JSON.toJSONString(jsonData, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        // 标记文件生成是否成功
        boolean flag = true;
        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(filePath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            write.write(content);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            log.error("创建文件{}失败", filePath, e);
        }
        return flag;
    }

    /**
     * 读取json文件，返回json串
     *
     * @param fileName
     * @return
     */
    public static String readJsonFile(String fileName) {

        File jsonFile = new File(fileName);
        return readJsonFile(jsonFile);
    }

    public static String readJsonFile(File jsonFile) {
        String jsonStr = "";
        try {
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
