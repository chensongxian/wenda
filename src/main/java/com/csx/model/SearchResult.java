package com.csx.model;

/**
 * Created by csx on 2016/9/28.
 */
public class SearchResult {
    private int type;
    private Question question;
    private Comment comment;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
