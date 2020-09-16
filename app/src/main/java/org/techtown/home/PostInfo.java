package org.techtown.home;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo implements Serializable {
    private String title;
    private String author;
    private String startDay;
    private String finishDay;
    private String totalPage;
    private String readPage;
    private String ocrWise;
    private ArrayList<String> contents;
    private ArrayList<String> formats;
    private String publisher;//작성자 userUID
    private Date createdAt;
    private String id;

    public PostInfo(String title, String author, String startDay, String finishDay, String totalPage, String readPage, String ocrWise, ArrayList<String> contents, ArrayList<String> formats, String publisher, Date createdAt, String id){
        this.title = title;
        this.author = author;
        this.startDay = startDay;
        this.finishDay = finishDay;
        this.totalPage = totalPage;
        this.readPage = readPage;
        this.ocrWise = ocrWise;
        this.contents = contents;
        this.formats = formats;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
    }


    public PostInfo(String title, String author, String startDay, String finishDay, String totalPage, String readPage, String ocrWise, ArrayList<String> contents, ArrayList<String> formats, String publisher, Date createdAt){
        this.title = title;
        this.author = author;
        this.startDay = startDay;
        this.finishDay = finishDay;
        this.totalPage = totalPage;
        this.readPage = readPage;
        this.ocrWise = ocrWise;
        this.contents = contents;
        this.formats = formats;
        this.publisher = publisher;
        this.createdAt = createdAt;
    }


    public Map<String, Object> getPostInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("title",title);
        docData.put("author",author);
        docData.put("startDay",startDay);
        docData.put("finishDay",finishDay);
        docData.put("totalPage",totalPage);
        docData.put("readPage",readPage);
        docData.put("ocrWise",ocrWise);
        docData.put("contents",contents);
        docData.put("formats",formats);
        docData.put("publisher",publisher);
        docData.put("createdAt",createdAt);
        return  docData;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public String getStartDay() { return startDay; }

    public void setStartDay(String startDay) { this.startDay = startDay; }

    public String getFinishDay() { return finishDay; }

    public void setFinishDay(String finishDay) { this.finishDay = finishDay; }

    public String getTotalPage() { return totalPage; }

    public void setTotalPage(String totalPage) { this.totalPage = totalPage; }

    public String getReadPage() { return readPage; }

    public void setReadPage(String readPage) { this.readPage = readPage; }

    public String getOcrWise() { return ocrWise; }

    public void setOcrWise(String ocrWise) { this.ocrWise = ocrWise; }

    public ArrayList<String> getContents() { return contents; }

    public void setContents(ArrayList<String> contents) { this.contents = contents; }

    public ArrayList<String> getFormats() { return formats; }

    public void setFormats(ArrayList<String> formats) { this.formats = formats; }

    public String getPublisher() { return publisher; }

    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Date getCreatedAt() { return createdAt; }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}
