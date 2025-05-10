package com.devteria.post.repository.htttpclient;

import com.devteria.post.dto.ApiResponse;
import com.devteria.post.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${app.service.profile.url}")
public interface ProfileClient {
    @GetMapping("internal/users/{userId}")
    ApiResponse<UserProfileResponse> getUserProfile(@PathVariable String userId);
}
