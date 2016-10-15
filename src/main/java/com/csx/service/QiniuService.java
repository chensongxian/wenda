package com.csx.service;

import com.alibaba.fastjson.JSONObject;
import com.csx.controller.HomeController;
import com.csx.util.WendaUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.Base64;
import com.qiniu.util.StringMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by csx on 2016/7/10.
 */
@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    //设置好账号的ACCESS_KEY和SECRET_KEY
    String ACCESS_KEY = "YXFHOMGZBx1ZpO-4Zrunh3PLf5IYlnjfJy9HNmK-";
    String SECRET_KEY = "MlFWgAreqir3vDR5GgPSwDhlTjWWHgwoKadee1Ya";
    //要上传的空间
    String bucketname = "toutiao";
    //上传到七牛后保存的文件名
    String key = "my-java.png";
    //上传文件的路径
//    String FilePath = "/.../...";

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    //创建上传对象
    UploadManager uploadManager = new UploadManager();

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        return auth.uploadToken(bucketname);
    }

    //base64上传
    public String getBase64UpToken() {
        return auth.uploadToken(bucketname, null, 3600, new StringMap().put("insertOnly", 1));
    }

    public String upload(MultipartFile file) throws IOException {
        try {
            int doPos = file.getOriginalFilename().lastIndexOf(".");
            if (doPos < 0) {
                return null;
            }

            String fileExt = file.getOriginalFilename().substring(doPos + 1).toLowerCase();

            if (!WendaUtil.isFileAllowed(fileExt)) {
                return null;
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + "." + fileExt;
            //调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());
            //打印返回的信息
            System.out.println(res.bodyString());
            if (res.isOK() && res.isJson()) {
                String key = JSONObject.parseObject(res.bodyString()).get("key").toString();
                return WendaUtil.QINIU_DOMAIN_PREFIX + key;
            } else {
                logger.error("上传出错" + res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            logger.error("七牛异常" + e.getMessage());
            return null;
        }
    }

    public String uploadBase64(String base64) throws IOException {
        try {
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            //文件大小
            int l = bytes.length;
            //构建url
            String url = "http://up.qiniu.com/putb64/" + l;

            //构造post对象
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", "application/octet-stream");
            post.addHeader("Authorization", "UpToken " + getBase64UpToken());
            post.setEntity(new StringEntity(base64));

            //请求与响应
            HttpClient c = HttpClientBuilder.create().build();
            HttpResponse res = c.execute(post);


            //打印返回的信息
            //输出
            System.out.println(res.getStatusLine());
            String responseBody = EntityUtils.toString(res.getEntity(), "UTF-8");
            System.out.println(responseBody);
            if (res.getStatusLine().getStatusCode()==200) {
                String key = JSONObject.parseObject(responseBody).get("key").toString();
                return WendaUtil.QINIU_DOMAIN_PREFIX + key;
            } else {
                logger.error("上传出错" + responseBody);
                return null;
            }
        } catch (Exception e) {
            logger.error("七牛异常" + e.getMessage());
            return null;
        }
    }



}
