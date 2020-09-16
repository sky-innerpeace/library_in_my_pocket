package org.techtown.home.algorithm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class DownLoadBroker implements Runnable {
    private String address;
    private String fileName;

    public DownLoadBroker(String address) {
        this.address = address;
    }

    public DownLoadBroker(String address, String fileName) {
        this.address = address;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            URL url = new URL(address);
            InputStream is = url.openStream();
            //bufferinputstream 사용하면 빨라짐
            InputStream input = new BufferedInputStream(is);

            int data;
            while ((data = input.read()) != -1) {
                bos.write(data);

            }
            bos.close();
            input.close();
            System.out.println("download complete...");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
