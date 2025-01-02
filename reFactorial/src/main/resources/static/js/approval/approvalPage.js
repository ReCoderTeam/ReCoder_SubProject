function searchOnEnter(event, approverId) {
    if (event.key === 'Enter') {  // 엔터 키인지 확인
        event.preventDefault();  // form 자동 제출 방지

        if (approverId === 'firstApprover') {
            openFirstApproverPopup(approverId);
        } else if (approverId === 'midApprover') {
            openMidApproverPopup(approverId);
        } else if (approverId === 'finalApprover') {
            openFinalApproverPopup(approverId);
        }
    }
}

// 최초 승인자 검색 팝업 열기
function openFirstApproverPopup(firstApprover) {
    const inputValue = document.getElementById(firstApprover)?.value || ''; // 입력된 값 가져오기
    console.log('입력된 값:', inputValue);

    const screenWidth = window.screen.width;
    const screenHeight = window.screen.height;
    const popupWidth = 600;
    const popupHeight = 500;

    const popupLeft = (screenWidth - popupWidth) / 2;
    const popupTop = (screenHeight - popupHeight) / 2 - 50;

    const url = `/approvals/searchEmployee?name=${encodeURIComponent(inputValue)}`;
    window.open(
        url,
        'searchPopup',
        `width=${popupWidth},height=${popupHeight},top=${popupTop},left=${popupLeft},scrollbars=no,resizable=no`
    );
    window.targetInputId = 'firstApprover';
}

// 중간 승인자 검색 팝업 열기
function openMidApproverPopup(midApprover) {
    const inputValue = document.getElementById(midApprover)?.value || '';
    console.log('입력된 값:', inputValue);

    const screenWidth = window.screen.width;
    const screenHeight = window.screen.height;
    const popupWidth = 600;
    const popupHeight = 500;

    const popupLeft = (screenWidth - popupWidth) / 2;
    const popupTop = (screenHeight - popupHeight) / 2 - 50;

    const url = `/approvals/searchEmployee?name=${encodeURIComponent(inputValue)}`;
    window.open(
        url,
        'searchPopup',
        `width=${popupWidth},height=${popupHeight},top=${popupTop},left=${popupLeft},scrollbars=no,resizable=no`
    );
    window.targetInputId = 'midApprover';
}

// 최종 승인자 검색 팝업 열기
function openFinalApproverPopup(finalApprover) {
    const inputValue = document.getElementById(finalApprover)?.value || '';
    console.log('입력된 값:', inputValue);

    const screenWidth = window.screen.width;
    const screenHeight = window.screen.height;
    const popupWidth = 600;
    const popupHeight = 500;

    const popupLeft = (screenWidth - popupWidth) / 2;
    const popupTop = (screenHeight - popupHeight) / 2 - 50;

    const url = `/approvals/searchEmployee?name=${encodeURIComponent(inputValue)}`;
    window.open(
        url,
        'searchPopup',
        `width=${popupWidth},height=${popupHeight},top=${popupTop},left=${popupLeft},scrollbars=no,resizable=no`
    );
    window.targetInputId = 'finalApprover';
}

// 참조자 팝업 열기
function openReferrerPopup() {
    const screenWidth = window.screen.width;
    const screenHeight = window.screen.height;
    const popupWidth = 600;
    const popupHeight = 500;

    const popupLeft = (screenWidth - popupWidth) / 2;
    const popupTop = (screenHeight - popupHeight) / 2 - 50;

    const url = '/approvals/searchReferrers?name=';
    window.open(url, 'referrerPopup',
        `width=${popupWidth},height=${popupHeight},top=${popupTop},left=${popupLeft},scrollbars=no,resizable=no`);

    window.targetInputId = 'referrer-container'; // 참조자 리스트에 추가
}

// 부모 창에서 호출되는 함수로 참조자를 동적으로 추가
function addReferrer(name) {
    const container = document.getElementById('referrer-container');

    container.classList.add('has-referrers');

    const existingInputs = container.getElementsByClassName('referrer-input');
    for (let i = 0; i < existingInputs.length; i++) {
        if (existingInputs[i].value === name) {
            alert('이미 추가된 참조자입니다.');
            window.close(); // 중복일 경우에도 창이 닫히도록
            return;
        }
    }

    const input = document.createElement('input');
    input.type = 'text';
    input.name = 'referrerNames';
    input.value = name;
    input.readOnly = true;
    input.classList.add('referrer-input');
    container.appendChild(input);

    updateReferrersField();
}


