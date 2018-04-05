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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;


// import com.olf.openjvs.*;
// import com.olf.openjvs.enums.COL_TYPE_ENUM;
// import com.enb.libraries.IEnbridgeScript;

// import org.json.*;

public class PjmTest
{
    private static final String savePath = "C:\\Users\\edwardk3\\PortableApps\\LivITy\\.babun\\cygwin\\home\\edwardk3\\workspace\\jiso-tool\\data\\pjm\\";

    public static void main(String[] args) throws Exception
    {
        CloseableHttpClient c = UTIL_PowerHttpConnect.getHttpClient("nyiso");

        Map<String, String> urisToGetHash = getConfig(new HashMap<String, String>());

        // create a thread for each URI report to download
        GetThread[] threads = new GetThread[urisToGetHash.size()];

        try
        {
            Integer i = 0;
            for (Entry<String, String> entry : urisToGetHash.entrySet())
            {
                HttpGet httpget = new HttpGet(entry.getValue());
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

    private static Map<String, String> getConfig(Map<String, String> uriMap) throws Exception
    {
        File xlsxFile = new File("pjm.xlsx");
        FileInputStream fis = new FileInputStream(xlsxFile);

        // Finds the workbook instance for XLSX file
        XSSFWorkbook workBook = new XSSFWorkbook (fis);

        // Return first sheet from the XLSX workbook
        XSSFSheet sheet = workBook.getSheetAt(0);

        for(Row row : sheet) {
            if (row.getRowNum() == 0)
                continue;
            // Cell cell = row.getCell();
            String uri = row.getCell(6).getStringCellValue() + "report=" + row.getCell(4).getStringCellValue() + "&version=L&format=C&&start=02/01/2018&stop=02/28/2018&username=loucksd&password=Enbpower3!";
            uriMap.put(row.getCell(4).getStringCellValue(), uri);
        }
        return uriMap;
    }
}
