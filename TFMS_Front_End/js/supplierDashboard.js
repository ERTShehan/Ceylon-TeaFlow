const API_BASE = "http://localhost:8080";
const token = localStorage.getItem("ctf_access_token");


let currentPage = 1;
const itemsPerPage = 5;
let allAdvanceRequests = [];

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

    const modal = document.getElementById("All-request");
    const closeBtn = modal.querySelector(".close");
    const loadBtn = document.getElementById("load-all-apply-packets-btn");
    const tableBody = document.getElementById("allRequestsTableBody");
    const searchInput = document.getElementById("searchInput");

    loadBtn.addEventListener("click", async () => {
        modal.classList.remove("hidden");
        try {
            const resp = await fetch(`${API_BASE}/supplier/getAllTeaPacketRequests`, {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                }
            });

            if (!resp.ok) throw new Error("Failed to fetch requests");

            const result = await resp.json();
            if (result.code === 200 && result.data) {
                renderRequestsTable(result.data);
            } else {
                tableBody.innerHTML = `<tr><td colspan="5" class="text-center p-3 text-red-500">No records found</td></tr>`;
            }
        } catch (err) {
            console.error(err);
            tableBody.innerHTML = `<tr><td colspan="5" class="text-center p-3 text-red-500">Error loading requests</td></tr>`;
        }
    });

    closeBtn.addEventListener("click", () => {
        modal.classList.add("hidden");
    });

    searchInput.addEventListener("keyup", () => {
        const filter = searchInput.value.toLowerCase();
        const rows = tableBody.querySelectorAll("tr");
        rows.forEach(row => {
            const productName = row.cells[1].textContent.toLowerCase();
            row.style.display = productName.includes(filter) ? "" : "none";
        });
    });

    function renderRequestsTable(data) {
        tableBody.innerHTML = "";
        data.forEach(req => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td class="p-3">${new Date(req.requestDate).toLocaleString()}</td>
                <td class="p-3">${req.productName}</td>
                <td class="p-3">${req.weight}</td>
                <td class="p-3">Rs. ${req.price.toFixed(2)}</td>
                <td class="p-3">${req.status}</td>
            `;
            tableBody.appendChild(row);
        });
    }

    async function loadAdvanceRequests() {
        try {
            const res = await fetch(`${API_BASE}/supplier/getAllAdvances`, {
                headers: { "Authorization": "Bearer " + token }
            });
            const body = await res.json().catch(() => null);
            if (!res.ok) throw new Error(body?.status || "Failed to load requests");

            allAdvanceRequests = body?.data || [];
            currentPage = 1;
            renderAdvanceRequests();

        } catch (err) {
            console.error("Error loading advance requests:", err);
            Swal.fire({ icon: 'error', title: 'Error', text: err.message || 'Error fetching advance requests' });
        }
    }

    function renderAdvanceRequests() {
        const tbody = document.getElementById("advanceRequestsTable");
        const paginationControls = document.getElementById("pagination-controls");

        if (!tbody) return;

        // clear
        tbody.innerHTML = "";
        paginationControls.innerHTML = "";

        if (!allAdvanceRequests.length) {
            tbody.innerHTML = `<tr><td colspan="3" class="p-3 text-gray-500 text-center">No requests found</td></tr>`;
            return;
        }

        const totalPages = Math.ceil(allAdvanceRequests.length / itemsPerPage);
        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = Math.min(startIndex + itemsPerPage, allAdvanceRequests.length);
        const pageData = allAdvanceRequests.slice(startIndex, endIndex);

        pageData.forEach(req => {
            const row = document.createElement("tr");

            let statusClass = "status-pending";
            if (req.status === 'APPROVED') statusClass = "status-approved";
            if (req.status === 'REJECTED') statusClass = "status-rejected";

            row.innerHTML = `
            <td class="p-3">${new Date(req.date).toLocaleDateString()}</td>
            <td class="p-3">LKR ${req.amount.toLocaleString()}</td>
            <td class="p-3">
                <span class="${statusClass}">${req.status}</span>
            </td>
        `;
            tbody.appendChild(row);
        });

        renderPaginationControls(totalPages);
    }

    function renderPaginationControls(totalPages) {
        const paginationControls = document.getElementById("pagination-controls");
        if (totalPages <= 1) return;

        paginationControls.className = "flex justify-center gap-2 mt-4"; // wrapper styles

        function createButton(label, disabled, active, onClick) {
            const btn = document.createElement("button");
            btn.innerHTML = label;

            btn.className =
                "px-3 py-1 rounded-md border text-sm font-medium transition " +
                (disabled
                    ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                    : active
                        ? "bg-tea-green text-white border-tea-green"
                        : "bg-white text-gray-700 border-gray-300 hover:bg-tea-green hover:text-white");

            btn.disabled = disabled;
            if (!disabled) btn.addEventListener("click", onClick);
            return btn;
        }

        paginationControls.appendChild(
            createButton("&laquo;", currentPage === 1, false, () => {
                currentPage--;
                renderAdvanceRequests();
            })
        );

        const startPage = Math.max(1, currentPage - 2);
        const endPage = Math.min(totalPages, startPage + 4);
        for (let i = startPage; i <= endPage; i++) {
            paginationControls.appendChild(
                createButton(i, false, i === currentPage, () => {
                    currentPage = i;
                    renderAdvanceRequests();
                })
            );
        }

        paginationControls.appendChild(
            createButton("&raquo;", currentPage === totalPages, false, () => {
                currentPage++;
                renderAdvanceRequests();
            })
        );
    }

    function fetchTeaProducts() {
        fetch(`${API_BASE}/supplier/teaProduction`)
            .then(response => response.json())
            .then(data => {
                if (data.data && Array.isArray(data.data)) {
                    displayTeaProducts(data.data);
                }
            })
            .catch(err => {
                console.error('Error fetching tea products:', err);
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
            const card = document.createElement('div');
            card.className = 'product-card border rounded-lg p-4 hover:border-tea-green transition';

            card.innerHTML = `
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
            productsContainer.appendChild(card);
        });

        document.querySelectorAll('.apply-btn').forEach(button => {
            button.addEventListener('click', async (e) => {
                const productId = e.target.getAttribute('data-product-id');
                const productName = e.target.getAttribute('data-product-name');

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

                if (!result.isConfirmed) return;

                try {
                    const response = await fetch(`${API_BASE}/supplier/applyPacket?productId=${productId}`, {
                        method: 'POST',
                        headers: {
                            "Authorization": "Bearer " + token,
                            "Content-Type": "application/json"
                        }
                    });

                    const resJson = await response.json();
                    if (resJson.code === 200) {
                        Swal.fire({ icon: 'success', title: 'Success!', text: 'Request submitted successfully!' });
                    } else {
                        Swal.fire({ icon: 'error', title: 'Application Failed', text: 'Failed to submit request' });
                    }
                } catch (err) {
                    console.error("Error:", err);
                    Swal.fire({ icon: 'error', title: 'Error', text: 'An error occurred while submitting your request' });
                }
                loadMonthlyPacketRequestsAndDisplay();
            });
        });
    }

    function escapeHtml(text) {
        const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
        return text.toString().replace(/[&<>"']/g, m => map[m]);
    }

    function formatPrice(price) {
        return parseFloat(price).toFixed(2);
    }

    async function fetchMonthlyPacketRequests() {
        try {
            const resp = await fetch(`${API_BASE}/supplier/totalTeaPacketRequestsMonth`, {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                }
            });
            const json = await resp.json();
            return json.data || 0;
        } catch {
            return 0;
        }
    }

    async function loadMonthlyPacketRequestsAndDisplay() {
        const total = await fetchMonthlyPacketRequests();
        const element = document.querySelector("#tea-packets-count");
        if (element) element.textContent = `${total} units`;
    }

    renderCalendar();
    loadAdvanceRequests();
    fetchTeaProducts();
    fetchMonthlyTotalAndDisplay();
    loadMonthlyPacketRequestsAndDisplay();
    new SupplierDashboard();
    loadAdvanceAvailability();
    loadAccountBalance();
});

