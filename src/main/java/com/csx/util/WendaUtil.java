package com.csx.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

/**
 * Created by csx on 2016/7/3.
 */
public class WendaUtil {
    private static final Logger logger = LoggerFactory.getLogger(WendaUtil.class);

    public static int ANONYMOUS_USERID = 3;
    public static int SYSTEM_USERID = 4;

    public static String IMAGE_DIR="G:\\idea\\wenda\\upload\\";
    public static String[] IMAGE_FILE_EXT=new String[]{"png","bmp","gif","jpg","jpeg"};
    public static String TOUTIAO_DOMAIN="http://127.0.0.1:8080/";
    public static String QINIU_DOMAIN_PREFIX="http://oa3u0zrzi.bkt.clouddn.com/";

    public static String getJSONString(int code) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        return json.toJSONString();
    }

//    public static String getJSONString(int code,ViewObject vo) {
//        JSONObject json = new JSONObject();
//        json.put("code", code);
//        json.put("vo",vo);
//        return json.toJSONString();
//    }
    public static String getJSONString(int code, JSONObject vo) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("vo", vo);
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }

    public static String getJSONString(int code, String[] msg) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }

    public static String getJSONString(int code, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toJSONString();
    }

    /**
     * JSon转换
     * @param list
     * @return
     */
    public static String getJSONString(List<JSONObject> list) {
        JSONObject json=new JSONObject();
        if(list.size()<10){
            json.put("hasNext",false);
        }else{
            json.put("hasNext",true);
        }
        json.put("data",list);
        return json.toJSONString();
    }



    public static String getTopicListJson(List<JSONObject> list) {
        JSONObject json=new JSONObject();
        json.put("data",list);
        return json.toJSONString();
    }

    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("生成MD5失败", e);
            return null;
        }
    }



    public static boolean isFileAllowed(String fileExt){
        for(String ext:IMAGE_FILE_EXT){
            if(fileExt.equals(ext)){
                return true;
            }
        }
        return false;
    }
}
