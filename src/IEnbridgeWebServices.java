
/************************************************************************************************************************
 * Filename: IEnbridgeWebServices.java
 * SVN: JVS\Libraries
 *
 * Description: This is the base class that external web services calls (Gets, post, connection) must be used.
 *
 *  REVISION HISTORY:
 * ---date-----   ---author--  ---revision details-----------------------------------------------------------------------
 * 2018-Mar-23     Y.Shorman    Created
 *
 ************************************************************************************************************************
 */

/**
 * The IEnbridgeWebServices class is the base call for all Plugins written
 * at Enbridge to use any calls to external prices web services. It extends the OpenJVS IEnbridgeScript interface but
 * to use all Enb library functions that all JVS use.
 *
 * @author shormany
 */
package com.enb.libraries;

import com.olf.openjvs.OException;
import com.olf.openjvs.Table;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;


public class IEnbridgeWebServices extends IEnbridgeScript
{
	public static final int PJM_SOURCE = 1;
	public static final int AESO_SOURCE = 2;
	public static final int IESO_SOURCE = 3;
	public static final int MISO_SOURCE = 4;
	public static final int NYISO_SOURCE = 5;

	protected static String strCaKeystoreType;
	protected static String strCaKeystorePath;
	protected static String strCaKeystorePass;
	protected static String strClientKeystoreType;
	protected static String strClientKeystorePath;
	protected static String strClientKeystorePass;

	protected static int intOperation;

	public IEnbridgeWebServices()
	{
		super("IEnbridgeWebServices");
	}

	// Kevin:
	// Assume that all parameters that you need are in the p_tblArguments table passed to main.
	public void main(Table p_tblArguments, Table p_tblReturn) throws OException
	{
		intOperation = p_tblArguments.getInt("operation", 1);
		strCaKeystoreType = p_tblArguments.getString("ca_keystore_type", 1);
        strCaKeystorePath = p_tblArguments.getString("ca_keystore_path", 1);
        strCaKeystorePass = p_tblArguments.getString("ca_keystore_pass", 1);
        strClientKeystoreType = p_tblArguments.getString("client_keystore_type", 1);
        strClientKeystorePath = p_tblArguments.getString("client_keystore_path", 1);
        strClientKeystorePass = p_tblArguments.getString("client_keystore_pass", 1);
	}

	public CloseableHttpClient getHttpClient() throws Exception
	{
		return buildClient();
	}

	// Kevin add all your code (Methods, classed here)
	public CloseableHttpClient buildClient() throws Exception
    {
        HttpClientBuilder builder = HttpClientBuilder.create();
        SSLConnectionSocketFactory sslConnectionFactory = getSocket();

        builder.setSSLSocketFactory(sslConnectionFactory);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("https", sslConnectionFactory)
            .register("http", new PlainConnectionSocketFactory())
            .build();

       PoolingHttpClientConnectionManager cm = (socketFactoryRegistry != null) ?
            new PoolingHttpClientConnectionManager(socketFactoryRegistry):
            new PoolingHttpClientConnectionManager();

        builder.setConnectionManager(cm).setDefaultCookieStore(new BasicCookieStore());

        // create and return httpClient
        return builder.build();
	}

	protected SSLConnectionSocketFactory getSocket() throws Exception
    {
        // load the key store, containing SERVER TRUST certificates
        TrustManager[] trustManagers = getTrustManagers(strCaKeystoreType, new FileInputStream(new File(strCaKeystorePath)), strCaKeystorePass);

        // load the key store, containing CLIENT TRUST certificates
        KeyManager[] keyManagers = getKeyManagers(strClientKeystoreType, new FileInputStream(new File(strClientKeystorePath)), strClientKeystorePass);

        // Create an SSL context with our private key store, We load the key-material and the trust-material
    	SSLContext ctx = SSLContext.getInstance("TLS");
    	ctx.init(keyManagers, trustManagers, new SecureRandom());
    	return new SSLConnectionSocketFactory(ctx, new DefaultHostnameVerifier());
    }

	protected KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword) throws Exception
	{
    	KeyStore keyStore = KeyStore.getInstance(keyStoreType);
    	keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
    	KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    	kmf.init(keyStore, keyStorePassword.toCharArray());
    	return kmf.getKeyManagers();
    }

    protected TrustManager[] getTrustManagers(String trustStoreType, InputStream trustStoreFile, String trustStorePassword) throws Exception
    {
		KeyStore trustStore = KeyStore.getInstance(trustStoreType);
    	trustStore.load(trustStoreFile, trustStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf.getTrustManagers();
    }
}
