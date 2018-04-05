package org.enb.iso;

import com.enb.utilities.UTIL_PowerHttpConnect;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public class IesoTest {
    private final static String baseUrl = "https://reports.ieso.ca/api/v1.1/files/private/TIDAL/";
    private final static String username = "loucksd";
    private final static String password = "Qr67885!";

    public static void main(String[] args)
    {
        HttpClientContext context = UTIL_PowerHttpConnect.getContext(username, password);

        try {
            CloseableHttpClient client = UTIL_PowerHttpConnect.getHttpClient("ieso");
            HttpGet httpget = new HttpGet(baseUrl);
            HttpResponse response = client.execute(httpget, context);
            // System.out.println(response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(entity));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
