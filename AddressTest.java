package com.imooc.demo.demo;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class AddressTest {
    private static final Logger logger = LoggerFactory.getLogger(AddressTest.class);
    //高德的key
    private static final String key = "";
    //固定请求地址
    private static final String APIURL = "https://restapi.amap.com/v3/geocode/regeo?";
    public static void main(String[] args) {
　　　　　//高德经纬度
        String addByAMAP = getAddByAMAP("109.842599,27.21797");
        logger.info("result:"+addByAMAP);
    }
    public static String getAddByAMAP(String log){
        StringBuffer s = new StringBuffer();
        s.append("output=json&").append("key=").append(key).append("&location=").append(log);
        String res = sendPost(APIURL,s.toString());
        String province = JSONObject.fromObject(JSONObject.fromObject(JSONObject.fromObject(res).get("regeocode")).get("addressComponent")).optString("province").toString();
        String city = JSONObject.fromObject(JSONObject.fromObject(JSONObject.fromObject(res).get("regeocode")).get("addressComponent")).optString("city").toString();
        logger.info("province:"+province);
        logger.info("city:"+city);
        if ("[]".equals(city)){
            return province;
        }
        return city;
    }
    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}