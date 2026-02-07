package com.example.assignment.service;

import com.example.assignment.entity.Extension;
import com.example.assignment.entity.ExtensionType;
import com.example.assignment.repository.ExtensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtensionService {

    private final ExtensionRepository extensionRepository;

    //커스텀 확장자 목록 조회
    public List<Extension> getCustomExtensions() {
        return extensionRepository.findAllByTypeOrderByCreatedAtDesc(ExtensionType.CUSTOM);
    }
    
    //고정 확장자 목록 조회
    public List<Extension> getFixedExtensions() {
        return extensionRepository.findAllByTypeOrderByCreatedAtDesc(ExtensionType.FIXED);
    }

    //커스텀 확장자 추가
    @Transactional
    public void addCustomExtension(String name) {
        //200개가 넘어가는 경우
        long count = extensionRepository.countByType(ExtensionType.CUSTOM);
        if (count >= 200) {
            throw new IllegalArgumentException("커스텀 확장자는 최대 200개까지만 등록 가능합니다.");
        }

        //중복 체크
        if (extensionRepository.countByName(name) > 0) {
            throw new IllegalArgumentException("이미 등록된 확장자입니다.");
        }

        //저장
        Extension extension = Extension.builder()
                .name(name)
                .type(ExtensionType.CUSTOM)
                .build();
                
        extensionRepository.save(extension);
    }

    //고정 확장자 체크/해제
    @Transactional
    public void toggleFixedExtension(String name, boolean isChecked) {
    	//체크완료 되면
        if (isChecked) {
            //저장
        	if (extensionRepository.countByName(name) == 0) {
                Extension extension = Extension.builder()
                        .name(name)
                        .type(ExtensionType.FIXED)
                        .build();
                extensionRepository.save(extension);
            }
        }
        //체크가 해제되면
        else {
            //DB에서 삭제
            extensionRepository.findByName(name)
                    .ifPresent(extension -> extensionRepository.delete(extension));
        }
    }

    //커스텀 확장자 삭제
    @Transactional
    public void deleteExtensionById(Long id) {
        extensionRepository.deleteById(id);
    }
}