async function fetchMonthlyTotalAndDisplay() {
    const total = await fetchMonthlyTotal();
    document.querySelector("#monthly-supply-total").textContent = `${total} kg`;
}

async function fetchMonthlyTotal() {
    const token = localStorage.getItem("ctf_access_token");
    const resp = await fetch(`${API_BASE}/supplier/getSupplierMonthlyTotal`, {
        method: "GET",
        headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" }
    });
    const json = await resp.json();
    return json.data || 0;
}

async function loadAdvanceAvailability() {
    try {
        const now = new Date();
        const year = now.getFullYear();
        const month = now.getMonth() + 1;

        const resp = await fetch(`${API_BASE}/supplier/getSupplierMonthlyTotal?year=${year}&month=${month}`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (!resp.ok) throw new Error("Failed to fetch monthly total");

        const result = await resp.json();
        const totalPrice = result?.data?.totalPrice || 0;

        const available = totalPrice * 0.6;
        console.log(available);

        document.getElementById("advance-available-amount").textContent =
            `LKR ${available.toLocaleString()}`;
        document.getElementById("advance-progress").style.width = "60%";
        document.getElementById("advance-text").textContent =
            "60% of your monthly earnings available";

        window.MAX_ADVANCE_AMOUNT = available;

    } catch (err) {
        console.error("Error loading advance availability:", err);
    }
}

async function loadAccountBalance() {
    try {
        const token = localStorage.getItem("ctf_access_token");

        const now = new Date();
        const year = now.getFullYear();
        const month = now.getMonth() + 1;

        const resp = await fetch(
            `${API_BASE}/supplier/getSupplierMonthlyTotal?year=${year}&month=${month}`,
            {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );

        const body = await resp.json();
        if (!resp.ok) throw new Error(body?.status || "Failed to fetch balance");

        const totalPrice = body?.data?.totalPrice || 0;

        const balanceEl = document.querySelector("#account-balance-amount");
        if (balanceEl) {
            balanceEl.textContent = `LKR ${totalPrice.toLocaleString()}`;
        }

    } catch (err) {
        console.error("Error loading account balance:", err);
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "Failed to load account balance."
        });
    }
}


