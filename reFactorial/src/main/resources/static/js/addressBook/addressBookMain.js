// 검색 상태 변수
let debounceTimeout; // 디바운스 타이머
let preValue = ""; // 이전 검색어

document.addEventListener('DOMContentLoaded', function () {

    const searchInput = document.getElementById('search-input');

    // 페이지 로드 시 전체 직원 조회 실행
    fetchEmployees();

    // 검색 입력에서 입력할 때마다 실시간 검색 실행
    searchInput.addEventListener('input', (event) => {
        clearTimeout(debounceTimeout); // 이전 타이머 취소
        debounceTimeout = setTimeout(() => {
            const presentValue = event.target.value.trim();
            if (preValue !== presentValue) {
                if (presentValue === "") {
                    fetchEmployees(); // 입력값이 비어 있으면 전체 조회 실행
                } else {
                    searchEmployees(presentValue); // 검색어가 변경되었을 때 fetch 실행
                }
                preValue = presentValue; // 이전 값 갱신
            }
        }, 200); // 200ms 디바운스
    });
});

// 전체 직원 조회 함수
function fetchEmployees() {
    fetch('/addressBook/employees')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => renderEmployees(data))
        .catch(error => console.error('Error fetching employees:', error));
}

// 검색 실행 함수
function searchEmployees(keyword) {
    fetch(`/addressBook/search?keyword=${keyword}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => renderEmployees(data))
        .catch(error => {
            console.error('Error fetching employees:', error);
            // 에러 메시지를 사용자에게 표시
            const tableBody = document.getElementById('employee-list');
            tableBody.innerHTML = `<div class="table-row">검색 중 오류가 발생했습니다.</div>`;
        });
}

// 데이터를 테이블에 렌더링하는 함수
function renderEmployees(data) {
    const tableBody = document.getElementById('employee-list');
    tableBody.innerHTML = ""; // 기존 데이터를 초기화

    if (data.length === 0) {
        tableBody.innerHTML = `<div class="table-row">검색 결과가 없습니다.</div>`;
        return;
    }

    data.forEach(employee => {
        const row = document.createElement('div');
        row.className = "table-row";

        function formatPhoneNumber(phoneNumber) {
            if (!phoneNumber) return '';
            return phoneNumber.replace(/(\d{3})(\d{3,4})(\d{4})/, '$1-$2-$3'); // 전화번호 포맷팅
        }

        row.innerHTML = `
            <div class="table-cell">${employee.id}</div>
            <div class="table-cell">${employee.name}</div>
            <div class="table-cell">${employee.deptName}</div>
            <div class="table-cell">${employee.positionName}</div>
            <div class="table-cell">${employee.email}</div>
            <div class="table-cell">${formatPhoneNumber(employee.phone)}</div>
        `;
        tableBody.appendChild(row);
    });
}

// 스크롤 감지 및 버튼 표시
const scrollableElement = document.querySelector('.main-content'); // 스크롤이 발생하는 요소 선택

scrollableElement.addEventListener('scroll', function () {
    console.log('Scroll event triggered on .main-content'); // 스크롤 이벤트 실행 여부 확인
    const scrollToTopButton = document.getElementById('scrollToTop');
    if (scrollableElement.scrollTop > 200) { // 스크롤 위치가 200px 이상일 때 버튼 표시
        scrollToTopButton.style.display = 'block';
    } else {
        scrollToTopButton.style.display = 'none';
    }
});

// 위로 이동 버튼 클릭 이벤트
document.getElementById('scrollToTop').addEventListener('click', function () {
    scrollableElement.scrollTo({
        top: 0, // 최상단으로 이동
        behavior: 'smooth' // 부드럽게 이동
    });
});







