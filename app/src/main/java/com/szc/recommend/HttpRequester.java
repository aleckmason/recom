package com.szc.recommend;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Vector;

public class HttpRequester {
    public static interface Callback {
        public void onResult(String content);
    }


    private String defaultContentEncoding;
      
    public HttpRequester() {     
      this.defaultContentEncoding = Charset.defaultCharset().name();
    }     
      

    public HttpRespons sendGet(String urlString) throws IOException {
        return this.send(urlString, "GET", null, null);     
    }     
      

    public HttpRespons sendGet(String urlString, Map<String, String> params)
            throws IOException {
        return this.send(urlString, "GET", params, null);     
    }     
      

    public HttpRespons sendGet(String urlString, Map<String, String> params,
                                                  Map<String, String> propertys) throws IOException {
        return this.send(urlString, "GET", params, propertys);     
    }     
      

    public HttpRespons sendPost(String urlString) throws IOException {
        return this.send(urlString, "POST", null, null);     
    }     
      

    public HttpRespons sendPost(String urlString, Map<String, String> params, Callback callback)
            throws IOException {
        return this.send(urlString, "POST", params, null);
    }     
      

    public HttpRespons sendPost(String urlString, Map<String, String> params,
                                                   Map<String, String> propertys, Callback callback) throws IOException {
        return this.send(urlString, "POST", params, propertys,callback);
    }


    private HttpRespons send(String urlString, String method,
                                                Map<String, String> parameters, Map<String, String> propertys, Callback callback) throws IOException {
        HttpURLConnection urlConnection = null;

        if (method.equalsIgnoreCase("GET") && parameters != null) {
            StringBuffer param = new StringBuffer();
            int i = 0;
            for (String key : parameters.keySet()) {
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");
                param.append(key).append("=").append(parameters.get(key));
                i++;
            }
            urlString += param;
        }
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod(method);
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);

        if (propertys != null)
            for (String key : propertys.keySet()) {
                urlConnection.addRequestProperty(key, propertys.get(key));
            }


        if (method.equalsIgnoreCase("POST") && parameters != null) {
            StringBuffer param = new StringBuffer();
            for (String key : parameters.keySet()) {
                param.append("&");
                param.append(key).append("=").append(parameters.get(key));
            }
            urlConnection.getOutputStream().write(param.toString().getBytes());
            urlConnection.getOutputStream().flush();
            urlConnection.getOutputStream().close();
        }
        HttpRespons resp = makeContent(urlString, urlConnection);
        if(callback != null) {
            callback.onResult(resp.content);
        }

        return resp;

    }

    private HttpRespons send(String urlString, String method,
                                                Map<String, String> parameters, Map<String, String> propertys)
            throws IOException {
      return send(urlString,method,parameters,propertys,null);
    }     
     

    public static String sendHttpClientPost(String httpPath, String parameters)throws IOException {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(httpPath);
            // 设置POST参数
            if (parameters != null) {
                StringEntity se = new StringEntity(parameters, "UTF-8");
                se.setContentType("application/json"); 
                post.setEntity(se);
            }
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instreams = entity.getContent();
                String str = convertStreamToString(instreams);
                post.abort();
                return str;
            }
        } catch (IOException e) {
        }
        return null;
    }
    /**
     * 将响应的数据流转换成字符串
     *
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
      

    private HttpRespons makeContent(String urlString,
                                                       HttpURLConnection urlConnection) throws IOException {
        HttpRespons httpResponser = new HttpRespons();     
        try {     
            InputStream in = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in,"utf-8"));
            httpResponser.contentCollection = new Vector<String>();
            StringBuffer temp = new StringBuffer();
            String line = bufferedReader.readLine();
            while (line != null) {     
                httpResponser.contentCollection.add(line);     
                temp.append(line).append("\r\n");     
                line = bufferedReader.readLine();     
            }     
            bufferedReader.close();     
      
            String ecod = urlConnection.getContentEncoding();
            if (ecod == null)     
                ecod = this.defaultContentEncoding;     
      
            httpResponser.urlString = urlString;     
      
            httpResponser.defaultPort = urlConnection.getURL().getDefaultPort();     
            httpResponser.file = urlConnection.getURL().getFile();     
            httpResponser.host = urlConnection.getURL().getHost();     
            httpResponser.path = urlConnection.getURL().getPath();     
            httpResponser.port = urlConnection.getURL().getPort();     
            httpResponser.protocol = urlConnection.getURL().getProtocol();     
            httpResponser.query = urlConnection.getURL().getQuery();     
            httpResponser.ref = urlConnection.getURL().getRef();     
            httpResponser.userInfo = urlConnection.getURL().getUserInfo();     
      
            httpResponser.content = new String(temp.toString().getBytes(), ecod);
            httpResponser.contentEncoding = ecod;     
            httpResponser.code = urlConnection.getResponseCode();     
            httpResponser.message = urlConnection.getResponseMessage();     
            httpResponser.contentType = urlConnection.getContentType();     
            httpResponser.method = urlConnection.getRequestMethod();     
            httpResponser.connectTimeout = urlConnection.getConnectTimeout();     
            httpResponser.readTimeout = urlConnection.getReadTimeout();     
      
            return httpResponser;     
        } catch (IOException e) {
            throw e;     
        } finally {     
            if (urlConnection != null)     
                urlConnection.disconnect();     
        }     
    }     
      

    public String getDefaultContentEncoding() {
        return this.defaultContentEncoding;     
    }     
      

    public void setDefaultContentEncoding(String defaultContentEncoding) {
        this.defaultContentEncoding = defaultContentEncoding;     
    }     
}  