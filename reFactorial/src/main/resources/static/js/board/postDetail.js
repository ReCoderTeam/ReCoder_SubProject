const noAttachment = document.querySelector(".noAttachment");

if (noAttachment) {
    if (attachmentFileList) {
        noAttachment.style.display = 'none'
    } else {
        noAttachment.style.display = "block";
    }
}


// 댓글 삭제 버튼 클릭 시 동작 (addEventListener 클릭시 동작을 하는 것)
// 삭제버튼 눌렀을 때 data-comment-id와 data-post-id 세팅하는 이벤트
document.querySelector('#comment-list').addEventListener('click', function (event) {
    // 클릭한 요소가 .deleteBtn인지 확인
    if (event.target && event.target.classList.contains('deleteBtn')) {
        const commentId = event.target.getAttribute('data-comment-id');
        const postId = event.target.getAttribute('data-post-id');

        const confirmDeleteBtn = document.querySelector("#confirmDeleteBtn");

        // 모달의 확인 버튼에 삭제할 댓글 ID와 게시글 ID 설정
        confirmDeleteBtn.setAttribute('data-comment-id', commentId);
        confirmDeleteBtn.setAttribute('data-post-id', postId);

    }
});

// 확인 버튼
const confirmDeleteBtn = document.querySelector("#confirmDeleteBtn");
// 확인 버튼 누를 시 댓글 삭제 API 호출 후 댓글 리랜더링
confirmDeleteBtn.addEventListener("click", function () {
    const commentId = confirmDeleteBtn.getAttribute('data-comment-id');
    const postId = confirmDeleteBtn.getAttribute('data-post-id');


    // 서버로 삭제 요청
    fetch(`http://localhost:8080/board/commentDelete?commentId=${commentId}&postId=${postId}`)
        .then((response) => response.json())
        .then((data) => {

            const commentListArea = document.querySelector('#comment-list');
            let commentList = '';
            let empId = document.querySelector('#empId').value;

            // 새로운 댓글 리스트를 생성
            data.forEach((item) => {
                commentList += `
                    <div style="display: flex; border-bottom: 1px solid gray; padding: 10px;">
                        <div style='width: 35%;'>${item.empName}</div>
                        <div style='width: 30%;'>${item.commentContent}</div>
                        <div style='width: 30%;'>${item.commentCreationDate}</div>
                    `
                if (empId === item.empId) {
                    commentList += `
                        <button type="button" data-toggle="modal" data-target="#commentDeleteModal"
                        data-comment-id="${item.commentId}"
                        data-post-id="${item.postId}"
                        class="deleteBtn">삭제</button>
                        `
                }
                commentList += `</div>`;
            });

            // 댓글 리스트 업데이트
            // innerHTML : 이 요소 안에 HTML을 넣겠습니다.
            commentListArea.innerHTML = commentList;
        });
});

