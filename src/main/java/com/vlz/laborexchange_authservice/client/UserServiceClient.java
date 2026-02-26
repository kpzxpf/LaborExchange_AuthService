package com.vlz.laborexchange_authservice.client;

import com.vlz.laborexchange_authservice.dto.LoginRequest;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "${spring.clients.user-service.name}",
        url = "${spring.clients.user-service.url}"
)
public interface UserServiceClient {

    @PostMapping("/api/users/register")
    Long registerUser(@RequestBody RegisterRequest request);

    @GetMapping("/api/users/existsByEmail")
    boolean existsUserByEmail(@RequestParam("email") String email);

    @PostMapping("/api/users/checkLogin")
    boolean checkLogin(@RequestBody LoginRequest request);

    @GetMapping("/api/users/userIdByEmail")
    Long getUserIdByEmail(@RequestParam("email") String email);
}