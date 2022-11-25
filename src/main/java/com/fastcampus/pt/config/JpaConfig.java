package com.fastcampus.pt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //시간에 대해서 값을 자동으로 넣어주는 어노테이션 (생성일자, 수정일자)
@Configuration
public class JpaConfig {
}
