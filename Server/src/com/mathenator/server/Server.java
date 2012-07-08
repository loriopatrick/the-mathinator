package com.mathenator.server;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import java.io.IOException;

public class Server {

    public Server (int port) {
        this.port = port;
        address = new InetSocketAddress(port);
    }

    protected int port;
    protected InetSocketAddress address;
    protected HttpServer server;

    public void start (String dir) throws IOException {
        server = HttpServer.create(address, 0);

        server.createContext("/calc", new Calc());
        server.createContext("/", new Get(dir));
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }
}
