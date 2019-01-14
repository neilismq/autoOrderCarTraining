package com.bj.zzq.utils;

import com.sun.deploy.net.cookie.CookieUnavailableException;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/11
 * @Description:
 */
public class HttpUtils {
    private static String cookie = "";
    //学车不登录地址
    public static String loginUrl = "https://api.xuechebu.com/usercenter/userinfo/login";
    //龙泉驾校登录地址
    public static String longquanjiaxiaoLoginUrl = "http://longquanapi.xuechebu.com/Student/setbadingstuinfo";
    //查询是否有号
    public static String orderQueryUrl = "http://longquanapi.xuechebu.com/KM2/ClYyTimeSectionUIQuery2";
    //获取用户信息
    public static String userInfoUrl = "http://longquanapi.xuechebu.com/Student/StudyInfo";
    //预约
    public static String orderUrl = "http://longquanapi.xuechebu.com/KM2/ClYyAddByMutil";

    //请求工具
    public static String doHttp(String method, String url, HashMap<String, String> headers, HashMap<String, String> params) throws URISyntaxException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        CloseableHttpClient httpclient = null;
        if (url.startsWith("https")) {
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            TrustStrategy trustStrategy = new TrustStrategy() {

                public boolean isTrusted(X509Certificate[] x509Certificates, String s) {
                    return true;
                }
            };
            SSLContext sslContext = sslContextBuilder.loadTrustMaterial(trustStrategy).build();
            SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext);
            httpclient = HttpClients.custom().setSSLSocketFactory(factory).build();
        } else {
            //默认http客户端，毫秒级
            httpclient = HttpClients.createDefault();
        }

        //生成url
        URIBuilder builder = null;
        try {
            builder = new URIBuilder(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        //添加参数
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addParameter(key, params.get(key));
            }
        }

        URI uri = builder.build();

        // 创建http请求
        HttpRequestBase httpRequestBase = null;
        if ("GET".equalsIgnoreCase(method)) {
            httpRequestBase = new HttpGet(uri);
        } else if ("POST".equalsIgnoreCase(method)) {
            httpRequestBase = new HttpPost(uri);
        }

        //添加header
        if (headers != null) {
            for (String name : headers.keySet()) {
                httpRequestBase.addHeader(name, headers.get(name));
            }
        }
        httpRequestBase.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");

        if (!"".equals(cookie)) {
            httpRequestBase.addHeader("Cookie", cookie);
        }

        //设置代理,方便查看请求
//        HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
//        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
//        httpRequestBase.setConfig(config);

        // 执行请求
        CloseableHttpResponse response = httpclient.execute(httpRequestBase);
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");

        // 如果是登录请求，获取cookie
        if (loginUrl.equals(url) || longquanjiaxiaoLoginUrl.equals(url)) {
            Header[] allHeaders = response.getAllHeaders();

            for (Header header : allHeaders) {
                String name = header.getName();
                if ("Set-Cookie".equals(name)) {
                    String value = header.getValue();
                    value = value.substring(0, value.indexOf(";") + 1);
                    cookie += " " + value;
                }
            }
            if (longquanjiaxiaoLoginUrl.equals(url)) {
                cookie = cookie.substring(0, cookie.length() - 1);
            }
        }
        System.out.println("地址：" + url + "返回结果：");
        System.out.println(result);
        return result;
    }

    public static void addJsonpParams(Map map) {
        map.put("ISJSONP", "true");
        map.put("os", "pc");
        map.put("callback", "jQuery19103597663931350108_1547188429681");
        map.put("_", "1547188429686");
    }
}
