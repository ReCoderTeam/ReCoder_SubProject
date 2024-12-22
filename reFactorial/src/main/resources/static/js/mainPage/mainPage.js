const todayAttPtag = document.querySelector("#todayAtt")

window.onload = function () {
    fetch("/attendance/getTodayAttendance", {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log("근태 data: ", data)
            
            let att = data.attStatus;
            
            if (att == '출근' || att == "정상 출근"){
                att = "정상 출근"
            }

            todayAttPtag.textContent = att;
        })
        .catch(error => {
            console.error("Fetch error: ", error);
        });

    let lat;
    let lon;
    navigator.geolocation.getCurrentPosition((position) => {
        lat = position.coords.latitude;
        lon = position.coords.longitude;
        console.log("latitude : ",position.coords.latitude," longitude : ", position.coords.longitude);

        fetch("/api/weather-key", {
            method: "GET"
        }).then(response => response.text())
            .then(key =>{
                console.log("api key : ",key)

                if (key){
                    fetch(`https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${key}`,{
                        method: 'GET'
                    }).then(response => response.json())
                        .then(data =>{
                            console.log("날씨 데이터 : ", data)

                            const maxTemp = data.main.temp_max;
                            const minTemp = data.main.temp_min;
                            const icon = data.weather[0].icon;

                            setWeather(maxTemp,minTemp,icon)
                        })
                } else {
                    console.log("api key 없음")
                }

            });

    });
}

function setWeather (maxTemp,minTemp,icon){
    maxTemp = (maxTemp -273.15).toFixed(1)
    minTemp = (minTemp -273.15).toFixed(1)

    const weatherAPIBox = document.querySelector(".WeatherAPIBox");

    const result = `
                <div class="weatherImg">
                    <img src="https://openweathermap.org/img/wn/${icon}@4x.png">
                </div>
                <div class="weatherTemp">
                    <div>${minTemp} <span></span> /</div>
                    <div> ${maxTemp} <span></span> </div>
                </div>
    `
    weatherAPIBox.innerHTML =''
    weatherAPIBox.innerHTML = result
}