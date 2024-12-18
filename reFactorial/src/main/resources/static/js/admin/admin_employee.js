const menuMainBox = document.querySelector(".menuMainBox");
const selectedMenu = document.querySelectorAll(".sideMenuUl li");
const modifyEmpInfo = document.querySelector("#modifyEmpInfo");
const registeEmp = document.querySelector("#registeEmp");
const updateAtt = document.querySelector("#updateAtt");

let sendDept = 0;
let sendSearchEmpName = '';

let searchDept = null;
let searchEmpName = null;

document.addEventListener("DOMContentLoaded", function () {
    menuMainBox.innerHTML = `
                        <div class="employeeListBox">
                            <div class="searchBox">
                                <div>
                                    <label for="searchDept">부서 : </label>
                                    <select name="searchDept" id="searchDept">
                                        <option value="0" id="dept0">전체</option>
                                        <option value="1" id="dept1">인사팀</option>
                                        <option value="2" id="dept2">개발팀</option>
                                        <option value="3" id="dept3">마케팅팀</option>
                                        <option value="4" id="dept4">회계팀</option>
                                        <option value="5" id="dept5">영업팀</option>
                                    </select>
                                </div>
                                <div>
                                    <label for="searchEmpName">이름 : </label>
                                    <input type="text" name="searchEmpName" id="searchEmpName">
                                </div>
                            </div>

                            <div class="employeeList">
                                    <!--데이터 들어올 공간-->
                            </div>
                        </div>
                    `;

    function fetchEmployeeList(sendDept,sendSearchEmpName){
        const employeeListBox = document.querySelector(".employeeList");

        fetch(`/admin/getAllEmployee?sendDept=${sendDept}&sendSearchEmpName=${sendSearchEmpName}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {

                console.log("전체 데이터 : ",data)

                let employeeList = '';

                data.userList.forEach(employee => {
                    employeeList += `<div class="empItem" data-emp-id = "${employee.empId}">
                                            <div class="profileImg">
<!--                                                <img src=${employee.profile} alt="사진">-->
                                                <img src="#" alt="사진">
                                            </div>

                                            <div>${employee.empName}</div>
                                            <div>${employee.empId}</div>
                                            <div class="deptPosition">
                                                <div>${employee.deptName}/</div>
                                                <div>${employee.positionName}</div>
                                            </div>
                                        </div>`
                });

                if (data.userList.length == 0){
                    employeeList = `<div class="notMatch">검색 조건에 맞는 사원이 없습니다.</div>`
                }

                employeeListBox.innerHTML = employeeList;

                const empItems = document.querySelectorAll(".empItem")
                empItems.forEach(item => {
                    item.addEventListener("click", () => {
                        const empId = item.dataset.empId;

                        console.log("보여줄 대상자 : ", empId)

                        fetch(`/admin/modifyEmpInfo?empId=${empId}`)
                            .then(response => response.json())
                            .then(data => {

                                console.log("data : ", data)

                                fetch('/admin/addEmployeeFragment', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/json',
                                    },
                                    body: JSON.stringify({
                                        ModifyUser: data.ModifyUser// ModifyUser 객체를 서버로 전달
                                    }),
                                })
                                    .then(response => response.text())
                                    .then(html => {
                                        const form = new DOMParser().parseFromString(html, "text/html").querySelector('form');

                                        menuMainBox.innerHTML = '';
                                        menuMainBox.appendChild(form);
                                    })

                            })
                            .catch((error) => console.error("Fetch error: ", error));
                    })
                })

            })
            .catch(error => {
                console.error("Fetch error: ", error);
            });
    }

    fetchEmployeeList(sendDept, sendSearchEmpName);

    searchDept = document.querySelector("#searchDept")
    searchEmpName = document.querySelector("#searchEmpName")

    searchDept.addEventListener("change" , () =>{
        console.log("searchDept : ",searchDept.value)
        sendDept = searchDept.value
        console.log("sendDept : ",sendDept, " sendSearchEmpName : ",sendSearchEmpName)
        fetchEmployeeList(sendDept,sendSearchEmpName)
    })

    searchEmpName.addEventListener("input" , () =>{
        console.log("searchEmpId : ",searchEmpName.value)
        sendSearchEmpName = searchEmpName.value
        console.log("sendDept : ",sendDept, " sendSearchEmpName : ",sendSearchEmpName)
        fetchEmployeeList(sendDept,sendSearchEmpName)
    })
})

// 등록하기
registeEmp.addEventListener("click", () => {
    fetch("/admin/addEmployeeFragment")
        .then(response => response.text())
        .then(html => {
            // HTML에서 form 부분만 추출
            const form = new DOMParser().parseFromString(html, "text/html").querySelector('form');

            menuMainBox.innerHTML = '';
            menuMainBox.appendChild(form);
        })

    selectedMenu.forEach(menu => {
        menu.classList.remove("selected")
    })

    registeEmp.classList.add("selected")
})

// 정보수정 클릭
modifyEmpInfo.addEventListener("click", () => {
    window.location.reload();
})

// 근태 수정
let page = 1;

// fetchData 함수는 전역에서 사용할 수 있도록 밖으로 이동
const fetchData = (page = 1) => {
    const selectedDay = document.getElementById('searchAttDate').value;

    fetch(`/admin/getByDateAtt?selectedDay=${encodeURIComponent(selectedDay)}&page=${page}&size=10`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log("전체 사원 근태 data: ", data);

            // 테이블의 tbody에 데이터를 추가
            const empAttTable = document.getElementById("empAttTable");
            empAttTable.innerHTML = '';

            data.items.forEach((att) => {
                fetch('/user/getNameById', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        empId: att.empId
                    })
                })
                    .then(response => response.json())
                    .then(data => {
                        const attEmpName = data.empName;

                        const row = `
                        <tr>
                            <td class="empId">${att.empId}</td>
                            <td class="empName">${attEmpName}</td>
                            <td class="attDate">${att.attDate}</td>
                            <td class="attStatus">
                                <select name="attStatus" id="attStatus" data-emp-id="${att.empId}" data-att-date="${att.attDate}" onchange="logAttStatus(this)">
                                    <option value="지각" ${att.attStatus === '지각' ? 'selected' : ''}>지각</option>
                                    <option value="연차" ${att.attStatus === '연차' ? 'selected' : ''}>연차</option>
                                    <option value="반차" ${att.attStatus === '반차' ? 'selected' : ''}>반차</option>
                                    <option value="정상 출근" ${att.attStatus === '정상 출근' || att.attStatus === '출근' ? 'selected' : ''}>정상 출근</option>
                                </select>
                            </td>
                        </tr>
                    `;
                        empAttTable.innerHTML += row;

                        // select 요소에 change 이벤트 리스너를 추가
                        const selectElement = document.querySelector(`#attStatus[data-emp-id="${att.empId}"][data-att-date="${att.attDate}"]`);
                        selectElement.addEventListener('change', (event) => logAttStatus(event.target));
                    })
                    .catch(error => {
                        console.error("Fetch error: ", error);
                    });
            });

            const pagination = document.querySelector('.pagination');
            const totalPages = data.totalPages;

            pagination.innerHTML = '';  // 기존 페이지네이션 초기화

            for (let i = 1; i <= totalPages; i++) {
                const pageButton = document.createElement('button');
                pageButton.textContent = i;
                pageButton.addEventListener('click', () => {
                    page = i
                    fetchData(page);  // 해당 페이지 데이터 가져오기
                });
                pagination.appendChild(pageButton);
            }
        })
        .catch(error => {
            console.error("Fetch error: ", error);
        });
};

