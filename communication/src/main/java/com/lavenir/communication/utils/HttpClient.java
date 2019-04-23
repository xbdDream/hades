package com.lavenir.communication.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求工具类
 */
public class HttpClient {

    /**
     * post基本请求
     * @param url
     * @param map
     * @param encoding
     * @return
     */
    public static String sendPost(String url, Map<String,String> map, String encoding){
        String resBody = null;
        CloseableHttpResponse response = null;

        try{
            //创建client对象
            CloseableHttpClient client = HttpClients.createDefault();

            //创建post请求对象
            HttpPost post = new HttpPost(url);

            //封装参数
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            if(map != null){
                for(Map.Entry<String, String> entry : map.entrySet()){
                    parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }

            //设置参数到对象中
            post.setEntity(new UrlEncodedFormEntity(parameters,encoding));

            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
            post.setHeader("Content-type", "application/x-www-form-urlencoded");
            post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //执行请求，获取响应结果
            response = client.execute(post);

            //获得结果，转为String
            HttpEntity entity = response.getEntity();
            if(entity != null){
                resBody = EntityUtils.toString(entity, encoding);
            }

            //关闭流
            EntityUtils.consume(entity);
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            //释放链接
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("响应结果:" + resBody);
        return resBody;
    }

    /**
     * https绕过ssl验证请求
     * @param url
     * @param map
     * @param encoding
     * @return
     */
    public static String sendSslPost(String url, Map<String,String> map, String encoding){
        String resBody = null;
        CloseableHttpResponse response = null;

        try{
            //采用绕过验证的方式处理https请求
            SSLContext sslcontext = createIgnoreVerifySSL();

            // 设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);

            //创建自定义的httpclient对象
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();

            //创建post请求对象
            HttpPost post = new HttpPost(url);

            //封装参数
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            if(map != null){
                for(Map.Entry<String, String> entry : map.entrySet()){
                    parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }

            //设置参数到对象中
            post.setEntity(new UrlEncodedFormEntity(parameters,encoding));

            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
            post.setHeader("Content-type", "application/x-www-form-urlencoded");
            post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //执行请求，获取响应结果
            response = client.execute(post);

            //获得结果，转为String
            HttpEntity entity = response.getEntity();
            if(entity != null){
                resBody = EntityUtils.toString(entity, encoding);
            }

            //关闭流
            EntityUtils.consume(entity);
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            //释放链接
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resBody;
    }

    /**
     * 绕过SSL验证
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }

    /**
     * 设置ssl证书请求https
     * @param url
     * @param map
     * @param encoding
     * @param sslcontext
     * @return
     */
    public static String sendSslPost(String url, Map<String,String> map, String encoding, SSLContext sslcontext){
        String resBody = null;
        CloseableHttpResponse response = null;

        try{
            //如果密码为空，则用"nopassword"代替
//            sslcontext = custom("D:\\keys\\wsriakey", "tomcat");

            // 设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);

            //创建自定义的httpclient对象
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();

            //创建post请求对象
            HttpPost post = new HttpPost(url);

            //封装参数
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            if(map != null){
                for(Map.Entry<String, String> entry : map.entrySet()){
                    parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }

            //设置参数到对象中
            post.setEntity(new UrlEncodedFormEntity(parameters,encoding));

            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
            post.setHeader("Content-type", "application/x-www-form-urlencoded");
            post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //执行请求，获取响应结果
            response = client.execute(post);

            //获得结果，转为String
            HttpEntity entity = response.getEntity();
            if(entity != null){
                resBody = EntityUtils.toString(entity, encoding);
            }

            //关闭流
            EntityUtils.consume(entity);
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            //释放链接
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resBody;
    }

    /**
     * 设置信任自签名证书
     *
     * @param keyStorePath        密钥库路径
     * @param keyStorepass        密钥库密码
     * @return
     */
    public static SSLContext custom(String keyStorePath, String keyStorepass){
        SSLContext sc = null;
        FileInputStream instream = null;
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            instream = new FileInputStream(new File(keyStorePath));
            trustStore.load(instream, keyStorepass.toCharArray());
            // 相信自己的CA和所有自签名的证书
            sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
        } catch (KeyStoreException | NoSuchAlgorithmException| CertificateException | IOException | KeyManagementException e) {
            e.printStackTrace();
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
        return sc;
    }

    public static void main(String []args){
        String url = "http://test.jkp.intradak.cn/sst-tcgl/api/terminal/fileUpload.json?terminalCode=123123";
        Map<String, String> map = new HashMap<>();
        HttpClient.sendPost(url,map, "utf-8");

    }

}
