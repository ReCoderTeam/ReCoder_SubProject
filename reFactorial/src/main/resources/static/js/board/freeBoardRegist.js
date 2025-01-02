let optionCount = 2; // 기본 항목 개수
const maxOptions = 5; // 최대 항목 수 설정
const minOptions = 2; // 최소 항목 수 설정

// 항목 추가 버튼 클릭 시
document.getElementById('add-option').addEventListener('click', function () {
    const errorMessage = document.getElementById('error-message');
    const pollContainer = document.getElementById('poll-options');
    const addButton = document.getElementById('add-option');

    if (optionCount >= maxOptions) {
        errorMessage.style.display = 'block'; // 최대 항목 수 경고 메시지
        addButton.style.display = 'none'; // 버튼 숨기기
        return;
    }

    errorMessage.style.display = 'none'; // 정상 추가 시 경고 메시지 숨기기

    optionCount++;

    // 새 항목 추가
    const newOption = document.createElement('div');

    newOption.classList.add('input-group-item');
    newOption.innerHTML = `
                            <label for="option${optionCount}">항목 ${optionCount} : </label>
                            <input type="text" name="option${optionCount}" placeholder="내용을 입력하세요">
                            <button type="button" class="delete-option">삭제</button>`;
    pollContainer.appendChild(newOption);

    // 삭제 버튼 이벤트 추가
    newOption.querySelector('.delete-option').addEventListener('click', function () {
        // 항목 삭제 전, 최소 항목 수 체크
        if (optionCount <= minOptions) {
            alert(`최소 ${minOptions}개의 항목은 유지해야 합니다.`);
            return; // 최소 항목 수 이하로 삭제 방지
        }

        pollContainer.removeChild(newOption); // 항목 삭제
        optionCount--; // 항목 개수 감소

        // 삭제 후 항목 번호 다시 정렬
        updateOptionNumbers();

        // 최소 항목 수를 유지하도록 처리
        updateDeleteButtons();

        // "항목 추가" 버튼 다시 보이게 하기
        if (optionCount < maxOptions) {
                addButton.style.display = 'inline-block';
            }
    });

    // 최대 항목 수 도달 시 추가 버튼 숨기기
    if (optionCount >= maxOptions) {
        addButton.style.display = 'none'; // 추가 버튼 숨기기
    }

    // 삭제 버튼이 최소 항목 수가 되면 숨기기
    updateDeleteButtons();
});

// 초기 항목에 삭제 버튼 이벤트 추가 (항목 3부터는 삭제 버튼 추가)
document.querySelectorAll('.delete-option').forEach(button => {
button.addEventListener('click', function () {
    // 항목 삭제 전, 최소 항목 수 체크
    if (optionCount <= minOptions) {
        alert(`최소 ${minOptions}개의 항목은 유지해야 합니다.`);
        return; // 최소 항목 수 이하로 삭제 방지
    }

    const optionGroup = button.closest('.input-group-item');
    optionGroup.remove(); // 항목 삭제
    optionCount--; // 항목 개수 감소

    // 삭제 후 항목 번호 다시 정렬
    updateOptionNumbers();

    // 최소 항목 수를 유지할 수 있도록 버튼을 보이게 처리
    updateDeleteButtons();

    // "항목 추가" 버튼 다시 보이게 하기
    if (optionCount < maxOptions) {
        document.getElementById('add-option').style.display = 'inline-block';
    }

    });
});

// 삭제 후 항목 번호를 재정렬하는 함수
function updateOptionNumbers() {
    const pollContainer = document.getElementById('poll-options');
    const options = pollContainer.querySelectorAll('.input-group-item');

    // 각 항목에 대해 번호를 다시 매김
    options.forEach((option, index) => {
        const label = option.querySelector('label');
        label.setAttribute('for', `option${index + 1}`);  // id 속성 수정
        label.innerHTML = `항목 ${index + 1} :`;  // 항목 번호 업데이트
        const input = option.querySelector('input');
        input.setAttribute('id', `option${index + 1}`);  // input id 수정
    });

    optionCount = options.length; // optionCount를 업데이트
}

// 삭제 버튼 표시 여부 업데이트
function updateDeleteButtons() {
    const deleteButtons = document.querySelectorAll('.delete-option');

    // 항목이 2개 이하로 남은 경우 삭제 버튼 숨기기
    if (optionCount <= minOptions) {
        deleteButtons.forEach(button => {
            button.style.display = 'none';
        });
    } else {
        deleteButtons.forEach(button => {
            button.style.display = 'inline-block'; // 삭제 버튼 보이기
        });
    }

    // "항목 추가" 버튼 표시 여부 업데이트
    const addButton = document.getElementById('add-option');
    if (optionCount < maxOptions) {
        addButton.style.display = 'inline-block'; // 항목 추가 버튼 보이기
    } else {
        addButton.style.display = 'none'; // 항목 추가 버튼 숨기기
    }
}

function onChangeSelectOption(){
    const categoryCode = document.querySelector('#categoryCode').value;
    const voteContainer = document.querySelector('#vote-container');

    const postFileBox = document.querySelector(".postFileBox");
    const freeBoardForm = document.querySelector('#freeBoardForm');

    if(categoryCode === "4"){
        postFileBox.style.display = 'none';
        voteContainer.style.display = 'block';
        freeBoardForm.removeAttribute('enctype');
    }else if(categoryCode === "3"){
        postFileBox.style.display = 'flex'
        voteContainer.style.display = 'none';

        // form에 enctype 속성 추가
        freeBoardForm.setAttribute('enctype', 'multipart/form-data');
    }else{
        voteContainer.style.display = 'none';
        postFileBox.style.display = 'none';
        freeBoardForm.removeAttribute('enctype');
    }
}

function initSelectOption(){
    const Url = new URLSearchParams(window.location.search);

    const categoryCodeValue = Url.get("categoryCode");

    console.log("categoryCodeValue: ",categoryCodeValue)
    const voteContainer = document.querySelector('#vote-container');

    const postFileBox = document.querySelector(".postFileBox");
    const freeBoardForm = document.querySelector('#freeBoardForm');
    const postId = document.querySelector('#postId');
    //
    const addOption = document.querySelector('#add-option');

    // 수정일 경우에는 항목 추가 버튼을 숨김
    if(postId.value){
        addOption.style.display = 'none';
    }

    // 카테고리 코드가 4일 경우 투표항목 노출
    if(categoryCodeValue == 4){
        postFileBox.style.display = 'none';
        voteContainer.style.display = 'block';
        freeBoardForm.removeAttribute('enctype');

    // 카테고리 코드가 3일 경우 첨부파일 노출
    }else if(categoryCodeValue == 3){
        postFileBox.style.display = 'flex'
        voteContainer.style.display = 'none';

    // form에 enctype 속성 추가
        freeBoardForm.setAttribute('enctype', 'multipart/form-data');
    }else{
        voteContainer.style.display = 'none';
        postFileBox.style.display = 'none';
        freeBoardForm.removeAttribute('enctype');
    }
}

// 처음 페이지 로딩 시 최소 항목 수 보장
updateDeleteButtons();  // 삭제버튼 표시여부
initSelectOption();     // 카테고리 3,4일 때 첨부파일,투표 표시


