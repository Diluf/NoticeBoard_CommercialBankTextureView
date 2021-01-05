package com.c3labs.dss_CommercialBank.WebService;

import com.c3labs.dss_CommercialBank.Clz.TLSSocketFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by c3 on 2/6/2018.
 */

public class CallWeb {
    public String callService(String urlString) {
//        try {
//            Client client = Client.create();
//            WebResource webResource = client.resource(url);
//            client.setConnectTimeout(10000);
//            MultivaluedMap queryParams = new MultivaluedMapImpl();
//            return webResource.queryParams(queryParams).get(String.class);
//        } catch (com.sun.jersey.api.client.ClientHandlerException e) {
//            Log.d("==========", "callService: ------------------------------");
//            e.printStackTrace();
//        }


        StringBuilder sb = null;
        BufferedReader reader = null;
        String serverResponse = "**";
        try {

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //

//            TLSSocketFactory socketFactory = new TLSSocketFactory();
//            connection.setSSLSocketFactory(socketFactory);

            //
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.connect();
            int statusCode = connection.getResponseCode();
            //Log.e("statusCode", "" + statusCode);
            if (statusCode == 200) {
                sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            }

            connection.disconnect();
            if (sb != null)
                serverResponse = sb.toString();
        } catch (java.net.SocketTimeoutException e) {
            e.printStackTrace();
            return "timeOutEx";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return serverResponse;

//        return "";
    }
}


