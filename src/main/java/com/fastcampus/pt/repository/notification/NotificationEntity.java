package com.fastcampus.pt.repository.notification;

import com.fastcampus.pt.repository.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "notification")
public class NotificationEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임합니다. (AUTO_INCREMENT)
    private Integer notificationSeq;
    private String uuid;


    private String text;
    private boolean sent;
    private LocalDateTime sentAt;

}

