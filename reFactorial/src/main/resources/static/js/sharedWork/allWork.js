
    // 이벤트에 대한 행동 추가
    document.addEventListener('DOMContentLoaded', function() {
    // html 요소 불러오는 곳
    var calendarEl = document.getElementById('calendar');
    var modal = document.getElementById('modal');
    var modalOverlay = document.getElementById('modal-overlay');
    var closeModalBtn = document.getElementById('closeModalBtn');
    var saveEventBtn = document.getElementById('saveEventBtn');
    var deleteEventBtn = document.getElementById('deleteEventBtn');
    var eventTitleInput = document.getElementById('eventTitle');
    var eventDescriptionInput = document.getElementById('eventDescription');
    var eventStartDateInput = document.getElementById('eventStartDate');
    var eventEndDateInput = document.getElementById('eventEndDate');
    var eventColorSelect = document.getElementById('eventColor');


    // 캘린더 설정
    var calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: 'dayGridMonth',
    locale: 'ko',
    fixedWeekCount: false,
    displayEventTime: false,
    events: '/event',

    // 일정 렌더링 시 색상 적용
    eventDidMount: function(info) {
    if (info.event.extendedProps.color) {
    info.el.style.backgroundColor = info.event.extendedProps.color;
}
},

    // 날짜 클릭 시 일정 추가 모달
    dateClick: function(info) {
    eventTitleInput.value = '';
    eventDescriptionInput.value = '';
    eventStartDateInput.value = info.dateStr + "T09:00"; // 기본값 설정
    eventEndDateInput.value = info.dateStr + "T23:59";   // 기본값 설정

    // 클릭한 날짜를 시작 날짜로 설정
    eventStartDateInput.value = info.dateStr;  // 클릭한 날짜를 시작 날짜 필드에 적용
    eventEndDateInput.value = info.dateStr;    // 종료 날짜도 시작 날짜와 동일하게 설정 (기본값 설정)

    saveEventBtn.onclick = saveEvent;

    deleteEventBtn.style.display = 'none';

    openModal();
},

    // 일정 클릭 시 상세 보기 모달
    eventClick: function(info) {
    var event = info.event;
    eventTitleInput.value = event.title;
    eventDescriptionInput.value = event.extendedProps.description;
    eventStartDateInput.value = event.startStr;
    eventEndDateInput.value = event.endStr ? event.endStr.split('T')[0] : event.startStr;
    eventColorSelect.value = event.borderColor; // 색상 적용

    // 수정
    saveEventBtn.onclick = function() {
    // 이벤트 업데이트를 위한 임시 객체 생성
    const updatedProps = {
    title: eventTitleInput.value,
    start: eventStartDateInput.value,
    end: eventEndDateInput.value || null,
    backgroundColor: eventColorSelect.value,
    borderColor: eventColorSelect.value,
    extendedProps: {
    description: eventDescriptionInput.value,
    color: eventColorSelect.value
}
};



    if (eventEndDateInput.value < eventStartDateInput.value) {
    alert('종료 날짜는 시작 날짜보다 앞설 수 없습니다.');
    return;
}

    const updatedEventData = {
    workId: event.id,  // 수정할 일정을 구분하는 고유 ID
    workTitle: eventTitleInput.value,  // 수정된 제목
    workExplanation: eventDescriptionInput.value,  // 수정된 설명
    workSchedule: eventStartDateInput.value,  // 수정된 시작 날짜
    deadLine: eventEndDateInput.value,  // 수정된 종료 날짜
    workColor: eventColorSelect.value   // 색상
};


    if (confirm('이 일정을 정말로 수정하시겠습니까?')) {
    fetch('/workModify', {
    method: 'POST',
    headers: {
    'Content-Type': 'application/json'
},
    body: JSON.stringify(updatedEventData),
})
    .then(response => {
    if (response.ok) {
    alert('일정이 수정되었습니다.');
    // 기존 이벤트 제거
    event.remove();
    // 새로운 이벤트 추가
    calendar.addEvent(updatedProps);
    closeModal();
} else {
    alert('수정 실패');
}
})
    .catch(error => {
    console.error('Error:', error);
});
}

    closeModal();
};

    // 삭제하기
    deleteEventBtn.onclick = function() {
    if (confirm('이 일정을 정말로 삭제하시겠습니까?')) {
    fetch('/workDelete', {
    method: 'POST', // POST를 사용하여 데이터 전달
    headers: {
    'Content-Type': 'application/json'
},
    body: JSON.stringify({ workId: event.id }) // `workId`를 JSON으로 보냄
})
    .then(response => {
    if (response.ok) {
    alert('일정이 삭제되었습니다.');
    event.remove();
    closeModal();
} else {
    alert('삭제 실패');
}
})
    .catch(error => {
    console.error('Error:', error);
});
}
};

    deleteEventBtn.style.display = 'inline-block';
    openModal();
}
});

    calendar.render();

    // 모달 열기
    function openModal() {
    modal.style.display = 'block';
    modalOverlay.style.display = 'block';
}

    // 모달 닫기
    function closeModal() {
    modal.style.display = 'none';
    modalOverlay.style.display = 'none';
}

    closeModalBtn.onclick = closeModal;
    modalOverlay.onclick = closeModal;

    // 날짜를 눌렀을 때 등록
    function saveEvent() {
    var eventTitle = eventTitleInput.value;
    var eventDescription = eventDescriptionInput.value;
    var eventStartDate = eventStartDateInput.value;
    var eventEndDate = eventEndDateInput.value;
    var eventColor = eventColorSelect.value; // 색상 선택 값


    if (eventEndDate < eventStartDate) {
    alert('종료 날짜는 시작 날짜보다 앞설 수 없습니다.');
    return;
}

    if (eventTitle && eventDescription && eventStartDate && eventEndDate) {
    // JSON 형태로 데이터 준비
    var eventData = {
    workTitle: eventTitle,
    workExplanation: eventDescription,
    workSchedule: eventStartDate,
    deadLine: eventEndDate,
    workColor: eventColor // 선택한 색상 추가
};


    closeModal();
    // 서버로 데이터 전송
    fetch('/allWork', {
    method: 'POST',
    headers: {
    'Content-Type': 'application/json'
},
    body: JSON.stringify(eventData)
})
    .then(response => {
    if (response.ok) {
    alert('일정이 저장되었습니다.');
    location.reload();
} else {
    alert('저장 실패');
}
})
    .catch(error => {
    console.error('Error:', error);
});
    closeModal();
} else {
    alert('모든 필드를 입력해주세요.');
}
}
});