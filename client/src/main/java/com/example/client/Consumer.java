package com.example.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class Consumer {
    int port = 8111;

    Consumer() {
        System.out.println("Default port: " + port);
    }

    Consumer(int port) {
        this.port = port;
        System.out.println("Port: " + port);
    }


    public static void main(String[] args) {
        Integer eta = new Consumer().getEta("HammerSmith", 613);
        System.out.println("ETA: " + eta);
    }

    public Integer getEta(String station, int nr) {
        try {
            String url = String.format("http://localhost:%d/bus/%s/%s",port, station, nr);
            System.out.println("Using url: " + url);
            HttpResponse r = Request.Get(url).execute().returnResponse();
            String json = EntityUtils.toString(r.getEntity());
            System.out.println("Json= " + json);
            JSONObject jsonObject = new JSONObject(json);
            String eta = jsonObject.get("eta").toString();
            return new Integer(eta);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}