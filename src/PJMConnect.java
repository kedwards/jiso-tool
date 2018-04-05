package org.enb.iso;

import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.util.*;

public class PJMConnect
{
    private String report_name;
    private String subscription_key;
    private URIBuilder builder;

    public String getReportName()
    {
        return report_name;
    }

    public String getSubscriptionKey()
    {
        return subscription_key;
    }

    public String toString()
    {
        return super.toString() + " {"
            + "report_name="
            + getReportName()
            + " subscription_key="
            + getSubscriptionKey()
            + "}";
    }

    private PJMConnect(Builder b)
    {
        this.report_name = b.report_name;
        this.subscription_key = b.subscription_key;
        this.builder = b.builder;
    }

    public static class Builder
    {
        private String report_name;
        private String subscription_key;
        private URIBuilder builder;

        public Builder(String report_name, String subscription_key)
        {
            this.report_name = report_name;
            this.subscription_key = subscription_key;
            this.builder = new URIBuilder();
        }

        public Builder setParams(Hashtable<String, String> params)
        {
            java.util.Enumeration parameters = params.keys();

            for(Map.Entry m:params.entrySet()){
                this.builder.setParameter((String) m.getKey(), (String) m.getValue());
            }
            return this;
        }

        public HttpEntity build()
        {
            PJMConnect connect = new PJMConnect(this);

            try
            {
                HttpClient httpclient = HttpClients.createDefault();
                connect.builder.setScheme("https")
                    .setHost("api.pjm.com/api/v1")
                    .setPath(connect.report_name);
                URI uri = connect.builder.build();
                HttpGet request = new HttpGet(uri);

                request.setHeader("Ocp-Apim-Subscription-Key", connect.subscription_key);
                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();

                return entity;
            }
            catch (Exception e)
            {
                return null; // System.out.println(e.getMessage());
            }
        }
    }
}
