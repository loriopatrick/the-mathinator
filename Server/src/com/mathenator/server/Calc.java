package com.mathenator.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.mathenator.engine.*;

import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Date;

public class Calc implements HttpHandler {

    public static String logFile = "math.log.txt";

    public Calc () {
        try{
            File f = new File(logFile);
            if (!f.exists()) {
                f.createNewFile();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void Write(String data, OutputStream out) throws IOException {
        data += "\n";
        out.write(data.getBytes());
    }

    public String Read(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();

        int r;
        while ((r = inputStream.read()) != -1) {
            sb.append((char) r);
        }

        inputStream.close();

        return sb.toString();
    }

    public static int steps = 80;

    public void handle(HttpExchange ex) {

        Headers responseHeaders = ex.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/plain");

        OutputStream outputStream = ex.getResponseBody();

        String eq = null;
        try {
            eq = Read(ex.getRequestBody());
            System.out.println("CALC: " + eq);


            ex.sendResponseHeaders(200, 0);

            if (eq.contains("=")) {
                Node n = Parser.CreateNode(eq);
                Parser.MarkUp(n);
                Write(Parser.ReadNodeLatex(n), outputStream);
                for (int i = 0; i < steps; i++) {
                    Parser.MarkUp(n);
                    try {
                        Write(Parser.ReadNodeLatex(n), outputStream);
                    } catch (Exception e) {}
                    if (Solve2.Step(n, "x")) break;
                }
            } else {
                Node n = Parser.CreateNode(eq);
                Parser.MarkUp(n);
                Write(Parser.ReadNodeLatex(n), outputStream);
                for (int i = 0; i < steps; i++) {
                    Parser.MarkUp(n);
                    try {
                        Write(Parser.ReadNodeLatex(n), outputStream);
                    } catch (Exception e) {}
                    if (Simplify.Step(n)) break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        ex.close();

        try {
            IO.Append(logFile, new Date().getTime() +
                    "\t" + ex.getRemoteAddress().getAddress().toString() + "\t" + eq + '\n');
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
