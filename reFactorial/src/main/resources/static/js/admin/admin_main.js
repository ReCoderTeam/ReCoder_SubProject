const attChartItem = Object.keys(attendanceChart);
const attChartCount = Object.values(attendanceChart);

// 객체의 키-값 쌍을 배열로 변환
const sortedEntries = Object.entries(empHiredDateChart).sort(([keyA], [keyB]) => {
    // 날짜 문자열을 비교하여 정렬
    return new Date(keyA) - new Date(keyB);
});

// 정렬된 데이터를 기반으로 labels와 data 생성
const hiredChartItem = sortedEntries.map(([key]) => key);
const hiredChartCount = sortedEntries.map(([, value]) => value);

const attChart = document.getElementById('attChart');
const hiredChart = document.getElementById('hiredChart');

new Chart(attChart, {
    type: 'bar',
    data: {
        labels: attChartItem,
        datasets: [{
            label: '해당 직원 수',
            data: attChartCount,
            borderWidth: 1,
            backgroundColor: [
                'rgba(255, 99, 132, 0.5)',
                'rgba(255, 159, 64, 0.5)',
                'rgba(255, 205, 86, 0.5)',
                'rgba(75, 192, 192, 0.5)',
                'rgba(54, 162, 235, 0.5)',
            ],
            borderColor: [
                'rgb(255, 99, 132)',
                'rgb(255, 159, 64)',
                'rgb(255, 205, 86)',
                'rgb(75, 192, 192)',
                'rgb(54, 162, 235)',
            ],
        }]
    },
    options: {
        maintainAspectRatio: false,
        scales: {
            y: {
                ticks:{
                    stepSize: 2 // 단위
                }
            }
        }
    }
});

new Chart(hiredChart, {
    type: 'line',
    data: {
        labels: hiredChartItem,
        datasets: [
            {
                label: '입사 현황',
                data: hiredChartCount,
                borderColor: 'rgba(92, 25, 21, 1)',
                backgroundColor: 'rgba(92, 25, 21, 1)',
            }
        ]
    },
    options: {
        maintainAspectRatio: false,

        responsive: true,
        plugins: {
            legend: {
                display: false
            },
        },
        scales: {
            y: {
                beginAtZero: true, // y축 0부터 시작,
                ticks:{
                    stepSize: 1 // 단위
                },
                suggestedMax: Math.max(...hiredChartCount) + 1,
            }
        }
    },
});