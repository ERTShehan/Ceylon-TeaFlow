document.addEventListener("DOMContentLoaded", () => {
    let monthsBack = 3;
    let currentYearMonth = new Date();

    const monthLabel = document.getElementById('calendar-month-label');
    const calendarGrid = document.getElementById('calendar-grid');

    document.getElementById('prev-month').addEventListener('click', () => {
        currentYearMonth.setMonth(currentYearMonth.getMonth() - 1);
        renderCalendar();
    });
    document.getElementById('next-month').addEventListener('click', () => {
        currentYearMonth.setMonth(currentYearMonth.getMonth() + 1);
        renderCalendar();
    });

    async function fetchCalendarData() {
        const token = localStorage.getItem("ctf_access_token");
        const url = `http://localhost:8080/supplier/supplierCalendarData?monthsBack=${monthsBack}`;

        const resp = await fetch(url, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (!resp.ok) {
            console.error("Failed to fetch calendar data", resp.status, resp.statusText);
            return new Map();
        }

        const json = await resp.json();
        const map = new Map();
        if (json && json.data) {
            json.data.forEach(item => {
                map.set(item.date, item.totalNet);
            });
        }
        return map;
    }

    function formatMonthLabel(date) {
        const opts = { year: 'numeric', month: 'long' };
        return date.toLocaleString(undefined, opts);
    }

    function startOfMonth(date) {
        return new Date(date.getFullYear(), date.getMonth(), 1);
    }

    function daysInMonth(date) {
        return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
    }

    function fmtYMD(d) {
        const y = d.getFullYear();
        const m = (d.getMonth() + 1).toString().padStart(2, '0');
        const day = d.getDate().toString().padStart(2, '0');
        return `${y}-${m}-${day}`;
    }

    async function renderCalendar() {
        const dataMap = await fetchCalendarData();

        const displayDate = new Date(currentYearMonth);
        monthLabel.textContent = formatMonthLabel(displayDate);

        calendarGrid.innerHTML = '';
        const weekdays = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];
        weekdays.forEach(w => {
            const h = document.createElement('div');
            h.className = 'text-center text-gray-500 font-medium';
            h.textContent = w;
            calendarGrid.appendChild(h);
        });

        const first = startOfMonth(displayDate);
        const startWeekDay = first.getDay();
        const totalDays = daysInMonth(displayDate);

        for (let i = 0; i < startWeekDay; i++) {
            const blank = document.createElement('div');
            blank.className = 'calendar-day p-2 text-center text-gray-300';
            calendarGrid.appendChild(blank);
        }

        let monthTotal = 0;
        for (let day = 1; day <= totalDays; day++) {
            const d = new Date(displayDate.getFullYear(), displayDate.getMonth(), day);
            const dayStr = fmtYMD(d);
            const cell = document.createElement('div');
            cell.className = 'calendar-day p-2 text-center';
            const weight = dataMap.get(dayStr) || 0;

            if (weight > 0) {
                cell.classList.add('has-data','rounded-lg','border','border-tea-green');
                const weightDiv = document.createElement('div');
                weightDiv.className = 'text-xs text-tea-green';
                weightDiv.textContent = `${weight} kg`;
                cell.appendChild(document.createTextNode(day));
                cell.appendChild(weightDiv);
                monthTotal += weight;
                cell.title = `${weight} kg on ${dayStr}`;
            } else {
                cell.textContent = day;
            }
            calendarGrid.appendChild(cell);
        }

        document.getElementById('month-total-supplied').textContent = `Total Supplied: ${monthTotal.toFixed(2)} kg`;
        const target = 1000;
        const pct = Math.min(100, Math.round((monthTotal / target) * 100));
        document.getElementById('month-progress').style.width = `${pct}%`;
        document.getElementById('month-target-text').textContent = `${pct}% of monthly target achieved`;
        document.getElementById('month-amount').textContent = `LKR ${Math.round(monthTotal * 100)}`;
    }

    renderCalendar();
});

async function fetchMonthlyTotal() {
    const token = localStorage.getItem("ctf_access_token");
    const resp = await fetch("http://localhost:8080/supplier/getSupplierMonthlyTotal", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    });

    if (!resp.ok) {
        console.error("Failed to fetch monthly total");
        return 0;
    }
    const json = await resp.json();
    return json.data || 0;
}

document.addEventListener("DOMContentLoaded", async () => {
    const total = await fetchMonthlyTotal();
    document.querySelector("#monthly-supply-total").textContent = `${total} kg`;
});