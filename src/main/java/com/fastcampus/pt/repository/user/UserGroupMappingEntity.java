package com.fastcampus.pt.repository.user;


import com.fastcampus.pt.repository.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user_group_mapping")
@IdClass(UserGroupMappingId.class) // 복합키로 아이디 설정
public class UserGroupMappingEntity extends BaseEntity {
    @Id
    private String userGroupId;
    @Id
    private String userId;

    private String userGroupName;
    private String description;

}
