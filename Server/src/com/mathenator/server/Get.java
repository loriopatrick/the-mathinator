package com.mathenator.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Get implements HttpHandler {

    public Get(String root) {
        this.root = root;
    }

    public String root;

    public void handle(HttpExchange ex) {
        String path = ex.getRequestURI().toString();


        if (path.equals("/")) path = "/index.html";
        System.out.println("GET: " + path);

        Headers responseHeaders = ex.getResponseHeaders();
//        responseHeaders.set("Content-Type", "text/plain");

        try {
            ex.sendResponseHeaders(200, 0);
            IO.ReadTo(root + path, ex.getResponseBody());
        } catch (Exception e) {
            System.out.println(e);
        }

        ex.close();
    }
}
