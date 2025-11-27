package com.vlz.laborexchange_authservice.producer;

public interface KafkaProducer<T> {
    void send(T event);
}