//숨겨진 input 필드에 참조자 목록을 추가
function updateReferrersField() {
    const referrers = [];
    const referrerInputs = document.getElementsByClassName('referrer-input');

    for (let i = 0; i < referrerInputs.length; i++) {
        referrers.push(referrerInputs[i].value);
    }

    document.getElementById('referrers').value = referrers.join(',');
    console.log("referrers hidden field에 저장된 값: ", document.getElementById('referrers').value);
}

// 휴가신청서를 클릭하면 드롭다운과 날짜입력 필드 표시
function showLeaveTypeDropdown() {
    const category = document.getElementById('category').value;
    const leaveTypeDropdown = document.getElementById('leaveTypeDropdown');
    const leaveDate = document.getElementById('leaveDate');

    if (category === "category3") {  // 휴가 신청서 선택 시
        leaveTypeDropdown.style.display = 'flex';  // 드롭다운 보이기
        leaveDate.style.display = 'flex';
    } else {
        leaveTypeDropdown.style.display = 'none';  // 다른 카테고리 선택 시 드롭다운 숨기기
        leaveDate.style.display = 'none';
    }
}

// form submit 이벤트 리스너 추가
document.querySelector('form').addEventListener('submit', function(event) {
    event.preventDefault(); // 기본 제출 동작 중지

    // 필수 필드 검증
    const title = document.getElementById('title').value.trim();
    const category = document.getElementById('category').value;
    const content = document.getElementById('content').value.trim();

    // 에러 메시지 초기화
    let errorMessage = '';

    // 각 필드 검증
    if (!title) {
        errorMessage += '제목을 입력해주세요.\n';
    }

    if (!category) {
        errorMessage += '분류를 선택해주세요.\n';
    }

    if (!content) {
        errorMessage += '내용을 입력해주세요.\n';
    }

    // 에러가 있으면 alert로 표시
    if (errorMessage) {
        alert(errorMessage);
        return;
    }

    // 모든 검증을 통과하면 form 제출
    this.submit();
});



function createSnowflake() {
    const snowflake = document.createElement('div');
    snowflake.className = 'snowflake';

    // 랜덤 특성 부여
    const left = Math.random() * 100; // 랜덤 가로 위치
    const size = Math.random() * 10 + 15; // 15-25px 크기
    const duration = Math.random() * 5 + 8; // 8-13초 낙하 시간
    const color = [
        'rgba(176, 224, 230, 0.4)', // 연한 하늘색
        'rgba(173, 216, 230, 0.4)', // 밝은 하늘색
        'rgba(135, 206, 235, 0.4)', // 스카이블루
        'rgba(176, 196, 222, 0.4)', // 연한 스틸블루
        'rgba(188, 212, 230, 0.4)'  // 파스텔 하늘색
    ][Math.floor(Math.random() * 5)];

    // 랜덤 눈송이 모양
    snowflake.innerHTML = ['❄', '✦', '❅', '✧'][Math.floor(Math.random() * 4)];

    // 스타일 적용
    snowflake.style.left = `${left}%`;
    snowflake.style.fontSize = `${size}px`;
    snowflake.style.color = color;
    snowflake.style.animationDuration = `${duration}s`;

    // 약간의 흔들림 효과 추가
    const wobble = Math.random() * 20 - 10;
    snowflake.style.animationName = `snowfall-${wobble}`;

    // 컨테이너에 추가
    document.getElementById('snowContainer').appendChild(snowflake);

    // 애니메이션 끝나면 제거
    setTimeout(() => {
        snowflake.remove();
    }, duration * 1000);
}

// 초기 눈송이들 생성
for(let i = 0; i < 50; i++) {  // 초기눈송이 갯수
    setTimeout(createSnowflake, Math.random() * 8000);  //숫자를 줄이면 더 빠르게 초기 눈송이가 생성됨
                                                        //숫자를 키우면 더 천천히 생성됨
}

// 계속해서 새로운 눈송이 생성  클수록 조금
setInterval(createSnowflake, 800);

// 눈의 양: setInterval의 시간(150)을 조절
// 눈의 크기: size 변수의 범위(15-25px) 조절
// 낙하 속도: duration 변수의 범위(8-13초) 조절
// 색상: color 배열의 색상들 수정