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

        Headers responseHeaders = ex.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/plain");

        try {
            IO.ReadTo(root + path, ex.getResponseBody());
            ex.sendResponseHeaders(200, 0);
        } catch (Exception e) {
            try {
                ex.sendResponseHeaders(500, 0);
            } catch (Exception e2) {
                System.out.println(e2);
            }
        }

        ex.close();
    }
}
