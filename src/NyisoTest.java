package org.enb.iso;

import com.enb.utilities.UTIL_PowerHttpConnect;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.security.SecureRandom;
import java.security.KeyStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class NyIsoTest
{
    private static final String savePath = "C:\\Users\\edwardk3\\PortableApps\\LivITy\\.babun\\cygwin\\home\\edwardk3\\workspace\\jiso-tool\\data\\nyiso\\";

    public static void main(String[] args) throws Exception
    {
        CloseableHttpClient c = UTIL_PowerHttpConnect.getHttpClient("nyiso");

        try {
            // request report listing
            HttpGet httpget = new HttpGet("https://dss.nyiso.com/dss/login.jsp?user=louckda2&pass=NYFiles2018&automated=2");

            HttpResponse response = c.execute(httpget);
            HttpEntity entity = response.getEntity();
            BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

            Map<String, String> urisToGetHash = getConfig(new HashMap<String, String>(), br);

            EntityUtils.consume(entity);

            // create a thread for each URI report to download
            GetThread[] threads = new GetThread[urisToGetHash.size()];

            Integer i = 0;
            for (Entry<String, String> entry : urisToGetHash.entrySet())
            {
                httpget = new HttpGet(entry.getValue());
                threads[i] = new GetThread(c, httpget, savePath + entry.getKey() + ".csv");
                i++;
            }

            // start the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].start();
            }

            // join the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].join();
            }
        } finally {
            c.close();
        }
    }

    private static Map<String, String> getConfig(Map<String, String> uriMap, BufferedReader br) throws Exception
    {
        String line = null;
        while((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            uriMap.put(parts[1], "https://dss.nyiso.com/dss/docViewAGN.jsp?RepoType=I&ID=" + parts[0] + "&DocName=" + parts[1] + "&DocType=csv");
        }
        return uriMap;
    }

    protected static class GetThread extends Thread
    {
        private final CloseableHttpClient httpClient;
        private final HttpGet httpget;
        private String fileSavePath;

        public GetThread(CloseableHttpClient httpClient, HttpGet httpget, String fileSavePath)
        {
            this.httpClient = httpClient;
            this.httpget = httpget;
            this.fileSavePath = fileSavePath;
        }

        @Override
        public void run()
        {
            File fileUri = new File(fileSavePath);

            try {
                CloseableHttpResponse response = httpClient.execute(httpget);
                try {
                    HttpEntity entity = response.getEntity();

                    System.out.println(EntityUtils.toString(entity));
                    // InputStream is = entity.getContent();
                    // FileOutputStream fos = new FileOutputStream(fileUri);
                    // int inByte;
                    // while((inByte = is.read()) != -1)
                    //     fos.write(inByte);
                    // is.close();
                    // fos.close();
                } finally {
                    response.close();
                }
            } catch (ClientProtocolException ex) {
                // Handle protocol errors
                System.out.println("protocol error: " + ex.getMessage());
            } catch (IOException ex) {
                // Handle I/O errors
                System.out.println("io ERROR: " + ex.getMessage());
            }
        }
    }
}
