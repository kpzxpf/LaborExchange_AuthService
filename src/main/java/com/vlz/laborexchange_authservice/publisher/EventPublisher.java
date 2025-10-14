package com.vlz.laborexchange_authservice.publisher;

import com.vlz.laborexchange_authservice.dto.RegisterRequest;

public interface EventPublisher<T> {
    void publish(T event);
}