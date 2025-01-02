console.log("js파일 연결")

document.addEventListener("DOMContentLoaded", () => {

    var userIcon = document.querySelector('.headerIconBox .myPage');

    // 요소가 존재하는지 확인
    if (userIcon) {
        userIcon.addEventListener('click', function () {
            if (LoginUserInfo.viewAuth === 'USER') {
                location.href = 'user/myPage';  // user 페이지로 이동
            } else {
                location.href = '/admin/main';  // admin 페이지로 이동
            }
        });
        console.log("viewAuth : ", LoginUserInfo.viewAuth);
    } else {
        console.log("myPage 요소를 찾을 수 없습니다.");
    }
});

function logout() {
    fetch('/auth/logout', {
        method: 'POST'
    }).catch(error => {
        console.error('Logout failed:', error);
    });
}
