/***********************************************************************************************************************************************
 * Filename: UTIL_PullPJMIso_P.java
 * SVN: JVS\Utilities
 *
 * Description: Param Script for PJM ISO File downloads.
 *
 *  REVISION HISTORY:
 * ---Date---    ---Author--   ---Revision Details----------------------------------------------------------------------------------------------
 * 12-Mar-2018   Shormany      Issue#00000: Initial creation;
 *
 ***********************************************************************************************************************************************
 */
package com.enb.utilities;

import com.enb.libraries.IEnbridgeWebServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
//import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
//import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
//import org.apache.http.client.utils.URLEncodedUtils;
//import org.apache.http.NameValuePair;
import org.json.JSONObject;

import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OException;
import com.olf.openjvs.Str;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.DATE_FORMAT;

public class UTIL_PullPowerIso_M extends IEnbridgeWebServices
{
	public void main(Table p_tblArguments, Table p_tblReturn) throws OException 
	{
		super.main(p_tblArguments, p_tblReturn);
		
		int intOperation = p_tblArguments.getInt("operation", 1);
		
		try {
			switch(intOperation) {
				case IEnbridgeWebServices.IESO_SOURCE:
					doIESO(p_tblArguments);
					break;
				case IEnbridgeWebServices.AESO_SOURCE:
					break;
				case IEnbridgeWebServices.PJM_SOURCE:
					doPJM(p_tblArguments);
					break;
				case IEnbridgeWebServices.NYISO_SOURCE:
					doNY(p_tblArguments);
					break;
				case IEnbridgeWebServices.MISO_SOURCE:
					doMiso(p_tblArguments);
					break;
			}
		} catch (Exception e) {
			throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}
	
	private void doMiso(Table p_tblArguments) throws OException
	{
		String strSavePath = p_tblArguments.getString("save_path", 1);
		String strRootUri = p_tblArguments.getString("root_uri", 1);
		
		CloseableHttpClient client = null;
				
		try {
			client = getHttpClient();
			strRootUri = "https://markets.midwestiso.org/MISO/getSettlementStatementFile?entity=TDL&nodeId=key0";
			HttpGet httpget = new HttpGet(strRootUri); // + "getSettlementStatementFile?entity=TDL_MP&nodeId=key0");
//			HttpClientContext context = getContext(strRootUri, p_tblArguments.getString("user", 1), p_tblArguments.getString("pass", 1));	
			CloseableHttpResponse response = client.execute(httpget);
			HttpEntity entity = response.getEntity();
			
			String strFileName = strSavePath +  "\\TDL_MP\\tdl_file.csv";
			
        	writeFile(entity.getContent(), strFileName);
		} catch(Exception e) {
        	print(e.getClass().getSimpleName() + " - " + e.getMessage());
			throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
        } finally {
        	try {
				client.close();
			} catch (IOException e) {
				throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
			}
        }
	}
	
	private void doNY(Table p_tblArguments) throws OException
	{
		String strSavePath = p_tblArguments.getString("save_path", 1);
		String strRootUri = p_tblArguments.getString("root_uri", 1);
		String strDlUri = p_tblArguments.getString("dl_uri", 1);
		String strUser = p_tblArguments.getString("user", 1);
		String strPass = p_tblArguments.getString("pass", 1);
		
		CloseableHttpClient client = null;
		Map<String, String> urisToGetHash = new HashMap<String, String>();
		
//		try {
//			client = getHttpClient();
//			HttpGet httpget = new HttpGet(strRootUri + "user=" + strUser + "&pass=" + strPass + "&automated=2");
//				
//			CloseableHttpResponse response = client.execute(httpget);
//			HttpEntity entity = response.getEntity();
//		
//			BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
//			String line = null;
//						
//			while((line = in.readLine()) != null) {
//				String[] parts = line.split(",");
//				
//				String[] bits = parts[1].split("_");
//				String strFolder = bits[bits.length - 1];
//				
//				strDlUri = "https://dss.nyiso.com/dss/docViewAGN.jsp?RepoType=I&ID=";
//				String strResult = strDlUri + parts[0] + "&DocName=" + parts[1] + "&DocType=csv";
////				https://dss.nyiso.com/dss/docViewAGN.jsp?RepoType=I&ID=XXXX&DocName=XXXXXXXX&entry=&DocType=csv
//				urisToGetHash.put(strFolder + "\\" + parts[1], strResult);
//			}
//						
//			for (Entry<String, String> entry : urisToGetHash.entrySet())
//            {
//            	httpget = new HttpGet(entry.getValue());
//            	
//            	response = client.execute(httpget);
//            	String strFileName = strSavePath +  "\\" + entry.getKey() + ".csv";
//            	writeFile(response.getEntity().getContent(), strFileName);
//            }
//			
//			urisToGetHash.clear();
//			
//			httpget = new HttpGet(strRootUri + "user=" + strUser + "&pass=" + strPass + "&automated=3");
//			
//			response = client.execute(httpget);
//			entity = response.getEntity();
//		
//			in = new BufferedReader(new InputStreamReader(entity.getContent()));
//			line = null;
//						
//			while((line = in.readLine()) != null) {
//				String[] parts = line.split(",");
//				
//				String[] bits = parts[1].split("_");
//				String strFolder = bits[bits.length - 1];
//				
//				strDlUri = "https://dss.nyiso.com/dss/docViewAGN.jsp?RepoType=I&ID=";
//				String strResult = strDlUri + parts[0] + "&DocName=" + parts[1] + "&DocType=csv";
////				https://dss.nyiso.com/dss/docViewAGN.jsp?RepoType=I&ID=XXXX&DocName=XXXXXXXX&entry=&DocType=csv
//				urisToGetHash.put(strFolder + "\\" + parts[1], strResult);
//			}
//						
//			for (Entry<String, String> entry : urisToGetHash.entrySet())
//            {
//            	httpget = new HttpGet(entry.getValue());
//            	
//            	response = client.execute(httpget);
//            	String strFileName = strSavePath +  "\\" + entry.getKey() + ".csv";
//            	writeFile(response.getEntity().getContent(), strFileName);
//            }
//		} catch(Exception e) {
//        	throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
//        } finally {
//        	try {
//				client.close();
//			} catch (IOException e) {
//				throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
//			}
//        }
		
		try {
			client = getHttpClient();
			HttpGet httpget = new HttpGet(strRootUri + "user=" + strUser + "&pass=" + strPass + "&automated=3");
				
			CloseableHttpResponse response = client.execute(httpget);
			HttpEntity entity = response.getEntity();
		
			BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line = null;
						
			while((line = in.readLine()) != null) {
				String[] parts = line.split(",");
				
				String[] bits = parts[1].split("_");
				String strFolder = bits[bits.length - 1];
				
				strDlUri = "https://dss.nyiso.com/dss/docViewAGN.jsp?RepoType=I&ID=";
				String strResult = strDlUri + parts[0] + "&DocName=" + URLEncoder.encode(parts[1].toString(), "utf-8") + "&DocType=csv";
//				https://dss.nyiso.com/dss/docViewAGN.jsp?RepoType=I&ID=XXXX&DocName=XXXXXXXX&entry=&DocType=csv
				urisToGetHash.put("CADD\\" + parts[1], strResult);
			}
						
			for (Entry<String, String> entry : urisToGetHash.entrySet())
            {
            	httpget = new HttpGet(entry.getValue());
            	
            	response = client.execute(httpget);
            	String strFileName = strSavePath +  "\\" + entry.getKey() + ".csv";
            	writeFile(response.getEntity().getContent(), strFileName);
            }			
		} catch(Exception e) {
        	throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
        } finally {
        	try {
				client.close();
			} catch (IOException e) {
				throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
			}
        }
	}
	
	private void doPJM(Table p_tblArguments) throws OException
	{
		String strSavePath = p_tblArguments.getString("save_path", 1);
		
		Map<String, String> mapConfig = null;
		CloseableHttpClient client = null;
		
		try {		
			mapConfig = getConfig(p_tblArguments);
			client = getHttpClient();
            
            for (Entry<String, String> entry : mapConfig.entrySet())
            {
            	print("Key: " + entry.getKey() + " - Value: " + entry.getValue());
            	HttpGet httpget = new HttpGet(entry.getValue());
            	
            	CloseableHttpResponse response = client.execute(httpget);
                try {
                	String strFileName = strSavePath +  "\\" + entry.getKey() + ".csv";
                	HttpEntity entity = response.getEntity();
                	writeFile(entity.getContent(), strFileName);
                } catch(Exception e) {
                	throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
                } finally {
                    response.close();
                }
            }
		} catch (Exception e) {
			throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
		} finally {
            try {
				client.close();
			} catch (IOException e) {
				throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
			}
        }
	}
	
	private void doIESO(Table p_tblArguments) throws Exception
	{
		CloseableHttpClient client = getHttpClient();
		String strRootUri = p_tblArguments.getString("root_uri", 1);		
		HttpClientContext context = getContext(strRootUri, p_tblArguments.getString("user", 1), p_tblArguments.getString("pass", 1));
		
		HttpGet httpget = new HttpGet(strRootUri);
		CloseableHttpResponse response = client.execute(httpget, context);
		
		JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        JSONArray arrFileItems = jsonObject.getJSONArray("files");
        
        for(int i = 0; i < arrFileItems.length(); i++) {
            JSONObject innerObj = arrFileItems.getJSONObject(i);
            recurse(p_tblArguments, strRootUri, innerObj.getString("fileName") + "/", client, context);
        }
	}
	
	protected void recurse(Table p_tblArguments, String strRootUri, String strUri, CloseableHttpClient client, HttpClientContext context) throws Exception
	{
		Pattern p = Pattern.compile("_v[0-9]{1,2}.*$");
		
		HttpGet httpget = new HttpGet(strRootUri + strUri);
		CloseableHttpResponse response = client.execute(httpget, context);            	
    	            	
    	JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        JSONArray arrFileItems = jsonObject.getJSONArray("files");
        
        for(int i = 0; i < arrFileItems.length(); i++)
        {
           JSONObject innerObj = arrFileItems.getJSONObject(i);
           String strFileName = innerObj.getString("fileName");
           
           if (innerObj.getString("isDirectory").equals("true"))
           {
        	   recurse(p_tblArguments, strRootUri + strUri, strFileName + "/", client, context);
           } else {
        	   Matcher matcher = p.matcher(strFileName); 
        	   if(matcher.find())
        		   continue;
        	   
//        	   String strExtension = FilenameUtils.getExtension(strFileName);
        	   String strSavePath = p_tblArguments.getString("save_path", 1) + "\\" + strUri + strFileName;
    		   httpget = new HttpGet(strRootUri + strUri + strFileName);
           	   response = client.execute(httpget, context);
           	   HttpEntity entity = response.getEntity();
               writeFile(entity.getContent(), strSavePath);
           	}
        }
	}
	
	protected String getWeekStart() throws OException
    {
         return OCalendar.formatDateInt(OCalendar.today() - 7, DATE_FORMAT.DATE_FORMAT_DEFAULT);
    }
    
	protected String getWeekEnd() throws OException
    {
    	return OCalendar.formatDateInt(OCalendar.today() - 3, DATE_FORMAT.DATE_FORMAT_DEFAULT);
    }
    
	protected String getMonthStart() throws OException
    {
    	return OCalendar.formatDateInt(OCalendar.getSOM(OCalendar.jumpMonths(OCalendar.today(), -1)), DATE_FORMAT.DATE_FORMAT_DEFAULT);
    }
    
	protected String getMonthEnd() throws OException
    {
    	return OCalendar.formatDateInt(OCalendar.getEOM(OCalendar.jumpMonths(OCalendar.today(), -1)), DATE_FORMAT.DATE_FORMAT_DEFAULT);
    }
	
	protected HttpClientContext getContext(String strUri, String strUsername, String strPassword) throws MalformedURLException
    {
		URL aURL = new URL(strUri);
		
		HttpHost targetHost = new HttpHost(aURL.getHost(), 443, "https");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(strUsername, strPassword));

        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        return context;
    }
	
