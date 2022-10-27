package com.example.quoraclone.Model;

public class Post {

    private String askedBy, date, postid, publisher, question, questionimage, Topic;

    public Post() {
    }

    public Post(String askedBy, String date, String postid, String publisher, String question, String questionimage, String topic) {
        this.askedBy = askedBy;
        this.date = date;
        this.postid = postid;
        this.publisher = publisher;
        this.question = question;
        this.questionimage = questionimage;
        Topic = topic;
    }

    public String getAskedBy() {
        return askedBy;
    }

    public void setAskedBy(String askedBy) {
        this.askedBy = askedBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionimage() {
        return questionimage;
    }

    public void setQuestionimage(String questionimage) {
        this.questionimage = questionimage;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }
}
