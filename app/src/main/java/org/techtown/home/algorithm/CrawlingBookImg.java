package org.techtown.home.algorithm;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class CrawlingBookImg {
    Document doc = null;
    Runnable r = null;
    public void SearchBooks(String url) {

        Thread downloadThread = new Thread() {
            public void run() {
                try {
                    Connection con = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").timeout(10000);
                    Connection.Response resp = con.execute();

                    if (resp.statusCode() == 200) {
                        doc = con.get();

                    }

                    //Document doc = Jsoup.connect(url).post();


                    Elements element = doc.select("ol.thumb_list");    //cssQuery id는 # class는 .

                    System.out.println("=====================================");

                    for (Element image : element.select("div.thmb > a > img") ) {

                        String imgUrl = image.attr("src").trim();
                        System.out.println(imgUrl);
                        r = new DownLoadBroker(imgUrl, imgUrl.substring(imgUrl.lastIndexOf("/"))+1);
                        Thread dLoad = new Thread(r);
                        dLoad.start();
                        System.out.println("=====================================");
                        for(int i=0;i<10;i++){
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        downloadThread.start();

    }
}
