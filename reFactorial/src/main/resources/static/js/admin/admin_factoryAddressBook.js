document.addEventListener("DOMContentLoaded", function () {
    const selectedMenu = document.querySelectorAll(".sideMenuUl li");

    const menuMainBox = document.getElementById("menuMainBox");
    const registerContainer = document.getElementById("registerContainer");
    const editContainer = document.getElementById("editContainer");

    function cleanPhoneNumber(phoneNumber) {
        return phoneNumber.replace(/\D/g, ''); // 숫자가 아닌 문자를 제거
    }


    // 등록 화면 초기화
    showRegister();

    // 메뉴 버튼 이벤트 등록
    document.getElementById("showRegister").addEventListener("click", showRegister);
    document.getElementById("showEdit").addEventListener("click", showEdit);

    function showRegister() {
        selectedMenu.forEach(menu => {
            if (menu.classList.contains('selected')){
                menu.classList.remove('selected')
            }
        })

        document.getElementById("showRegister").classList.add('selected')

        menuMainBox.classList.add("register-mode");
        menuMainBox.classList.remove("edit-mode");
        registerContainer.style.display = "block";
        editContainer.style.display = "none";
    }

    function showEdit() {
        selectedMenu.forEach(menu => {
            if (menu.classList.contains('selected')){
                menu.classList.remove('selected')
            }
        })

        document.getElementById("showEdit").classList.add('selected')

        menuMainBox.classList.add("edit-mode");
        menuMainBox.classList.remove("register-mode");
        registerContainer.style.display = "none";
        editContainer.style.display = "block";

        initializeFactorySearch();
    }

    function initializeFactorySearch() {
        const searchInput = document.getElementById("factory-search-input");
        let debounceTimeout;

        searchInput.addEventListener("input", (event) => {
            clearTimeout(debounceTimeout);
            debounceTimeout = setTimeout(() => {
                const keyword = event.target.value.trim();
                if (keyword === "") {
                    fetchFactories();
                } else {
                    searchFactories(keyword);
                }
            }, 200);
        });

        fetchFactories();
    }

    function fetchFactories() {
        fetch("/admin/factories")
            .then(response => response.json())
            .then(factories => renderFactories(factories))
            .catch(error => console.error("Error fetching factories:", error));
    }

    function searchFactories(keyword) {
        fetch(`/admin/searchFactory?keyword=${keyword}`)
            .then(response => response.json())
            .then(factories => renderFactories(factories))
            .catch(error => console.error("Error searching factories:", error));
    }

    function renderFactories(factories) {
        const factoryList = document.getElementById("factory-list");
        factoryList.innerHTML = "";

        if (factories.length === 0) {
            factoryList.innerHTML = "<p>검색 결과가 없습니다.</p>";
            return;
        }

        factories.forEach(factory => {
            const factoryCard = document.createElement("div");
            factoryCard.className = "factory-card";
            factoryCard.innerHTML = `
                    <img src="${factory.fabImage}" alt="${factory.fabName}">
                    <h3>${factory.fabName}</h3>
                    <p>주소: ${factory.fabAddress}</p>
                `;
            factoryCard.addEventListener("click", () => loadFactory(factory.fabId));
            factoryList.appendChild(factoryCard);
        });
    }

    function loadFactory(factoryId) {
        console.log("loadFactory called with factoryId:", factoryId); // 호출 여부 확인
        fetch(`/admin/factory/${factoryId}`)
            .then(response => response.json())
            .then(factory => {
                console.log("Factory Data from API:", factory); // 서버 응답 확인
                populateEditForm(factory);
            })
            .catch(error => console.error("Error loading factory:", error));
    }

    function populateEditForm(factory) {
        console.log("Factory Data:", factory);
        document.getElementById("editFabId").value = factory.fabId;
        document.getElementById("editFabName").value = factory.fabName;
        document.getElementById("editFabAddress").value = factory.fabAddress;
        document.getElementById("editFabPhone").value = factory.fabPhone;
        document.getElementById("editManagerName").value = factory.managerName;
        document.getElementById("editManagerPhone").value = factory.managerPhone;
        document.getElementById("editFabEmail").value = factory.fabEmail;
        document.getElementById("editFabProduct").value = factory.fabProduct;
        document.getElementById("editFabImage").value = factory.fabImage;

        document.getElementById("factory-list").style.display = "none";
        document.querySelector(".search-container").style.display = "none";
        document.getElementById("editFactoryForm").style.display = "block";
    }


});

function goBackToList() {
    document.getElementById("editFactoryForm").style.display = "none";
    document.getElementById("factory-list").style.display = "flex";
    document.querySelector(".search-container").style.display = "block";
}

function cleanPhoneNumber(phoneNumber) {
    return phoneNumber.replace(/\D/g, ''); // 숫자가 아닌 문자를 제거
}

async function addFactory() {
    const newFactory = {
        fabName: document.getElementById("fabName").value,
        fabAddress: document.getElementById("fabAddress").value,
        fabPhone: cleanPhoneNumber(document.getElementById("fabPhone").value),
        managerName: document.getElementById("managerName").value,
        managerPhone: cleanPhoneNumber(document.getElementById("managerPhone").value),
        fabEmail: document.getElementById("fabEmail").value,
        fabProduct: document.getElementById("fabProduct").value,
        fabImage: document.getElementById("fabImage").value
    };

    console.log("managerName:", document.getElementById("managerName").value);


    const response = await fetch('/admin/addFactory', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newFactory)
    });

    if (response.ok) {
        alert("협력업체 등록 성공!");
        showEdit();
    } else {
        alert("협력업체 등록 실패!");
    }
}

function updateFactory() {
    const updatedFactory = {
        fabId: document.getElementById("editFabId").value,
        fabName: document.getElementById("editFabName").value,
        fabAddress: document.getElementById("editFabAddress").value,
        fabPhone: cleanPhoneNumber(document.getElementById("editFabPhone").value),
        managerName: document.getElementById("editManagerName").value,
        managerPhone: cleanPhoneNumber(document.getElementById("editManagerPhone").value),
        fabEmail: document.getElementById("editFabEmail").value,
        fabProduct: document.getElementById("editFabProduct").value,
        fabImage: document.getElementById("editFabImage").value
    };

    fetch("/admin/updateFactory", {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(updatedFactory)
    })
        .then(async response => {
            console.log("Response status:", response.status);
            const responseData = await response.json();
            console.log("Response JSON:", responseData);

            if (response.ok) {
                alert(responseData.message); // 성공 메시지 표시
                goBackToList();
            } else {
                alert("업데이트 실패: " + responseData.message);
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
            alert("서버 통신 오류: " + error.message);
        });
}