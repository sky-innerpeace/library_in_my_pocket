package org.techtown.home;

public class Book {
    private String title;
    private String link;
    private String image;
    private String author;
    private String publisher;
    private String isbn;
    private String description;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return " 책 제목 :" + title + "\n 책 링크 : " + link + "\n 책 이미지 : " + image + "\n 책 저자 : " + author +
                "\n 출판사 : " + publisher + "\n isbn : " + isbn + "\n 책 소개 : " + description + "\n";
    }


}
