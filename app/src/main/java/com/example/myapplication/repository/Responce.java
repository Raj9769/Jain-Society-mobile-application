package com.example.myapplication.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Responce<T> {
    @Nullable public final T data;
    @Nullable public String errorMessage;
    @NonNull public Status status;

    public Responce(@NonNull Status status, @Nullable T data, @Nullable String errorMessage) {
        this.data = data;
        this.errorMessage = errorMessage;
        this.status = status;
    }

    public static <T> Responce<T> loading(@Nullable T data){
        return new Responce<>(Status.LOADING,null,null);
    }

    public static <T> Responce<T> success(@NonNull T data){
        return new Responce<>(Status.SUCCESS,data,null);
    }

    public static <T> Responce<T> error(String errorMessage){
        return new Responce<>(Status.FAIL,null,errorMessage);
    }

    public static <T> Responce<T> internet(String internetMessage){
        return new Responce<>(Status.INTERNET,null,internetMessage);
    }
}
