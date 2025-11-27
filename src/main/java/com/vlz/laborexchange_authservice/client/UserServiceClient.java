package com.vlz.laborexchange_authservice.client;

import com.vlz.laborexchange_authservice.dto.LoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "user-service",
        url = "${clients.user-service.url}"
)
public interface UserServiceClient {

    @GetMapping("/api/users/existsByEmail")
    boolean existsUserByEmail(@RequestParam("email") String email);

    @PostMapping("/api/users/checkLogin")
    boolean checkLogin(@RequestBody LoginRequest request);
}