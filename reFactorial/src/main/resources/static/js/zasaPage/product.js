const products = /*[[${products}]]*/ [];

let currentRotation = 0;
let selectedIndex = 0;

// 캐러셀 초기화
function initializeCarousel() {
    const carousel = document.querySelector('.carousel');
    const angleStep = 360 / products.length;
    const radius = 1000;  // 카드와의 간격
    const verticalOffset = -50; // 여기에 원하는 값을 입력 (음수일수록 위로 올라감)



    carousel.style.transform = `rotateY(${currentRotation}deg)`;

    // 기존 내용 제거
    carousel.innerHTML = '';

    products.forEach((product, index) => {
        const cardWrapper = document.createElement('div');
        cardWrapper.className = 'card-wrapper';

        const angle = angleStep * index;

        // 카드의 위치 설정
        cardWrapper.style.transform = `rotateY(${angle}deg) translateZ(${radius}px) translateY(${verticalOffset}px)`;
        const card = document.createElement('div');
        card.className = 'product-card';

        // 카드 내용 설정
        card.innerHTML = `
            <img src="${product.image}" alt="${product.name}">
            <h3>${product.name}</h3>
            <p class="price">${product.price}</p>
            <p>${product.description}</p>
        `;

        // 카드가 항상 정면을 보도록 설정 - 여기가 핵심 수정 부분
        card.style.transform = `rotateY(${-angle - currentRotation}deg)`;

        cardWrapper.appendChild(card);
        cardWrapper.style.opacity = selectedIndex === index ? '1' : '0.7';
        carousel.appendChild(cardWrapper);
    });
}

// 회전 함수도 약간 수정
function rotateCarousel(direction) {
    currentRotation += -direction * (360 / products.length);
    const carousel = document.querySelector('.carousel');
    carousel.style.transform = `rotateY(${currentRotation}deg)`;

    selectedIndex = (selectedIndex + direction + products.length) % products.length;

    // 모든 카드의 방향과 투명도 업데이트
    document.querySelectorAll('.card-wrapper').forEach((wrapper, index) => {
        const card = wrapper.querySelector('.product-card');
        const angle = (360 / products.length) * index;

        // 여기가 핵심 수정 부분
        card.style.transform = `rotateY(${-angle - currentRotation}deg)`;
        wrapper.style.opacity = selectedIndex === index ? '1' : '0.7';

    });
}

// 페이지 로드 시 캐러셀 초기화
document.addEventListener('DOMContentLoaded', initializeCarousel);



async function fetchProducts() {
    const response = await fetch('/admin/product');
    const products = await response.json();

    updateCarousel(products);
}

function updateCarousel(products) {
    const carousel = document.querySelector('.carousel');
    const angleStep = 360 / products.length;
    const radius = 1000;

    carousel.innerHTML = '';

    // "판매" 상태인 제품만 렌더링
    products.filter(product => product.status === '판매').forEach((product, index) => {
        const cardWrapper = document.createElement('div');
        cardWrapper.className = 'card-wrapper';

        const angle = angleStep * index;

        cardWrapper.style.transform = `rotateY(${angle}deg) translateZ(${radius}px)`;

        const card = document.createElement('div');
        card.className = 'product-card';

        card.innerHTML = `
            <img src="${product.image}" alt="${product.name}">
            <h3>${product.name}</h3>
            <p class="price">${product.price}</p>
            <p>${product.description}</p>
        `;

        cardWrapper.appendChild(card);
        carousel.appendChild(cardWrapper);
    });

    products.forEach((product, index) => {
        const cardWrapper = document.createElement('div');
        cardWrapper.className = 'card-wrapper';

        const angle = angleStep * index;

        cardWrapper.style.transform = `rotateY(${angle}deg) translateZ(${radius}px)`;

        const card = document.createElement('div');
        card.className = 'product-card';

        card.innerHTML = `
            <img src="${product.image}" alt="${product.name}">
            <h3>${product.name}</h3>
            <p class="price">${product.price}</p>
            <p>${product.description}</p>
        `;

        cardWrapper.appendChild(card);
        carousel.appendChild(cardWrapper);
    });
}

// 페이지 로드 시 최신 제품 리스트 가져오기
document.addEventListener('DOMContentLoaded', fetchProducts);

