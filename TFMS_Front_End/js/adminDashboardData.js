async function loadTotalSuppliers() {
    try {
        const response = await fetch(`${API_BASE_URL}/adminDashboard/totalSuppliers`);
        const result = await response.json();

        if (result.code === 200) {
            document.getElementById("totalSuppliers").textContent = result.data;

            document.getElementById("newSuppliers").textContent = `+`+ result.data+` this month`;
        } else {
            console.error("Error:", result.status);
        }
    } catch (error) {
        console.error("Failed to load suppliers:", error);
    }
}

async function loadDashboardData() {
    try {
        const response = await fetch(`${API_BASE_URL}/adminDashboard/totalNormalCustomers`);
        const result = await response.json();

        if (result.status === 200) {
            document.getElementById("totalCustomers").innerText = result.data;
        }
    } catch (error) {
        console.error("Error loading dashboard data:", error);
    }
}

async function loadTotalStockQuantity() {
    try {
        const response = await fetch(`${API_BASE_URL}/adminDashboard/getTotalStockQuantity`);
        const result = await response.json();

        if (result.code === 200) {
            const qty = result.data;
            document.getElementById("total-stock-quantity-adminDashboard").textContent = `${qty} kg`;
        } else {
            console.error("Failed to load total stock quantity:", result);
        }
    } catch (error) {
        console.error("Error fetching total stock quantity:", error);
    }
}

function fetchTotalWeightThisMonth() {
    fetch(`${API_BASE_URL}/adminDashboard/totalWeightThisMonth`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            if (data.code === 200) {
                const formattedWeight = new Intl.NumberFormat().format(data.data) + " kg";

                document.querySelector("#total-weight-value-adminDashboard").textContent = formattedWeight;
            } else {
                console.error("Error:", data.message);
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
        });
}

function fetchBestSupplierToday() {
    fetch(`${API_BASE_URL}/adminDashboard/bestSupplierToday`)
        .then(response => response.json())
        .then(res => {
            if (res.data) {
                const supplierNameEl = document.getElementById("today-top-supplier-name");
                const supplierWeightEl = document.getElementById("today-top-supplier-total-weight");

                supplierNameEl.textContent = res.data.supplierName;
                supplierWeightEl.textContent = `${res.data.totalSupplied} kg supplied`;
            } else {
                document.getElementById("today-top-supplier-name").textContent = "No Suppliers Found Today";
                document.getElementById("today-top-supplier-total-weight").textContent = "0 kg supplied";
            }
        })
        .catch(err => {
            console.error("Error fetching top supplier:", err);
            document.getElementById("today-top-supplier-name").textContent = "Error loading data";
            document.getElementById("today-top-supplier-total-weight").textContent = "--";
        });
}

function getAllSuppliesData() {
    fetch(`${API_BASE_URL}/adminDashboard/getAllTeaLeafCounts`)
        .then(response => response.json())
        .then(res => {
            const tableBody = document.getElementById("all-tea-supplies-table-body");
            tableBody.innerHTML = ""; // Clear existing rows

            if (res.data && res.data.length > 0) {
                res.data.forEach(item => {
                    const row = document.createElement("tr");
                    row.className = "border-b";

                    row.innerHTML = `
                        <td class="p-2">${item.supplierName}</td>
                        <td class="p-2">${item.teaCardNumber}</td>
                        <td class="p-2">${item.date} ${item.time}</td>
                        <td class="p-2">${item.netWeight} kg</td>
                        <td class="p-2">${item.quality}</td>
                        <td class="p-2">${item.note || "-"}</td>
                    `;

                    tableBody.appendChild(row);
                });
            } else {
                const row = document.createElement("tr");
                row.innerHTML = `<td colspan="6" class="p-2 text-center text-gray-500">No deliveries found</td>`;
                tableBody.appendChild(row);
            }
        })
        .catch(err => {
            console.error("Error fetching tea leaf counts:", err);
            const tableBody = document.getElementById("all-tea-supplies-table-body");
            tableBody.innerHTML = `<tr><td colspan="6" class="p-2 text-center text-red-500">Error loading data</td></tr>`;
        });
}

document.addEventListener("DOMContentLoaded",() => {
    loadTotalSuppliers();
    loadDashboardData();
    loadTotalStockQuantity();
    fetchTotalWeightThisMonth();
    fetchBestSupplierToday();
    getAllSuppliesData();
});