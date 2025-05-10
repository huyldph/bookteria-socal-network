package com.devteria.post.service;

import com.devteria.post.dto.PageResponse;
import com.devteria.post.dto.request.PostRequest;
import com.devteria.post.dto.response.PostResponse;
import com.devteria.post.dto.response.UserProfileResponse;
import com.devteria.post.entity.Post;
import com.devteria.post.mapper.PostMapper;
import com.devteria.post.repository.PostRepository;
import com.devteria.post.repository.htttpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    DateTimeFormatter dateTimeFormatter;
    PostMapper postMapper;
    ProfileClient profileClient;

    public PostResponse createPost(PostRequest postRequest) {
        //Lấy thông tin user từ token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Post post = Post.builder()
                .content(postRequest.getContent())
                .userId(authentication.getName())
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        post = postRepository.save(post);

        return postMapper.toPostResponse(post);
    }

    public PageResponse<PostResponse> getAllPosts(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        UserProfileResponse userProfile = null;

        try {
            userProfile = profileClient.getUserProfile(userId).getResult();
        } catch (Exception e) {
            log.error("Error fetching user profile: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch user profile");
        }

        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var postPage = postRepository.findAllByUserId(userId, pageable);

        String userName = userProfile != null ? userProfile.getUsername() : "Unknown User";
        var postList = postPage.getContent().stream().map(post -> {
            var postResponse = postMapper.toPostResponse(post);
            postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
            postResponse.setUsername(userName);

            return postResponse;
        }).toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(postPage.getSize())
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .data(postList)
                .build();
    }
}
