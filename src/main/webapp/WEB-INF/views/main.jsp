<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>파일 확장자 차단</title>
    <style>
        body { font-family: 'Noto Sans KR', sans-serif; padding: 50px; background-color: #f5f5f5; }
        .container { max-width: 800px; margin: 0 auto; background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        h1 { border-bottom: 2px solid #333; padding-bottom: 10px; margin-bottom: 30px; font-size: 24px; }
        
        /* 섹션 스타일 */
        .section { margin-bottom: 20px; display: flex; align-items: flex-start; }
        .label { width: 120px; font-weight: bold; padding-top: 5px; }
        .content { flex: 1; }

        /* 고정 확장자 (체크박스) */
        .checkbox-group label { margin-right: 15px; cursor: pointer; }
        .checkbox-group input { margin-right: 5px; transform: scale(1.2); }

        /* 커스텀 확장자 (입력창) */
        .input-group { display: flex; gap: 10px; margin-bottom: 10px; }
        input[type="text"] { flex: 1; padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; }
        button#addBtn { padding: 10px 20px; background: #6c757d; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; }
        button#addBtn:hover { background: #5a6268; }

        /* 확장자 태그 리스트 박스 */
        .tag-box { 
            border: 1px solid #ddd; border-radius: 4px; padding: 15px; min-height: 200px; 
            display: flex; flex-wrap: wrap; align-content: flex-start; gap: 10px; 
        }
        .count-text { font-size: 13px; color: #666; margin-bottom: 8px; display: block; }
        
        .tag { 
            background: #f8f9fa; border: 1px solid #ced4da; padding: 5px 12px; border-radius: 20px; 
            font-size: 14px; color: #495057; display: inline-flex; align-items: center; 
        }
        .tag span { margin-right: 8px; }
        .del-btn { cursor: pointer; font-weight: bold; color: #adb5bd; font-size: 16px; }
        .del-btn:hover { color: #dc3545; }
    </style>
</head>
<body>

<div class="container">
    <h1>◎ 파일 확장자 차단</h1>

    <div class="section">
        <div class="label">고정 확장자</div>
        <div class="content checkbox-group">
            <c:forEach var="ext" items="${fixedExts}">
                <label>
                    <input type="checkbox" class="fixed-chk" value="${ext}"
                        <%-- DB에 포함되어 있으면 체크 표시 --%>
                        <c:if test="${checkedNames.contains(ext)}">checked</c:if>
                    > ${ext}
                </label>
            </c:forEach>
        </div>
    </div>

    <div class="section">
        <div class="label">커스텀 확장자</div>
        <div class="content">
            <div class="input-group">
                <input type="text" id="customInput" placeholder="확장자 입력 (최대 20자)" maxlength="20">
                <button id="addBtn">+ 추가</button>
            </div>

            <div class="tag-box">
                <span class="count-text" id="counter">${customCount} / 200</span>
                
                <div id="tagList">
                    <%-- DB에 저장된 커스텀 확장자들을 하나씩 꺼내서 태그로 만듦 --%>
                    <c:forEach var="item" items="${customList}">
                        <div class="tag">
                            <span>${item.name}</span>
                            <%-- 삭제 버튼에 ID를 숨겨둠 --%>
                            <span class="del-btn" onclick="deleteExtension(${item.id})">X</span>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    //고정 확장자 체크박스 클릭 이벤트
    document.querySelectorAll('.fixed-chk').forEach(chk => {
        chk.addEventListener('change', function() {
            const name = this.value;
            const isChecked = this.checked;

         // AJAX 요청
            fetch('/api/extension/check?name=' + name + '&isChecked=' + isChecked, {
                method: 'POST'
            }).then(res => res.text()).then(result => {
                if(result !== 'success') {
                    alert('저장에 실패했습니다.');
                    this.checked = !isChecked; // 실패하면 체크 상태 되돌림
                }
            });
        });
    });

    //커스텀 확장자 추가 버튼 클릭
    document.getElementById('addBtn').addEventListener('click', addCustomExtension);
    
    //엔터키 적용
    document.getElementById('customInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') addCustomExtension();
    });

    function addCustomExtension() {
        const input = document.getElementById('customInput');
        const name = input.value.trim();

        if (!name) {
            alert('확장자를 입력해주세요.');
            return;
        }

        // AJAX 요청
        fetch('/api/extension/custom?name=' + encodeURIComponent(name), {
            method: 'POST'
        }).then(res => res.text()).then(result => {
            if (result === 'success') {
                location.reload(); // 성공하면 화면 새로고침해서 목록 갱신
            } else {
                alert(result);
                input.value = ''; // 입력창 비우기
                input.focus();
            }
        });
    }

    //삭제 버튼 클릭
    function deleteExtension(id) {
        if (!confirm('정말 삭제하시겠습니까?')) return;

        fetch('/api/extension/custom/' + id, {
            method: 'DELETE'
        }).then(res => res.text()).then(result => {
            if (result === 'success') {
                location.reload(); // 성공하면 새로고침
            } else {
                alert('삭제 실패');
            }
        });
    }
</script>

</body>
</html>