package com.example.assignment.repository;

import com.example.assignment.entity.Extension;
import com.example.assignment.entity.ExtensionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtensionRepository extends JpaRepository<Extension, Long> {

    //타입별로 리스트 조회
	List<Extension> findAllByTypeOrderByCreatedAtDesc(ExtensionType type);

    //저장하기 전에 중복 검사
    Optional<Extension> findByName(String name);
    
    //존재 여부 확인
    long countByName(String name);

    //커스텀 확장자 200개 제한 체크용
    long countByType(ExtensionType type);
}