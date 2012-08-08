package com.mathinator.server;

import com.mathinator.server.IO;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.StreamHandler;

public class Get implements HttpHandler {

    public Get(String root) {
        this.root = root;
    }

    public String root;

    public void handle(HttpExchange ex) {
        String path = ex.getRequestURI().toString();

        if (path.endsWith("/")) path += "index.html";

        Headers h = ex.getResponseHeaders();


        String[] temp = path.split("\\.");
        String end = temp[temp.length - 1];

        if (end.equals("js")) {
            h.add("Content-Type", "text/javascript");
            h.add("charset", "utf-8");
        } else if (end.equals("html")) {
            h.add("Content-Type", "text/html");
            h.add("charset", "utf-8");
        } else {
            h.add("charset", "utf-8");
        }

        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream(root + path);
            h.add("Content-Length", in.available() + "");
            ex.sendResponseHeaders(200, 0);
            IO.ReadTo(in, ex.getResponseBody());
        } catch (Exception e) {
            System.out.println(e);
        }

        ex.close();
    }
}
