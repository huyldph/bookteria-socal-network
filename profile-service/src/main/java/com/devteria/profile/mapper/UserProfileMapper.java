package com.devteria.profile.mapper;

import org.mapstruct.Mapper;

import com.devteria.profile.dto.request.ProfileCreationRequest;
import com.devteria.profile.dto.response.UserProfileResponse;
import com.devteria.profile.entity.UserProfile;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    @Mapping(target = "username", source = "username")
    UserProfileResponse toUserProfileResponse(UserProfile entity);
}