
/***********************************************************************************************************************************************
 *$URL$
 *$Id$
 *
  * Description:
 *
 *  REVISION HISTORY:
 * ---Date---    ---Author--   ---Revision Details----------------------------------------------------------------------------------------------
 * 12-Mar-2018   Shormany      Issue#00000: Initial creation;
 *
 *
 ***********************************************************************************************************************************************
 */
package com.enb.utilities.power;

import java.io.*;
import java.net.URI;

import org.apache.http.*;
import org.apache.http.util.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.*;
import org.apache.http.client.methods.*;
import org.apache.commons.io.FileUtils;

import com.olf.openjvs.*;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.enb.libraries.IEnbridgeScript;

import org.json.*;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

/**
 * @author Shormany
 */

public class UTIL_PowerISOConnect_M extends IEnbridgeScript
{
	public static final int XML_SOURCE_FILE = 1;
	public static final int JSON_SOURCE_FILE = 2;

	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	// to be removed when creating parameter script.
	public String METADATA = "";
//	public String SUBSCRIPTION_KEY = "";
//	public String FILE_PATH = "";

	public static void createArgumentsTable(Table p_tblArgs) throws OException
    {
        p_tblArgs.clearRows();

        p_tblArgs.addCol("file_path", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("request_url", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("subscription_key", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("content_type", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("source_file_format", COL_TYPE_ENUM.COL_INT);
        p_tblArgs.addCol("parameters", COL_TYPE_ENUM.COL_TABLE);
    }

	@Override
	public void main(Table p_tblArguments, Table p_tblReturn)
	{
//		METADATA  = "<FeedSearchResult>";
//		METADATA += "    <links>";
//		METADATA += "        <rel>string</rel>";
//		METADATA += "        <href>string</href>";
//		METADATA += "    </links>";
//		METADATA += "    <items />";
		METADATA += "    <searchSpecification>";
		METADATA += "    {";
		METADATA += "      rowCount: 1000";
		//METADATA += "      'startRow': 1,";
		//METADATA += "      'isActiveMetadata': true,";
		//METADATA += "      'fields': [],";
		//METADATA += "      'filters': []";
		METADATA += "    }";
		METADATA += "    </searchSpecification>";
//		METADATA += "    <totalRows>0</totalRows>";
//		METADATA += "</FeedSearchResult>";

		try
		{
			startReport(p_tblArguments);
		}
		catch (OException e)
		{
			print(e.getMessage());
		}
		catch (Exception e)
		{
			print(e.getMessage());
		}
	}

	int startReport(Table p_tblArguments) throws OException, Exception
	{
		String strFilePath = "";
		String strRequestUrl = "";
		String strContentType = "";
		String strSubscriptionKey = "";

		int intResult = 0;
		int intSourceFileFormat = 0;

		if (p_tblArguments.getColNum("file_path") > 0)
			strFilePath = p_tblArguments.getString("file_path", 1);

		if (p_tblArguments.getColNum("request_url") > 0)
			strRequestUrl = p_tblArguments.getString("request_url", 1);

		if (p_tblArguments.getColNum("content_type") > 0)
			strContentType = p_tblArguments.getString("content_type", 1);

		if (p_tblArguments.getColNum("subscription_key") > 0)
			strSubscriptionKey = p_tblArguments.getString("subscription_key", 1);

		if (p_tblArguments.getColNum("source_file_format") > 0)
			intSourceFileFormat = p_tblArguments.getInt("source_file_format", 1);

		// Check for invalid parameters
		if ( (strFilePath.length() > 0) && (strRequestUrl.length() > 0) && (strContentType.length() > 0) && (strSubscriptionKey.length() > 0) )
		{
			HttpEntity heEntity = null;


			heEntity = getHTTPResponseEntity(strRequestUrl, strSubscriptionKey, strContentType);
			if (heEntity != null)
	        {
				String strData = "";

//				InputStream isData = heEntity.getContent();
//				StringWriter writer = new StringWriter();
//				IOUtils.copy(isData, writer, "UTF-8");//
//				strData = XML_HEADER + writer.toString();

				strData = EntityUtils.toString(heEntity);
				print(strData);

	        	if (intSourceFileFormat == XML_SOURCE_FILE)
	        	{
	        		strData = XML_HEADER + strData;
	        		createXMLCsvFile(strData, strFilePath);
	        	}
	        	else if (intSourceFileFormat == JSON_SOURCE_FILE)
	        		createJSonCsvFile(strData, strFilePath);
	        }
	        else
	        {
	        	print("Can't retrieve HttpEntity from PJM.");
	        }
		}
		else
		{
			print ("Ivalid arguments parameters.");
		}


		return intResult;
	}

	public static CloseableHttpClient getHTTPClient(String[] args) throws Exception
    {
        // load the key store, containing SERVER TRUST certificates
        TrustManager[] trustManagers = getTrustManagers(MisoTest.CA_KEYSTORE_TYPE, new FileInputStream(new File(MisoTest.CA_KEYSTORE_PATH)), MisoTest.CA_KEYSTORE_PASS);

        // load the key store, containing CLIENT TRUST certificates
        KeyManager[] keyManagers = getKeyManagers(MisoTest.CLIENT_KEYSTORE_TYPE, new FileInputStream(new File(MisoTest.CLIENT_KEYSTORE_PATH)), MisoTest.CLIENT_KEYSTORE_PASS);

        // Create an SSL context with our private key store, We load the key-material and the trust-material
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(keyManagers, trustManagers, new SecureRandom());

        // Prepare the HTTPClient builder.
        HttpClientBuilder builder = HttpClientBuilder.create();
        SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(ctx, new DefaultHostnameVerifier());
        builder.setSSLSocketFactory(sslConnectionFactory);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("https", sslConnectionFactory)
            .register("http", new PlainConnectionSocketFactory())
            .build();

        PoolingHttpClientConnectionManager cm = (socketFactoryRegistry != null) ?
            new PoolingHttpClientConnectionManager(socketFactoryRegistry):
            new PoolingHttpClientConnectionManager();

        builder.setConnectionManager(cm).setDefaultCookieStore(new BasicCookieStore());

        // create and return
        return builder.build();
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
}
