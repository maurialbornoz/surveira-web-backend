package com.example.surveybackend.utils.transformer;

public interface Transformer<K, T> {
    T transformData(K data);
}