document.getElementById("advanceForm")?.addEventListener("submit", async (e) => {
    e.preventDefault();

    const amount = parseFloat(document.getElementById("amount").value.trim());
    const reason = document.getElementById("reason").value.trim();

    if (isNaN(amount) || amount <= 0) {
        Swal.fire({ icon: 'warning', title: 'Invalid Amount', text: 'Please enter a valid advance amount.' });
        return;
    }

    if (amount > window.MAX_ADVANCE_AMOUNT) {
        Swal.fire({
            icon: 'error',
            title: 'Limit Exceeded',
            text: `You can only request up to LKR ${window.MAX_ADVANCE_AMOUNT.toLocaleString()}.`
        });
        return;
    }

    const result = await Swal.fire({
        title: 'Confirm Advance Request',
        html: `You are requesting an advance of <b>LKR ${amount}</b>.<br>Reason: ${reason || 'No reason provided'}`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Yes, submit request',
        cancelButtonText: 'Cancel'
    });

    if (!result.isConfirmed) return;

    try {
        const res = await fetch(`${API_BASE}/supplier/applyAdvance`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ amount, reason })
        });

        const body = await res.json();
        if (!res.ok) throw new Error(body?.status || "Submission failed");

        Swal.fire({ icon: 'success', title: 'Success', text: body?.data || "Advance submitted successfully!" });
        e.target.reset();
        loadAdvanceRequests();

    } catch (err) {
        console.error("Advance apply error:", err);
        Swal.fire({ icon: 'error', title: 'Error', text: err.message || "Error submitting advance" });
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const downloadBtn = document.querySelector("#downloadBillBtn");
    if (!downloadBtn) return;

    downloadBtn.addEventListener("click", async () => {
        const yearEl = document.getElementById("yearSelect");
        const monthEl = document.getElementById("monthSelect");

        if (!yearEl || !monthEl) {
            console.error("Year or Month select element not found!");
            return;
        }

        const year = yearEl.value;
        const month = monthEl.value;
        const token = localStorage.getItem("ctf_access_token");

        console.log("Year:", year, "Month:", month);

        try {
            const resp = await fetch(
                `${API_BASE}/supplier/downloadMonthlyBill?year=${year}&month=${month}`,
                {
                    method: "GET",
                    headers: {
                        "Authorization": `Bearer ${token}`,
                    }
                }
            );

            if (!resp.ok) {
                const errorText = await resp.text();
                throw new Error(errorText || "Failed to download bill");
            }

            const blob = await resp.blob();
            const url = window.URL.createObjectURL(blob);

            window.open(url, "_blank");

        } catch (err) {
            console.error("Error downloading monthly bill:", err);
            Swal.fire({
                icon: "error",
                title: "Download Failed",
                text: "Could not generate monthly bill. Please try again."
            });
        }
    });
});



