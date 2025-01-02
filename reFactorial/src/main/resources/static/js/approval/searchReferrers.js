function selectReferrer(button) {
    const name = button.getAttribute('data-name'); // 버튼의 data-name 속성 값 가져오기
    if (window.opener) {
        if (typeof window.opener.addReferrer === 'function') {
            // 부모 창의 addReferrer 함수가 존재하는지 확인
            window.opener.addReferrer(name); // 부모 창의 addReferrer 호출
            window.close(); // 팝업 닫기
        } else {
            console.error('부모 창에 addReferrer 함수가 정의되어 있지 않습니다.');
        }
    } else {
        alert('부모 창이 없습니다.');
    }
}