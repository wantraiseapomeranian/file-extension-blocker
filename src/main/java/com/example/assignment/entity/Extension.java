package com.example.assignment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Extension {
	
	//시퀀스를 이용한 PK 설정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //20자 제한, 중복 방지
    @Column(nullable = false, length = 20, unique = true)
    private String name;

    //타입
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExtensionType type;
    
    //데이터 생성 시간 자동 기록
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Extension(String name, ExtensionType type) {
        this.name = name;
        this.type = type;
    }
}