class SupplierDashboard {
    constructor() {
        this.token = localStorage.getItem("ctf_access_token");
        this.monthsBack = 3;
        this.currentYearMonth = new Date();

        this.monthLabel = document.getElementById('calendar-month-label');
        this.calendarGrid = document.getElementById('calendar-grid');

        this.initEventListeners();
        this.init();
    }

    initEventListeners() {
        document.getElementById('prev-month').addEventListener('click', () => {
            this.currentYearMonth.setMonth(this.currentYearMonth.getMonth() - 1);
            this.renderCalendar();
        });

        document.getElementById('next-month').addEventListener('click', () => {
            this.currentYearMonth.setMonth(this.currentYearMonth.getMonth() + 1);
            this.renderCalendar();
        });
    }

    async init() {
        await this.renderCalendar();
    }


    async fetchCalendarData() {
        const url = `${API_BASE}/supplier/supplierCalendarData?monthsBack=${this.monthsBack}`;
        try {
            const resp = await fetch(url, {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${this.token}`,
                    "Content-Type": "application/json"
                }
            });

            if (!resp.ok) throw new Error(`Fetch failed: ${resp.status}`);

            const json = await resp.json();
            const map = new Map();
            if (json && json.data) {
                json.data.forEach(item => {
                    map.set(item.date, item.totalNet);
                });
            }
            return map;
        } catch (err) {
            console.error("fetchCalendarData error:", err);
            return new Map();
        }
    }

    async fetchMonthlySummary(year, month) {
        try {
            const resp = await fetch(`${API_BASE}/supplier/getSupplierMonthlyTotal?year=${year}&month=${month}`, {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${this.token}`,
                    "Content-Type": "application/json"
                }
            });

            if (!resp.ok) throw new Error(`Fetch failed: ${resp.status}`);

            const json = await resp.json();
            return json.data || { totalKg: 0, totalPrice: 0 };
        } catch (err) {
            console.error("fetchMonthlySummary error:", err);
            return { totalKg: 0, totalPrice: 0 };
        }
    }

    formatMonthLabel(date) {
        const opts = { year: 'numeric', month: 'long' };
        return date.toLocaleString(undefined, opts);
    }

    startOfMonth(date) {
        return new Date(date.getFullYear(), date.getMonth(), 1);
    }

    daysInMonth(date) {
        return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
    }

    fmtYMD(d) {
        const y = d.getFullYear();
        const m = (d.getMonth() + 1).toString().padStart(2, '0');
        const day = d.getDate().toString().padStart(2, '0');
        return `${y}-${m}-${day}`;
    }


    async renderCalendar() {
        const dataMap = await this.fetchCalendarData();
        const displayDate = new Date(this.currentYearMonth);

        this.monthLabel.textContent = this.formatMonthLabel(displayDate);
        this.calendarGrid.innerHTML = '';

        const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        weekdays.forEach(w => {
            const h = document.createElement('div');
            h.className = 'text-center text-gray-500 font-medium';
            h.textContent = w;
            this.calendarGrid.appendChild(h);
        });

        const first = this.startOfMonth(displayDate);
        const startWeekDay = first.getDay();
        const totalDays = this.daysInMonth(displayDate);

        for (let i = 0; i < startWeekDay; i++) {
            const blank = document.createElement('div');
            blank.className = 'calendar-day p-2 text-center text-gray-300';
            this.calendarGrid.appendChild(blank);
        }

        const summary = await this.fetchMonthlySummary(displayDate.getFullYear(), displayDate.getMonth() + 1);

        for (let day = 1; day <= totalDays; day++) {
            const d = new Date(displayDate.getFullYear(), displayDate.getMonth(), day);
            const dayStr = this.fmtYMD(d);
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
                cell.title = `${weight} kg on ${dayStr}`;
            } else {
                cell.textContent = day;
            }
            this.calendarGrid.appendChild(cell);
        }

        document.getElementById('month-total-supplied').textContent =
            `Total Supplied: ${summary.totalKg.toFixed(2)} kg`;
        document.getElementById('month-amount').textContent =
            `LKR ${summary.totalPrice.toFixed(2)}`;

        const target = 1000;
        const pct = Math.min(100, Math.round((summary.totalKg / target) * 100));
        document.getElementById('month-progress').style.width = `${pct}%`;
        document.getElementById('month-target-text').textContent =
            `${pct}% of monthly target achieved`;
    }
}