	protected Map<String, String> getConfig(Table p_tblArguments) throws Exception
    {
		int intOperation = p_tblArguments.getInt("operation", 1);
		
		Map<String, String> urisToGetHash = new HashMap<String, String>();
		Table tblInput = Table.tableNew();
		createInputStructure(tblInput);
		
		switch(intOperation) {
			case IEnbridgeWebServices.IESO_SOURCE:
				doIESO(p_tblArguments);
				break;
			case IEnbridgeWebServices.AESO_SOURCE:
				break;
			case IEnbridgeWebServices.PJM_SOURCE:
				String strConfigFile = p_tblArguments.getString("config_file", 1);
				
				// extract data from CSV File Path
				if (tblInput.inputFromCSVFile(strConfigFile) > 0)
				{
					String strRootUri = p_tblArguments.getString("root_uri", 1);
					String strUsername = p_tblArguments.getString("user", 1);
					String strPassword = p_tblArguments.getString("pass", 1);
					
					for(int i = 1; i <= tblInput.getNumRows(); i++)
					{
						String strApiName = tblInput.getString("api_name", i);
						String strFrequency = tblInput.getString("frequency", i);
						
						if (runReport(strFrequency))
						{			
							String strStart = strFrequency.equals("weekly") ? getWeekStart() : getMonthStart();
							String strEnd = strFrequency.equals("weekly") ? getWeekEnd() : getMonthEnd();
							String strFileName = tblInput.getString("file_name", i) + "_" + strStart + "_"  + strEnd;
									
							String strUri = strRootUri +
								"&username=" +  strUsername + 
								"&password=" +  strPassword +
								"&report=" + strApiName +
								"&version=L&format=C" +
								"&start=" + strStart + 
								"&stop=" + strEnd;
								
							urisToGetHash.put(strFrequency + "\\" + strFileName.replaceAll("/", "_"), Str.stripBlanks(strUri));
						}
					}
				} else {
					throw new OException(" File path not specified");				
				}
				break;
		case IEnbridgeWebServices.NYISO_SOURCE:
				break;
			case IEnbridgeWebServices.MISO_SOURCE:
				break;
		}
		
		return urisToGetHash;
    }
	
