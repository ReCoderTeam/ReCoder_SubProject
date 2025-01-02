const searchDate = document.querySelector("#searchDate");
const dataBox = document.querySelector(".tktreserve_item_box");

searchDate.addEventListener("change",()=>{
    dataBox.innerHTML=''
    getTktReserve();
})

const formatPhoneNumber = (phone) => {
    return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
};

const getTktReserve = () =>{
    let selectedDay = searchDate.value;
    const imgList = ['umpa1.png','umpa2.png','squirrel.png']

    console.log("selectedDay : ",selectedDay)

    fetch(`/admin/getTktReserve?selectedDay=${selectedDay}`,{
        method : 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data =>{
            console.log("예약자 데이터 : ",data)

            if (data.resultList.length == 0){
                dataBox.innerHTML=`<div class="notMatch">해당 날짜에 예약자가 없습니다.</div>`
            } else{

                data.resultList.forEach(data =>{
                    const randomNum = Math.floor(Math.random() * 3)

                    const item = `
                            <div class="tktreserve_item">
                                <input type="checkbox" name="select_tkt[]" id="select_tkt" value="${data.tktReserveId}">
                                <div class="random_img">
                                    <img src="/images/${imgList[randomNum]}" alt="사진">
                                </div>
                                <div class="tkt_names">
                                    <div>${data.tktReserveName}, </div>
                                    <div>${data.tktReserveAccomp}</div>
                                </div>
                                <div class="tkt_date">${data.tktReserveDate}</div>
                                <div class="tkt_phone">${formatPhoneNumber(data.tktReservePhone)}</div>
                            </div>
                        `
                    dataBox.innerHTML += item
                })
            }
        })
}

document.addEventListener("DOMContentLoaded", ()=>{
    getTktReserve();
})