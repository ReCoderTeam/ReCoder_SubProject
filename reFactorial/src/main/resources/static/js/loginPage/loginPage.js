var swiper = new Swiper(".mySwiper", {
    effect: "cube",
    loop: true,
    // centeredSlides: true,
    grabCursor: true,
    slidesPerView: "auto",
    cubeEffect: {
        shadow: true,
        slideShadows: true,
        shadowOffset: 20,
        shadowScale: 0.8,
    },
    autoplay: {
        delay: 1500,
        disableOnInteraction: false
    },
    pagination: {
        el: ".swiper-pagination",
        clickable: true,
    }
});

// url 감지
urlSearch = new URLSearchParams(location.search);

// 로그인 오류 메세지 출력
const errorMsg = urlSearch.get('errorMessage');
const errorP = document.querySelectorAll(".loginBox p");

if (errorMsg) {
    console.log("errorMsg: ", errorMsg);

    errorP.forEach(errorPtag => {
        errorPtag.classList.add("error");
        errorPtag.textContent = errorMsg;
    })
} else {
    errorP.forEach(errorPtag => {
        errorPtag.classList.remove("error");
        errorPtag.textContent = '';
    })
}

// 입력 값 없이 로그인 시도 시에 막기
const loginBoxForm = document.querySelector(".loginBox");
const loginBtn = document.querySelector("#loginBtn")
loginBtn.addEventListener("click",(e)=>{
    e.preventDefault();

    const inputIdValue = document.querySelector("#member_id").value
    const inputPwdValue = document.querySelector("#member_pwd").value

    if (inputIdValue.trim().length === 0 || inputPwdValue.trim().length === 0){
        errorP.forEach(errorPtag => {
            errorPtag.classList.add("error");
            errorPtag.textContent = "입력 값을 넣어주세요";
        })
    } else {
        errorP.forEach(errorPtag => {
            loginBoxForm.submit();
            errorPtag.classList.remove("error");
            errorPtag.textContent = '';
        })
    }

})

// 사원번호 저장 관련 쿠기 존재여부 확인
window.onload = function () {
    const savedMemberId = getCookie("member_id");
    if (savedMemberId) {
        document.getElementById("member_id").value = savedMemberId;
        document.getElementById("saveId").checked = true;
    }
};

// 쿠키에서 값 가져오기
// (^|;) => 쿠키의 시작 부분 또는 이전 쿠키와 구분된 곳을 찾기
// \\s* => 공백 처리
// ([^;]*) => 값 부분을 캡처하는 패턴, 세미콜론이 아닌" 문자
function getCookie(name) {
    const value = document.cookie.match('(^|;)\\s*' + name + '=([^;]*)');
    return value ? value.pop() : '';
}

const confirmPTag = document.querySelectorAll("#ResetPwdForm p")
const confirmEmpIdTag = document.querySelector("#confirmEmpId")
const confirmEmpEmailTag = document.querySelector("#confirmEmpEmail")

    // 바꿔치기 버튼
const matchEmpIdEmailBtn = document.querySelector("#matchEmpIdEmailBtn");
const confirmCodeBtn = document.querySelector("#confirmCodeBtn");

    // 숨겨놓은 요소
const verificationCodeInput = document.querySelector("#verificationCode");
const labelTag = verificationCodeInput.previousElementSibling;
const pTag = verificationCodeInput.nextElementSibling;

    //인증번호 저장
let verificationCode;

matchEmpIdEmailBtn.addEventListener("click", (e) => {
    e.preventDefault();

    // 사원번호, 이메일 일치 확인
    const confirmEmpId = confirmEmpIdTag.value;
    const confirmEmpEmail = confirmEmpEmailTag.value;

    matchEmpIdEmailFunc(confirmEmpId,confirmEmpEmail)
})

    // 사번과 해당 이메일 일치하는지 확인하기
function matchEmpIdEmailFunc(confirmEmpId, confirmEmpEmail){

    fetch('/auth/matchEmpIdEmail', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            confirmEmpId: confirmEmpId,
            confirmEmpEmail: confirmEmpEmail
        })
    })
        .then(response => response.json())
        .then(data => {
            console.log("data matchEmpIdEmailStatus: ", data.matchEmpIdEmailStatus)

            if (data.matchEmpIdEmailStatus != null) {
                console.log(data.matchEmpIdEmailStatus.type)

                if (data.matchEmpIdEmailStatus) {
                    console.log("결과 true")

                    confirmEmpIdTag.classList.remove("error")
                    confirmEmpEmailTag.classList.remove("error")

                    confirmPTag.forEach(confirmTagp => {
                        confirmTagp.classList.remove("error");
                    })

                    confirmCodeBtn.style.display = "block"
                    verificationCodeInput.style.display = "block"
                    labelTag.style.display = "block"

                    matchEmpIdEmailBtn.style.display = "none";
                    matchEmpIdEmailBtn.type = "button"

                    sendVerificationCode(confirmEmpEmail);
                } else {
                    console.log("결과 false")
                    // p태그 지칭
                    confirmEmpIdTag.classList.add("error")
                    confirmEmpEmailTag.classList.add("error")

                    confirmPTag.forEach(confirmTagp => {
                        confirmTagp.classList.add("error");
                    })
                }
            }

            if (data.msgError) {
                confirmEmpIdTag.classList.add("error");
                confirmEmpIdTag.nextElementSibling.textContent = data.msgError;
                confirmEmpIdTag.nextElementSibling.classList.add("error")
            }

        })
        .catch(error => console.log(error));
}

    // 인증번호 보내기
function sendVerificationCode(confirmEmpEmail){
    console.log("안녕")

    fetch('/auth/sendVerificationCode',{
        method: 'POST',
        headers: {
        'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            sendToEmail : confirmEmpEmail
        })
    })
    .then(response => response.json())
    .then(data => {
        console.log("인증번호 발송 여부 data : ",data);

        verificationCode = data.number
    })
}

    // 인증번호 확인하기
confirmCodeBtn.addEventListener("click",(e)=>{
    e.preventDefault();

    const inputCode = verificationCodeInput.value
    console.log(inputCode);
    console.log("verificationCode: ",verificationCode)

    const sendToEmail = confirmEmpEmailTag.value;

    if (inputCode.trim() == '' || inputCode == null) {
        pTag.textContent = '인증번호를 입력해주세요.'
        pTag.classList.add("error");
        verificationCodeInput.classList.add("error")

    } else {
        pTag.classList.remove("error");
        verificationCodeInput.classList.remove("error")

        if (inputCode == verificationCode) {
            pTag.textContent = '인증번호가 일치하지 않습니다.'
            pTag.classList.remove("error");
            verificationCodeInput.classList.remove("error");

            ResetPwd(sendToEmail);
        } else {
            pTag.textContent = '인증번호가 일치하지 않습니다.'
            pTag.classList.add("error");
            verificationCodeInput.classList.add("error")
        }
    }
})

function ResetPwd(sendToEmail){
    const targetEmpId = confirmEmpIdTag.value;

    fetch('/auth/ResetPwd', {
        method: 'POST',
        headers: {
        'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            sendToEmail: sendToEmail,
            targetEmpId :targetEmpId
        })
    })
        .then(response => response.json())
        .then(data => {
        console.log("비밀번호 초기화 data : ", data);

        if (data.resetSuccess) {
            // 모달 열기
            $('#passwordResetModal').modal('hide');
            $('#resetPwNotice').modal('show');  // Bootstrap 모달을 여는 코드
        }
    })
}


