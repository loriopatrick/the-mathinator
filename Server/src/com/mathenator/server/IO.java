package com.mathenator.server;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IO {
    public static void WriteFrom(String path, InputStream in) throws IOException {
        FileOutputStream fs = new FileOutputStream(path);
        BufferedOutputStream bs = new BufferedOutputStream(fs);
        DataOutputStream dos = new DataOutputStream(bs);

        int r;
        while ((r = in.read()) != -1) {
            dos.write(r);
        }

        fs.close();
        bs.close();
        dos.close();
    }

    public static void Write(String path, String content) throws IOException {
        FileOutputStream fs = new FileOutputStream(path);
        BufferedOutputStream bs = new BufferedOutputStream(fs);
        DataOutputStream dos = new DataOutputStream(bs);

        for (int i = 0; i < content.length(); i++) dos.write(content.charAt(i));

        fs.close();
        bs.close();
        dos.close();
    }

    public static void ReadTo(InputStream in, OutputStream out) throws IOException {
        int r;
        while ((r = in.read()) != -1) {
            out.write(r);
        }

        in.close();
        in.close();
    }

    public static void ReadTo(String path, OutputStream out) throws IOException {
        FileInputStream fs = new FileInputStream(path);
        InputStream in = new BufferedInputStream(fs);

        int r;
        while ((r = in.read()) != -1) {
            out.write(r);
        }

        fs.close();
        in.close();
    }

    public static String Read(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        FileInputStream fs = new FileInputStream(path);
        InputStream in = new BufferedInputStream(fs);

        int r;
        while ((r = in.read()) != -1) {
            sb.append((char) r);
        }

        fs.close();
        in.close();

        return sb.toString();
    }
}
