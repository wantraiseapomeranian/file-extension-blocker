package com.example.assignment.controller;

import com.example.assignment.entity.Extension;
import com.example.assignment.service.ExtensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ExtensionController {

    private final ExtensionService extensionService;

    //고정 확장자 리스트
    private final String[] FIXED_EXTS = {"bat", "cmd", "com", "cpl", "exe", "scr", "js"};

    //메인 화면
    @GetMapping("/")
    public String home(Model model) {
        //DB에 저장된 고정 확장자 가져오기
        List<Extension> fixedList = extensionService.getFixedExtensions();
        //JSP에서 이름만 뽑아서 Set으로 만듦
        Set<String> checkedNames = fixedList.stream()
                .map(Extension::getName)
                .collect(Collectors.toSet());

        //DB에 저장된 커스텀 확장자 가져오기
        List<Extension> customList = extensionService.getCustomExtensions();

        //데이터 담아서 JSP로 보내기
        model.addAttribute("fixedExts", FIXED_EXTS);       // 전체 고정 확장자 목록 (화면 그리기용)
        model.addAttribute("checkedNames", checkedNames);  // 현재 체크된 놈들 (체크박스 표시용)
        model.addAttribute("customList", customList);      // 커스텀 확장자 리스트
        model.addAttribute("customCount", customList.size()); // 현재 개수 (0/200)

        return "main"; //main.jsp 로 이동
    }

    //고정 확장자 체크/해제
    @PostMapping("/api/extension/check")
    @ResponseBody
    public String toggleFixed(@RequestParam("name") String name, 
                              @RequestParam("isChecked") boolean isChecked) {
        //리스트에 없는 이상한 확장자가 들어오면 무시
        if (!Arrays.asList(FIXED_EXTS).contains(name)) {
            return "fail";
        }
        extensionService.toggleFixedExtension(name, isChecked);
        return "success";
    }

    //커스텀 확장자 추가
    @PostMapping("/api/extension/custom")
    @ResponseBody
    public String addCustom(@RequestParam("name") String name) {
        name = name.trim().toLowerCase();
        
        try {
            //고정 확장자 리스트에 있는 건지 체크
            if (Arrays.asList(FIXED_EXTS).contains(name)) {
                return "이미 고정 확장자에 존재하는 항목입니다.";
            }
            
            extensionService.addCustomExtension(name);
            return "success";
            
        } catch (IllegalArgumentException e) {
            //에러 메시지 보내기
            return e.getMessage();
        }
    }

    //커스텀 확장자 삭제
    @DeleteMapping("/api/extension/custom/{id}")
    @ResponseBody
    public String deleteCustom(@PathVariable("id") Long id) {
        extensionService.deleteExtensionById(id);
        return "success";
    }
}