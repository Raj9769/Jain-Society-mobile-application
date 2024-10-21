package com.example.myapplication.model;

import java.io.File;

public class SongModel {
    private String name;
    private int file;

    public SongModel(String name, int file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFile() {
        return file;
    }

    public void setFile(int file) {
        this.file = file;
    }
}
