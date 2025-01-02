function showTab(tabName) {
    // 모든 탭 내용을 숨김
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });

    // 모든 탭 버튼의 active 상태 제거
    document.querySelectorAll('.tab-button').forEach(button => {
        button.classList.remove('active');
    });

    // 선택된 탭 내용을 보여줌
    document.getElementById(tabName + '-tab').classList.add('active');

    // 선택된 탭 버튼을 active 상태로 변경
    document.querySelector(`[onclick="showTab('${tabName}')"]`).classList.add('active');
}

// 페이지 로드 시 기본 탭 활성화
document.addEventListener('DOMContentLoaded', function() {
    showTab('draft'); // 기본으로 기안 문서 탭 표시
});

function createCandyPlant(x) {
    const plant = document.createElement('div');
    plant.className = 'candy-plant';
    plant.style.left = `${x}%`;

    const stem = document.createElement('div');
    stem.className = 'plant-stem';

    const flower = document.createElement('div');
    flower.className = 'plant-flower';

    const flowerCenter = document.createElement('div');
    flowerCenter.className = 'flower-center';

    for (let i = 0; i < 5; i++) {
        const petal = document.createElement('div');
        petal.className = 'flower-petal';
        petal.style.background = `radial-gradient(circle at 50% 50%, hsl(${Math.random() * 360}, 100%, 75%), hsl(${Math.random() * 360}, 100%, 50%))`;
        flower.appendChild(petal);
    }

    flower.appendChild(flowerCenter);

    flower.addEventListener('click', () => {
        flower.style.animation = 'pop 0.5s ease-out';
        setTimeout(() => {
            flower.remove();
            createFloatingCandy(x);
        }, 300);
    });

    plant.appendChild(stem);
    plant.appendChild(flower);
    document.querySelector('.candy-plants').appendChild(plant);
}



function createFloatingCandy(x) {
    const candy = document.createElement('div');
    candy.className = 'floating-candy';
    candy.style.left = `${x}%`;
    candy.style.backgroundColor = `hsl(${Math.random() * 360}, 100%, 75%)`;
    document.querySelector('.floating-candies').appendChild(candy);

    setTimeout(() => {
        candy.remove();
    }, 5000);
}

for (let i = 0; i < 5; i++) {
    createCandyPlant(10 + i * 20);
}

setInterval(() => {
    createFloatingCandy(Math.random() * 100);
}, 2000);