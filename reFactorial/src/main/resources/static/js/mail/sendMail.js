function updateFileList() {
    const fileInput = document.getElementById('mailFiles');
    const fileListContainer = document.getElementById('fileListContainer');
    fileListContainer.innerHTML = ''; // 기존 파일 목록 초기화

    for (let i = 0; i < fileInput.files.length; i++) {
        const file = fileInput.files[i];
        const fileLink = document.createElement('div');
        fileLink.className = 'attachmentFile';
        fileLink.textContent = file.name; // 파일 이름 표시

        // 삭제 버튼 추가
        const removeButton = document.createElement('button');
        removeButton.textContent = '삭제';
        removeButton.onclick = function () {
            removeFile(file);
        };

        fileLink.appendChild(removeButton);
        fileListContainer.appendChild(fileLink);
    }
}

function removeFile(file) {
    const fileInput = document.getElementById('mailFiles');
    const fileList = Array.from(fileInput.files);
    const index = fileList.indexOf(file);
    if (index > -1) {
        fileList.splice(index, 1);
        const dataTransfer = new DataTransfer();
        fileList.forEach(f => dataTransfer.items.add(f));
        fileInput.files = dataTransfer.files;
        updateFileList(); // 파일 목록을 다시 업데이트
    }
}

const selectedEmployees = []; // 선택된 직원 목록

// 처음에 직원 목록을 로드합니다.
document.addEventListener('DOMContentLoaded', function () {
    fetchEmployees('');
});

// 직원 검색을 처리하는 함수
function searchReceiver() {
    const searchInput = document.getElementById('searchReceiverInput').value;
    fetchEmployees(searchInput);
}

// 직원 목록을 fetch 로 가져오는 함수
function fetchEmployees(searchQuery) {
    fetch(`/mail/searchReceiver?searchReceiverInput=${searchQuery}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
    })
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            const table = document.getElementById('employeeList');
            table.innerHTML = ''; // 기존 목록을 지우고
            data.forEach(function (employee) {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td><input type="checkbox" class="employee-checkbox" value="${employee.empId}" data-emp-name="${employee.empName}"></td>
                    <td>${employee.empId}</td>
                    <td>${employee.deptName}</td>
                    <td>${employee.positionName}</td>
                    <td>${employee.empName}</td>
                `;
                table.appendChild(row);
            });
        });
}

function confirmSelection() {
    const checkboxes = document.querySelectorAll('.employee-checkbox:checked');
    checkboxes.forEach(function (checkbox) {
        const empId = checkbox.value;
        const empName = checkbox.getAttribute('data-emp-name');

        if (!selectedEmployees.some(function (emp) {
            return emp.empId === empId;
        })) {
            selectedEmployees.push({empId: empId, empName: empName});
            updateSelectedReceivers();
        }
    });

    $('#searchItems').modal('hide');
}

function updateSelectedReceivers() {
    const container = document.getElementById('selectedReceivers');
    container.innerHTML = '';
    const hiddenInput = document.getElementById('receiverEmpIds');  // 변경된 부분

    selectedEmployees.forEach(function (employee) {
        const div = document.createElement('div');
        div.className = 'selected-employee';
        div.innerHTML = `${employee.empName} <button type="button" onclick="removeReceiver('${employee.empId}')">삭제</button>`;
        container.appendChild(div);
    });

    // 선택된 직원 ID를 숨겨진 input 에 쉼표로 구분하여 설정
    hiddenInput.value = selectedEmployees.map(function (emp) {
        return emp.empId;
    }).join(',');
}

function removeReceiver(empId) {
    const index = selectedEmployees.findIndex(function (emp) {
        return emp.empId === empId;
    });
    if (index > -1) {
        selectedEmployees.splice(index, 1);
        updateSelectedReceivers();
    }
}

document.getElementById('sendMailForm').addEventListener('submit', function(event) {
    const receiverEmpIds = document.getElementById('receiverEmpIds').value;
    if (!receiverEmpIds.trim()) {
        event.preventDefault(); // 폼 제출 막기
        alert('수신자를 선택해주세요.');
    }
});
