package org.enb.iso;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;

public class KeyStoreTest
{
    public static void main(String[] args) throws Exception
    {
        String keystoreFilename = "C:\\java\\jdk1.8.0_152\\jre\\lib\\security\\cacerts";

        char[] password = "changeit".toCharArray();
        String alias = "midwestiso";

        FileInputStream fIn = new FileInputStream(keystoreFilename);
        KeyStore keystore = KeyStore.getInstance("JKS");

        keystore.load(fIn, password);

        Certificate cert = keystore.getCertificate(alias);

        System.out.println(cert);
    }
}
