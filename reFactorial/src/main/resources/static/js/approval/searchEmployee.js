function selectEmployee(button) {
    const name = button.getAttribute('data-name'); // 버튼의 data-name 속성 값 가져오기
    if (window.opener) {
        const targetInputId = window.opener.targetInputId; // 부모 창의 targetInputId 가져오기
        console.log('선택된 이름:', name);
        console.log('Target Input ID:', targetInputId);

        const targetInput = window.opener.document.getElementById(targetInputId);
        if (targetInput) {
            targetInput.value = name; // 선택된 이름을 부모 창의 input 에 설정
        } else {
            alert('부모 창의 입력 필드를 찾을 수 없습니다.');
        }
        window.close(); // 팝업 닫기
    } else {
        alert('부모 창이 없습니다.');
    }
}