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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

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

		String strConfigFile = p_tblArguments.getString("config_file", 1);
		String strSavePath = p_tblArguments.getString("save_path", 1);

		Map<String, String> mapConfig = null;
		CloseableHttpClient client = null;

		try {
			Integer i = 0;

			mapConfig = getConfig(p_tblArguments, strConfigFile);
			client = (CloseableHttpClient) getHttpClient();

			// create a thread for each URI report to download
//	        GetThread[] threads = new GetThread[mapConfig.size()];

            for (Entry<String, String> entry : mapConfig.entrySet())
            {
            	print("Key: " + entry.getKey() + " - Value: " + entry.getValue());
//            	HttpGet httpget = new HttpGet(entry.getValue());
//
//            	CloseableHttpResponse response = (CloseableHttpResponse) client.execute(httpget);
//                try {
//                    String strFilename = Util.reportGetDirForToday() + "\\" + entry.getKey() + ".csv";
//                	HttpEntity entity = response.getEntity();
//
//                	InputStream is = entity.getContent();
//                    FileOutputStream fos = new FileOutputStream(strFilename);
//                    int inByte;
//                    while((inByte = is.read()) != -1)
//                        fos.write(inByte);
//                    is.close();
//                    fos.close();
//                } catch(Exception e) {
//                	throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
//                } finally {
//                    response.close();
//                }
//                threads[i] = new GetThread(client, httpget, strSavePath + entry.getKey() + ".csv");
//                i++;
            }

            // start the threads
//            for (int j = 0; j < threads.length; j++) {
//                threads[j].start();
//            }

            // join the threads
//            for (int j = 0; j < threads.length; j++) {
//                threads[j].join();
//            }
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

	protected class GetThread extends Thread
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
            try {
            	CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpget);
                try {
                    HttpEntity entity = response.getEntity();
                     InputStream is = entity.getContent();
                     FileOutputStream fos = new FileOutputStream(fileSavePath);
                     int inByte;
                     while((inByte = is.read()) != -1)
                         fos.write(inByte);
                     is.close();
                     fos.close();
                } finally {
                    response.close();
                }
            } catch (Exception e) {
            	try {
					throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
				} catch (OException e1) {
					e1.printStackTrace();
				}
            }
        }
    }

	protected void createInputStructure(Table p_tblInput) throws OException
	{
		for(int i = 1; i <= p_tblInput.getNumCols(); i++)
		{
			p_tblInput.setColName(i, p_tblInput.getString(i, 1).toLowerCase());
		}
		p_tblInput.delRow(1);
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

	protected Map<String, String> getConfig(Table p_tblArguments, String strConfigFile) throws Exception
    {
		Map<String, String> urisToGetHash = new HashMap<String, String>();
		Table tblInput = Table.tableNew();
		String strRootUri;
		String strUsername;
		String strPassword;

		// extract data from CSV File Path
		if (tblInput.inputFromCSVFile(strConfigFile) > 0)
		{
			createInputStructure(tblInput);

			strRootUri = p_tblArguments.getString("root_uri", 1);
			strUsername = p_tblArguments.getString("user", 1);
			strPassword = p_tblArguments.getString("pass", 1);

			for(int i = 1; i <= tblInput.getNumRows(); i++)
			{
				String strApiName = tblInput.getString("api_name", i);
				String strFrequency = tblInput.getString("frequency", i);

				if (runReport(strFrequency))
				{
					String strUri = strRootUri +
						"&username=" +  strUsername +
						"&password=" +  strPassword +
						"&report=" + strApiName +
						"&version=L&format=C" +
						"&start=" + (strFrequency.equals("weekly") ? getWeekStart() : getMonthStart()) +
						"&stop=" + (strFrequency.equals("weekly") ? getWeekEnd() : getMonthEnd());

					urisToGetHash.put(tblInput.getString("file_name", i), Str.stripBlanks(strUri));
				}
			}

			return urisToGetHash;
		} else {
			throw new OException(" File path not specified");
		}
    }

	protected void writeFile(HttpEntity entity, File savePath)
	{

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
