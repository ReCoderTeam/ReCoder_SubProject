// 새 제품 등록 요청 함수
async function addProduct() {
    // 폼에서 입력된 값을 객체로 만듦
    const newProduct = {

        name: document.getElementById("prodName").value, // 제품명
        description: document.getElementById("prodDescription").value, // 제품 설명
        price: document.getElementById("prodPrice").value, // 제품 가격
        image: document.getElementById("prodImage").value, // 이미지 URL
        category: document.getElementById("prodCategory").value // 제품 카테고리
    };

    console.log("등록 요청 데이터:", newProduct); // 확인용 로그

    // 서버에 POST 요청으로 새 제품 등록
    const response = await fetch('/admin/addProduct', {
        method: 'POST', // 데이터 저장 요청
        headers: {
            'Content-Type': 'application/json', // 요청 데이터 타입 (JSON)
        },
        body: JSON.stringify(newProduct), // JSON 형태로 데이터 전송
    });

    // 서버로부터 응답 받기
    const result = await response.text();
    console.log("서버 응답:", result); // 서버 응답 확인용 로그

    // 성공 여부에 따라 알림 표시
    if (result === "success") {
        alert("제품 등록 성공!"); // 등록 성공
        fetchProducts(); // 사용자 페이지의 최신 제품 리스트 업데이트 요청
    } else {
        alert("제품 등록 실패!"); // 등록 실패
    }
}



document.addEventListener("DOMContentLoaded", function () {
    const registerContainer = document.getElementById("registerContainer");
    const editContainer = document.getElementById("editContainer");
    const selectedMenu = document.querySelectorAll(".sideMenuUl li");

    // 제품 등록 화면 초기화
    showRegister();

    // 메뉴 버튼 이벤트 등록
    document.getElementById("showRegister").addEventListener("click", showRegister);
    document.getElementById("showEdit").addEventListener("click", showEdit);

    // 제품 등록 화면 표시
    function showRegister() {
        registerContainer.style.display = "block"; // 제품 등록 화면 표시
        editContainer.style.display = "none"; // 제품 수정 화면 숨김

        selectedMenu.forEach(menu => {
            if (menu.classList.contains('selected')){
                menu.classList.remove('selected')
            }
        })

        document.getElementById("showRegister").classList.add('selected')
    }

    // 제품 수정 화면 표시
    function showEdit() {
        registerContainer.style.display = "none"; // 제품 등록 화면 숨김
        editContainer.style.display = "block"; // 제품 수정 화면 표시

        selectedMenu.forEach(menu => {
            if (menu.classList.contains('selected')){
                menu.classList.remove('selected')
            }
        })

        document.getElementById("showEdit").classList.add('selected')

        initializeProductSearch(); // 검색창 초기화
    }

    // 제품 검색 초기화
    function initializeProductSearch() {
        const searchInput = document.getElementById("product-search-input");
        let debounceTimeout;

        searchInput.addEventListener("input", (event) => {
            clearTimeout(debounceTimeout);
            debounceTimeout = setTimeout(() => {
                const keyword = event.target.value.trim();
                if (keyword === "") {
                    fetchProducts();
                } else {
                    searchProducts(keyword);
                }
            }, 200); // 디바운스 200ms
        });

        fetchProducts(); // 전체 제품 초기 로드
    }

    // 전체 제품 조회
    function fetchProducts() {
        fetch("/admin/products")
            .then((response) => response.json())
            .then((products) => renderProducts(products))
            .catch((error) => console.error("Error fetching products:", error));
    }

    // 제품 검색
    function searchProducts(keyword) {
        fetch(`/admin/searchProduct?keyword=${keyword}`)
            .then((response) => response.json())
            .then((products) => renderProducts(products))
            .catch((error) => console.error("Error searching products:", error));
    }

    async function fetchProducts() {
        try {
            const response = await fetch('/admin/products');
            if (!response.ok) {
                throw new Error("제품 조회 실패: " + response.status);
            }
            const products = await response.json();
            console.log("API 응답 데이터:", products);

            if (!Array.isArray(products)) {
                console.error("API 응답이 배열이 아닙니다.");
                return;
            }

            renderProducts(products);
        } catch (error) {
            console.error("제품 조회 에러:", error);
        }
    }


    // 제품 카드 렌더링
    function renderProducts(products) {
        const productList = document.getElementById("product-list");
        productList.innerHTML = ""; // 기존 리스트 초기화

        if (products.length === 0) {
            productList.innerHTML = "<p>검색 결과가 없습니다.</p>";
            return;
        }

        products.forEach((product) => {
            const productCard = document.createElement("div");
            productCard.className = "product-card";
            productCard.innerHTML = `
                <img src="${product.image}" alt="${product.name}">
                <h3>${product.name}</h3>
                <p>코드: ${product.id}</p>
            `;
            productCard.addEventListener("click", () => loadProduct(product.id));
            productList.appendChild(productCard);
        });
    }

    // 특정 제품 정보 불러오기
    function loadProduct(productId) {
        console.log(`Clicked product ID: ${productId}`); // 클릭된 제품 ID 확인
        fetch(`/admin/product/${productId}`)
            .then((response) => response.json())
            .then((product) => populateEditForm(product))
            .catch((error) => console.error("Error loading product:", error));
    }

    // 수정 폼에 데이터 채우기
    function populateEditForm(product) {
        document.getElementById("editProdId").value = product.id;
        document.getElementById("editProdName").value = product.name;
        document.getElementById("editProdDescription").value = product.description;
        document.getElementById("editProdPrice").value = product.price;
        document.getElementById("editProdImage").value = product.image;
        document.getElementById("editProdCategory").value = product.category;
        document.getElementById("editProdStatus").value = product.status;

        // 제품 리스트와 검색창 숨기기
        document.getElementById("product-list").style.display = "none";
        document.querySelector(".search-container").style.display = "none";
        // 수정 폼 표시
        document.getElementById("editProductForm").style.display = "block";
    }

});

function updateProduct() {
    const updatedProduct = {
        id: document.getElementById("editProdId").value, // 제품 ID
        name: document.getElementById("editProdName").value,
        description: document.getElementById("editProdDescription").value,
        price: document.getElementById("editProdPrice").value,
        image: document.getElementById("editProdImage").value,
        category: document.getElementById("editProdCategory").value,
        status: document.getElementById("editProdStatus").value,
    };
    console.log("수정 요청 데이터:", updatedProduct);
    fetch("/admin/updateProduct", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(updatedProduct),
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("Update failed");
            }
            return response.text();
        })
        .then((result) => {
            alert("제품 수정 완료!");
            // 폼 숨기기 및 리스트 복원
            document.getElementById("editProductForm").style.display = "none";
            document.getElementById("product-list").style.display = "flex";
            document.querySelector(".search-container").style.display = "block";
            // 리스트 다시 로드
            fetchProducts();
        })
        .catch((error) => console.error("Error updating product:", error));
}


function goBackToList() {
    // 수정 폼 숨기기
    document.getElementById("editProductForm").style.display = "none";

    // 검색창과 제품 리스트 다시 표시
    document.getElementById("product-list").style.display = "flex";
    document.querySelector(".search-container").style.display = "block";
}