// logAttStatus 함수는 전역에서 호출될 수 있도록 정의
function logAttStatus(selectElement) {
    const empId = selectElement.getAttribute('data-emp-id');
    const attDate = selectElement.getAttribute('data-att-date');
    const selectedStatus = selectElement.value;

    fetch('/admin/modifyEmpAtt', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            empId: empId,
            attDate: attDate,
            selectedStatus: selectedStatus
        })
    })
    .then(response => response.json())
    .then(data => {
        // 근태 상태가 변경된 후 데이터를 새로 고침
        // fetch(page)
    })
}

// updateAtt 버튼 클릭 시 이벤트
updateAtt.addEventListener("click", () => {
    menuMainBox.innerHTML = `
            <div class="allEmpAttListBox">
                <div class="searchAttBox">
                    <form>
                        <div class="searchAttIdBox">
                            <label for="searchAttId">사번</label>
                            <input type="text" id="searchAttId" name="searchAttId">
                            <button type="button">조회</button>
                        </div>
                        <input type="date" id="searchAttDate" name="searchAttDate">
                    </form>
                </div>
                <div class="empAttList">
                    <div class="empAttListTitle">
                        <table>
                            <tr>
                                <th class="empId">사번</th>
                                <th class="empName">이름</th>
                                <th class="attDate">날짜</th>
                                <th class="attStatus">근태 상태</th>
                            </tr>
                        </table>
                    </div>
                    <div class="empAttListContent">
                        <table id="empAttTable"></table>
                    </div>
                </div>
                <div class="pagination"></div>
            </div>
        `;

    // 오늘 날짜를 가져와서 기본값으로 설정
    const today = new Date();
    const formattedDate = today.toISOString().split('T')[0];
    document.getElementById('searchAttDate').value = formattedDate;

    const searchAttDateInput = document.getElementById('searchAttDate');

    // 처음 데이터 로드
    fetchData(1);
    page = 1;

    // 날짜가 변경될 때마다 데이터 새로고침
    searchAttDateInput.addEventListener('change', () => {
        fetchData(1);
        page = 1
    });

    selectedMenu.forEach(menu => {
        menu.classList.remove("selected")
    })

    updateAtt.classList.add("selected")
});

