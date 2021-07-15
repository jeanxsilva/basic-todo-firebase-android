package br.com.faesp.crud_firebase_android;

import java.io.Serializable;

public class TaskModel implements Serializable {
    private int Id;
    private String Title;
    private String Description;

    public TaskModel(){
    }

    public String getTitle(){
        return this.Title;
    }

    public void setTitle(String title){
        this.Title = title;
    }

    public int getId(){
        return this.Id;
    }

    public void setId(int id){
        this.Id = id;
    }

    public String getDescription(){
        return this.Description;
    }

    public void setDescription(String description){
        this.Description = description;
    }
}