/***********************************************************************************************************************************************
 *$URL$
 *$Id$
 *
  * Description:
 *
 *  REVISION HISTORY:
 * ---Date---    ---Author--   ---Revision Details----------------------------------------------------------------------------------------------
 * 22-Mar-2018   kedwards      Issue#00000: Initial creation;
 *
 *
 ***********************************************************************************************************************************************
 */
package com.enb.utilities;

// import com.enb.libraries.IEnbridgeScript;
// import com.olf.openjvs.OException;
// import com.olf.openjvs.Table;
// import com.olf.openjvs.enums.COL_TYPE_ENUM;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

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

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class UTIL_PowerHttpConnect
{
    private static Logger logger = Logger.getLogger(UTIL_PowerHttpConnect.class);

    private static final String CA_KEYSTORE_TYPE = KeyStore.getDefaultType();
    private static final String CA_KEYSTORE_PATH = "C:\\java\\jdk1.8.0_152\\jre\\lib\\security\\cacerts";
    private static final String CA_KEYSTORE_PASS = "changeit";

    private static final String CLIENT_KEYSTORE_TYPE = "PKCS12";
    private static final String CLIENT_KEYSTORE_PATH = "C:\\Users\\edwardk3\\PortableApps\\LivITy\\.babun\\cygwin\\home\\edwardk3\\workspace\\jiso-tool\\cert\\mrm-oati-cert.pfx";
    private static final String CLIENT_KEYSTORE_PASS = "MRMiso2018";

	public static CloseableHttpClient getHttpClient(String iso) throws Exception
    {
        BasicConfigurator.configure();
        logger.info("I am Here TODAY - Entering application.");

        HttpClientBuilder builder = HttpClientBuilder.create();
        SSLConnectionSocketFactory sslConnectionFactory = getSocket();

        builder.setSSLSocketFactory(sslConnectionFactory);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("https", sslConnectionFactory)
            .register("http", new PlainConnectionSocketFactory())
            .build();

        switch (iso)
        {
            case "nyiso":
                PoolingHttpClientConnectionManager cm = (socketFactoryRegistry != null) ?
                    new PoolingHttpClientConnectionManager(socketFactoryRegistry):
                    new PoolingHttpClientConnectionManager();

                builder.setConnectionManager(cm).setDefaultCookieStore(new BasicCookieStore());
                break;
            case "miso":
                break;
            case "pjm":
                break;
        }
        // create and return httpClient
        return builder.build();
	}

    public static HttpClientContext getContext(String username, String password)
    {
        HttpHost targetHost = new HttpHost("reports.ieso.ca", 443, "https");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        return context;
    }

	protected static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf.getKeyManagers();
    }

    protected static TrustManager[] getTrustManagers(String trustStoreType, InputStream trustStoreFile, String trustStorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(trustStoreType);
        trustStore.load(trustStoreFile, trustStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf.getTrustManagers();
    }

    protected static SSLConnectionSocketFactory getSocket() throws Exception
    {
        // load the key store, containing SERVER TRUST certificates
        TrustManager[] trustManagers = getTrustManagers(CA_KEYSTORE_TYPE, new FileInputStream(new File(CA_KEYSTORE_PATH)), CA_KEYSTORE_PASS);

        // load the key store, containing CLIENT TRUST certificates
        KeyManager[] keyManagers = getKeyManagers(CLIENT_KEYSTORE_TYPE, new FileInputStream(new File(CLIENT_KEYSTORE_PATH)), CLIENT_KEYSTORE_PASS);

        // Create an SSL context with our private key store, We load the key-material and the trust-material
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(keyManagers, trustManagers, new SecureRandom());

        // Prepare the HTTPClient builder.
        return new SSLConnectionSocketFactory(ctx, new DefaultHostnameVerifier());
    }
}
