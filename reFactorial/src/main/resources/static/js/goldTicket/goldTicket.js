const personList = document.querySelectorAll(".person");

// 경도
let lon = 126.937256;

// 위도
let lat = 37.556318;

//옵션 없이 지도 객체를 생성하면 서울 시청을 중심으로 하는 16 레벨의 지도가 생성됩니다.
var map = new naver.maps.Map('map', {
    center: new naver.maps.LatLng(lat, lon),
    zoom: 15
});

var marker = new naver.maps.Marker({
    position: new naver.maps.LatLng(lat, lon),
    map: map
});

personList.forEach(person =>{
    person.addEventListener("click",async (e) => {
        const addressInfo = e.currentTarget.dataset.address

        // 클릭에 따라 데이터 가져오기 위해 fetch 를 사용 (외부 API 사용이기 때문에 controller 따로 생성해서 사용)
        const response = await fetch(`http://125.129.201.45:9696/proxy/geocode?query=${addressInfo}`)

        if (response.ok) {
            const data = await response.json(); // 응답을 JSON으로 변환

            lon = data.addresses[0].x
            lat = data.addresses[0].y

            map.setCenter(new naver.maps.LatLng(lat, lon)); // 지도 중심 변경
            marker.setPosition(new naver.maps.LatLng(lat, lon)); // 마커 위치 변경
        } else {
            console.error("API 호출 실패:", response.status);
        }
    })
})