	protected void createInputStructure(Table p_tblInput) throws OException
	{
		for(int i = 1; i <= p_tblInput.getNumCols(); i++)
		{
			p_tblInput.setColName(i, p_tblInput.getString(i, 1).toLowerCase());
		}
		p_tblInput.delRow(1);
	}
	
	protected void writeFile(InputStream is, String strFilename) throws IOException
	{
		new File(FilenameUtils.getPath(strFilename)).mkdirs();
		
		File file = new File(strFilename);
		
		if(!file.exists()) {
			FileOutputStream fos = new FileOutputStream(strFilename);
		    int inByte;
		    while((inByte = is.read()) != -1)
		        fos.write(inByte);
		    is.close();
		    fos.close();
		}
	}
	
	protected static String changeExtension(File f, String newExtension)
	{
		int i = f.getName().lastIndexOf('.');
	    String name = f.getName().substring(0,i);
	    return f.getParent() + "/" + name + newExtension;
	}
	
	protected boolean runReport(String strFrequency) throws OException
	{
		int intDayofMonth = OCalendar.getDay(OCalendar.today());
		int intDayofWeek = OCalendar.getDayOfWeek(OCalendar.today());
		
		return (strFrequency.equals("monthly") && (intDayofMonth >= 3 || intDayofMonth <= 7)) || (strFrequency.equals("weekly") && intDayofWeek == 1);
	}
	
	public static void createArgumentsTable(Table p_tblArgs) throws Exception
	{
		p_tblArgs.clearRows();
		
		p_tblArgs.addCol("config_file", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("operation", COL_TYPE_ENUM.COL_INT);
        p_tblArgs.addCol("iso_source", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("save_path", COL_TYPE_ENUM.COL_STRING);
        
        p_tblArgs.addCol("ca_keystore_type", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("ca_keystore_path", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("ca_keystore_pass", COL_TYPE_ENUM.COL_STRING);
        
        p_tblArgs.addCol("client_keystore_type", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("client_keystore_path", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("client_keystore_pass", COL_TYPE_ENUM.COL_STRING);
        
        p_tblArgs.addCol("root_uri", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("version", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("format", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("frequency", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("user", COL_TYPE_ENUM.COL_STRING);
        p_tblArgs.addCol("pass", COL_TYPE_ENUM.COL_STRING);
	}
}
