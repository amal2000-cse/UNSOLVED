package com.example.quoraclone.Model;

public class Comment {

private String comment , postid,date , publisher;

public  Comment(){

}

    public Comment(String comment, String postid, String date, String publisher) {
        this.comment = comment;
        this.postid = postid;
        this.date = date;
        this.publisher = publisher;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getComment() {
        return comment;
    }

    public String getPostid() {
        return postid;
    }

    public String getDate() {
        return date;
    }

    public String getPublisher() {
        return publisher;
    }
}
