package com.vlz.laborexchange_authservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "${spring.clients.role-service.name}",
        url = "${spring.clients.role-service.url}"
)
public interface RoleServiceClient {
    @GetMapping("/api/roles/roleByEmail")
    String getUserRoleByEmail(@RequestParam("email") String email);
}
