const API_BASE = "http://localhost:8080";

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
        const url = `${API_BASE}/supplier/supplierCalendarData?monthsBack=${monthsBack}`;

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
        const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
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
                cell.classList.add('has-data', 'rounded-lg', 'border', 'border-tea-green');
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
    loadAdvanceRequests();
    fetchTeaProducts();
    fetchMonthlyTotalAndDisplay();

    const form = document.getElementById("advanceForm");
    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();

            const amount = document.getElementById("amount")?.value?.trim();
            const reason = document.getElementById("reason")?.value?.trim();

            if (!amount) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Missing Amount',
                    text: 'Please enter the advance amount.',
                });
                return;
            }

            // Show confirmation dialog
            const result = await Swal.fire({
                title: 'Confirm Advance Request',
                html: `You are requesting an advance of <b>LKR ${amount}</b>.<br>Reason: ${reason || 'No reason provided'}`,
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes, submit request',
                cancelButtonText: 'Cancel'
            });

            if (!result.isConfirmed) {
                return;
            }

            const token = localStorage.getItem("ctf_access_token");
            if (!token) {
                Swal.fire({
                    icon: 'error',
                    title: 'Authentication Required',
                    text: 'You must log in first.',
                }).then(() => {
                    window.location.href = "./login.html";
                });
                return;
            }

            try {
                const res = await fetch(`${API_BASE}/supplier/applyAdvance`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer " + token
                    },
                    body: JSON.stringify({
                        amount,
                        reason
                    })
                });

                const body = await res.json().catch(() => null);

                if (!res.ok) {
                    const msg = body?.data || body?.status || res.statusText || "Request failed";
                    throw new Error(msg);
                }

                Swal.fire({
                    icon: 'success',
                    title: 'Success!',
                    text: body?.data || 'Advance application submitted successfully!',
                });
                form.reset();
                loadAdvanceRequests();

            } catch (err) {
                console.error("Advance apply error:", err);
                Swal.fire({
                    icon: 'error',
                    title: 'Submission Failed',
                    text: err.message || 'Error submitting advance payment.',
                });
            }
        });
    }
});

async function fetchMonthlyTotalAndDisplay() {
    const total = await fetchMonthlyTotal();
    document.querySelector("#monthly-supply-total").textContent = `${total} kg`;
}

async function fetchMonthlyTotal() {
    const token = localStorage.getItem("ctf_access_token");
    const resp = await fetch(`${API_BASE}/supplier/getSupplierMonthlyTotal`, {
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

async function loadAdvanceRequests() {
    const token = localStorage.getItem("ctf_access_token");
    if (!token) {
        Swal.fire({
            icon: 'error',
            title: 'Authentication Required',
            text: 'You must log in first.',
        }).then(() => {
            window.location.href = "./login.html";
        });
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/supplier/getAllAdvances`, {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        const body = await res.json().catch(() => null);
        if (!res.ok) throw new Error(body?.status || "Failed to load requests");

        const requests = body?.data || [];
        renderAdvanceRequests(requests);

    } catch (err) {
        console.error("Error loading advance requests:", err);
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: err.message || 'Error fetching advance requests',
        });
    }
}

function renderAdvanceRequests(requests) {
    const tbody = document.getElementById("advanceRequestsTable");
    if (!tbody) return;

    tbody.innerHTML = "";

    if (!requests.length) {
        tbody.innerHTML = `<tr><td colspan="3" class="p-3 text-gray-500 text-center">No requests found</td></tr>`;
        return;
    }

    requests.forEach(req => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td class="p-3">${new Date(req.date).toLocaleDateString()}</td>
            <td class="p-3">LKR ${req.amount}</td>
            <td class="p-3">
                <span class="px-2 py-1 rounded text-sm ${
            req.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                req.status === 'APPROVED' ? 'bg-green-100 text-green-700' :
                    'bg-red-100 text-red-700'
        }">${req.status}</span>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function fetchTeaProducts() {
    fetch(`${API_BASE}/supplier/teaProduction`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.data && Array.isArray(data.data)) {
                displayTeaProducts(data.data);
            } else {
                console.error('Unexpected data format:', data);
            }
        })
        .catch(error => {
            console.error('Error fetching tea products:', error);
            const container = document.getElementById('products-container');
            if (container) {
                container.innerHTML =
                    '<p class="text-red-500 text-center col-span-full">Failed to load products. Please try again later.</p>';
            }
        });
}

function displayTeaProducts(products) {
    const productsContainer = document.getElementById('products-container');
    if (!productsContainer) return;

    productsContainer.innerHTML = '';

    products.forEach(product => {
        const productCard = document.createElement('div');
        productCard.className = 'product-card border rounded-lg p-4 hover:border-tea-green transition';

        productCard.innerHTML = `
            <h3 class="font-semibold">${escapeHtml(product.name)}</h3>
            <p class="text-gray-600 text-sm mb-3">${escapeHtml(product.description)}</p>
            <div class="flex justify-between items-center">
                <p class="text-tea-green font-semibold">LKR ${formatPrice(product.price)}</p>
                <p class="font-semibold">${escapeHtml(product.quantity)}</p>
            </div>
            <button class="apply-btn mt-4 w-full py-2 bg-tea-green text-white rounded-lg font-semibold hover:bg-tea-green-light transition" 
                data-product-id="${product.id}" data-product-name="${escapeHtml(product.name)}">
                Apply now
            </button>
        `;

        productsContainer.appendChild(productCard);
    });

    // attach event listeners
    document.querySelectorAll('.apply-btn').forEach(button => {
        button.addEventListener('click', async (e) => {
            const productId = e.target.getAttribute('data-product-id');
            const productName = e.target.getAttribute('data-product-name');
            const token = localStorage.getItem("ctf_access_token");

            // Show confirmation dialog
            const result = await Swal.fire({
                title: 'Confirm Tea Packet Application',
                html: `You are applying for the tea packet: <b>${productName}</b>`,
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes, apply now',
                cancelButtonText: 'Cancel'
            });

            if (!result.isConfirmed) {
                return;
            }

            try {
                const response = await fetch(`${API_BASE}/supplier/applyPacket?productId=${productId}`, {
                    method: 'POST',
                    headers: {
                        "Authorization": "Bearer " + token,
                        "Content-Type": "application/json"
                    }
                });

                const result = await response.json();
                if (result.code === 200) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Success!',
                        text: 'Request submitted successfully!',
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Application Failed',
                        text: 'Failed to submit request',
                    });
                }
            } catch (error) {
                console.error("Error:", error);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'An error occurred while submitting your request',
                });
            }
        });
    });
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.toString().replace(/[&<>"']/g, function (m) { return map[m]; });
}

function formatPrice(price) {
    return parseFloat(price).toFixed(2);
}