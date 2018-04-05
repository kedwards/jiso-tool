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
import org.apache.http.NoHttpResponseException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.NoHttpResponseException;
import org.apache.http.util.EntityUtils;

public class MisoTest
{
    private static final String savePath = "C:\\Users\\edwardk3\\PortableApps\\LivITy\\.babun\\cygwin\\home\\edwardk3\\workspace\\jiso-tool\\data\\miso\\";

    public static void main(String[] args) throws Exception
    {
        new UTIL_PowerHttpConnect();
        CloseableHttpClient client = UTIL_PowerHttpConnect.getHttpClient("miso");

        try {
            // request report listing
            HttpGet httpget = new HttpGet("https://markets.midwestiso.org/MISO/getSettlementStatementFile?entity=TDL_MP&nodeId=key0");

            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();

            // System.out.println(entity.getContentType());
            System.out.println(EntityUtils.toString(entity));
        } catch(NoHttpResponseException e) {
            System.out.println(e.getMessage());
        } finally {
            client.close();
        }
    }
}
