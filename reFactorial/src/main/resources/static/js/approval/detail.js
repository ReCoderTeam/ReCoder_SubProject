document.addEventListener("DOMContentLoaded", function () {
    // 버튼 및 모달 관련 요소
    const approveButton = document.querySelector('button[name="action"][value="approve"]');
    const rejectButton = document.querySelector('button[name="action"][value="reject"]');
    const finalizeButton = document.querySelector('button[name="action"][value="finalize"]');
    const modal = document.getElementById('confirmModal');
    const modalMessage = document.getElementById('modalMessage');
    const rejectReason = document.getElementById('rejectReason');
    const confirmBtn = document.getElementById('confirmBtn');
    const cancelBtn = document.getElementById('cancelBtn');

    let actionType = ""; // 승인 유형
    let pmId = document.querySelector('input[name="pmId"]').value; // 문서 ID
    let isFinalApprover = document.getElementById("isFinalApprover")?.value === "true"; // 최종 승인자 여부

    // 디버깅: 변수 값 확인
    console.log("pmId:", pmId);
    console.log("isFinalApprover:", isFinalApprover);

    // 버튼 클릭 이벤트 추가
    [approveButton, rejectButton, finalizeButton].forEach((button) => {
        button?.addEventListener("click", function (e) {
            e.preventDefault();
            actionType = button.value;

            // 디버깅: 클릭된 버튼 확인
            console.log("클릭된 버튼:", actionType);

            if (actionType === "approve") {
                modalMessage.textContent = "승인하시겠습니까?";
                rejectReason.style.display = "none";
            } else if (actionType === "reject") {
                modalMessage.textContent = "반려하시겠습니까?";
                rejectReason.style.display = "block";
            } else if (actionType === "finalize") {
                modalMessage.textContent = "전결하시겠습니까?";
                rejectReason.style.display = "none";
            }

            modal.style.display = "block";
        });
    });

    // 모달 창 확인 버튼 클릭
    confirmBtn.addEventListener("click", function () {
        const reason = rejectReason.style.display === "block" ? rejectReason.value : "";

        // 승인/반려/전결 요청
        sendApprovalAction(actionType, pmId, reason);

        modal.style.display = "none";
    });


    // 모달 창 취소 버튼 클릭 (여기에 추가)
    cancelBtn.addEventListener("click", function () {
        modal.style.display = "none";
        rejectReason.value = "";
    });

    // 모달 바깥 영역 클릭 시 닫기
    window.addEventListener("click", function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
            rejectReason.value = "";
        }
    });


    // 승인/반려/전결 요청 전송 함수
    function sendApprovalAction(action, pmId, reason) {
        console.log("승인/반려/전결 요청 - action:", action, "pmId:", pmId, "reason:", reason);
        fetch(`/approvals/detail`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `action=${action}&pmId=${pmId}&reason=${reason}`,
        })
            .then((response) => {
                if (response.ok) {
                    console.log("서버 응답 성공 - action:", action);
                    if (action === "approve") {
                        alert("승인되었습니다.");
                        window.location.reload();
                    } else if (action === "reject") {
                        alert("반려되었습니다.");
                        window.location.href = "/approvals/rejected";
                    } else if (action === "finalize") {
                        alert("전결되었습니다.");
                        window.location.href = "/approvals/completed";
                    }
                } else {
                    console.error("서버 응답 실패");
                    alert("처리 중 오류가 발생했습니다.");
                }
            })
            .catch((error) => {
                console.error("서버와의 통신 중 문제가 발생했습니다:", error);
                alert("서버와의 통신 중 문제가 발생했습니다.");
            });
    }
});