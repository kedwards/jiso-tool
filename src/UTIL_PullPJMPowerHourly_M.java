
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

package com.enb.utilities.end_of_day;

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

public class UTIL_PullPJMPowerHourly_M extends IEnbridgeScript 
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
	
	
	HttpEntity getHTTPResponseEntity(String p_strRequestUrl, String p_strSubscriptionKey, String p_strContentType) throws Exception
	{
		HttpEntity heEntity = null;		
		
    	HttpClient httpclient = HttpClients.createDefault();        	
    	URIBuilder builder = new URIBuilder(p_strRequestUrl);
    	
   	
//    	builder.setScheme("https");
//    	builder.setHost("api.pjm.com/api/v1");
//    	builder.setPath("agg_definitions");
    	
    	builder.setParameter("startRow", "1");
    	builder.setParameter("rowCount", "15000");
    	
    	
    	//builder.setParameter("download", "TRUE");        
        //builder.setParameter("sort", "datetime_beginning_ept");
        //builder.setParameter("order", "Asc");        
        //builder.setParameter("isActiveMetadata", "FALSE");
        //builder.setParameter("fields", "{string}");
        builder.setParameter("datetime_beginning_utc", "2018-03-13");
        builder.setParameter("datetime_beginning_ept", "2018-03-13");
        //builder.setParameter("pnode_id", "{number}");
        //builder.setParameter("voltage", "{string}");
        //builder.setParameter("equipment", "{string}");
        //builder.setParameter("type", "{string}");
        //builder.setParameter("zone", "{string}");
        //builder.setParameter("row_is_current", "1");
        //builder.setParameter("version_nbr", "{number}");

        URI uri = builder.build();
        HttpGet request = new HttpGet(uri);
        
        print(uri.toString());
        
//        HttpPost request = new HttpPost(uri);
         
        request.setHeader("Content-Type", p_strContentType);
        request.setHeader("Ocp-Apim-Subscription-Key", p_strSubscriptionKey);

        
        // Request body

        HttpResponse response = httpclient.execute(request);
        heEntity = response.getEntity();
        
        print(request.getURI().toString());
        
		
		return heEntity;
	}
	
	
	public int createXMLCsvFile(String p_strJsonString, String p_strFilePath) throws IOException, OException, JSONException
	{
		int intResult = 0;
		
		Table tblTemp = null;
		XString xstring = Str.xstringNew();
		
		tblTemp = Table.xmlStringToTable(p_strJsonString, xstring, 1);
		String strError = Str.xstringGetString(xstring);
		
		Str.printToFile("M:\\Temp\\Dev\\xmlPJM.xml", p_strJsonString);
		
		if (isValidTable(tblTemp))
		{
			intResult = 1;
			Table tblOutput = tblTemp.getTable(1, 1).getTable(1, 1);
			
			tblTemp.viewTable();
			
			if (isValidTable(tblOutput))
				tblOutput.excelSave(p_strFilePath);
			
			
		}
		else
		{
			print("Table.xmlStringToTable: failed: " + strError);
		}			  
		
		return intResult;
    }
	
	public int createJSonCsvFile(String p_strJsonString, String p_strFilePath) throws IOException, Exception
	{
		int intResult = 1;
		
    	JSONObject jsonObject = new JSONObject(p_strJsonString);
        JSONArray arrDocsItems = jsonObject.getJSONArray("items");

        File csvFileName = new File(p_strFilePath);
        String strCsvData = CDL.toString(arrDocsItems);

        if (strCsvData.length() > 0)
        {
        	intResult = 1;
        	FileUtils.writeStringToFile(csvFileName, strCsvData, "UTF-8", false);
        }
        else
        {
        	intResult = 0;
        }
        
        return intResult;
    }

		

}
