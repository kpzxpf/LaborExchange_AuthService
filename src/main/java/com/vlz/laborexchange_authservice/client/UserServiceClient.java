package com.vlz.laborexchange_authservice.client;

import com.vlz.laborexchange_authservice.dto.LoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "user-service",
        url = "${clients.user-service.url}"
)
public interface UserServiceClient {

    @GetMapping("/api/users/existsByEmail/")
    boolean existsUserByEmail(@RequestBody String email);

    @GetMapping("/api/users/checkLogin/")
    boolean checkLogin(LoginRequest request);
}