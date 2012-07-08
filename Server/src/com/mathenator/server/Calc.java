package com.mathenator.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.mathenator.engine.*;

import java.io.OutputStream;
import java.io.IOException;

public class Calc implements HttpHandler {

    public void Write(String data, OutputStream out) throws IOException {
        data += "\n";
        out.write(data.getBytes());
    }

    public void handle(HttpExchange ex) {

        String eq = ex.getRequestBody().toString();
        System.out.println("CALC: " + eq);

        Headers responseHeaders = ex.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/plain");

        OutputStream outputStream = ex.getResponseBody();

        try {
            ex.sendResponseHeaders(200, 0);
            Node n = Parser.CreateNode(eq);
            Parser.MarkUp(n);
            Write(Parser.ReadNode(n), outputStream);
            for (int i = 0; i < 1000; i++) {
                Parser.MarkUp(n);
                Write(Parser.ReadNode(n), outputStream);
                if (Simplify.Step(n)) break;
            }
        } catch (Exception e) {
            try {
                Write("Error", outputStream);
            } catch (Exception e2) {

            }
        }

        ex.close();

    }
}
