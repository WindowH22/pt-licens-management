package com.fastcampus.pt.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroupMappingEntity,Integer> {
    List<UserGroupMappingEntity> findByUserGroupId(String userGroupId);
}
