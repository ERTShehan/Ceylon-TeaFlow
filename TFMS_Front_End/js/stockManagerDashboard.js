const API_BASE_URL = "http://localhost:8080";
let currentPage = 0;
let currentFilter = "ALL";

async function loadStockLevels() {
    try {
        const response = await fetch(`${API_BASE_URL}/stockDashboard/getStockLevels`);
        const result = await response.json();

        const stockGrid = document.getElementById("stock-grid");
        stockGrid.innerHTML = "";

        result.data.forEach(item => {
            let levelClass = "high";
            const qty = parseInt(item.quantity);

            if (qty < 200) {
                levelClass = "low";
            } else if (qty < 500) {
                levelClass = "medium";
            }

            const stockDiv = document.createElement("div");
            stockDiv.className = `stock-level ${levelClass} p-4 rounded-lg bg-gray-50 border`;

            stockDiv.innerHTML = `
                <div class="flex justify-between items-center mb-2">
                    <h3 class="font-semibold">${formatProductName(item.productName)}</h3>
                </div>
                <p class="text-2xl font-bold">${item.quantity} kg</p>
            `;

            stockGrid.appendChild(stockDiv);
        });
    } catch (error) {
        console.error("Error loading stock levels:", error);
    }
}

async function loadTeaProducts() {
    try {
        const response = await fetch(`${API_BASE_URL}/stockDashboard/loadTeaProductInDropdown`);
        const result = await response.json();

        if (result.code === 200) {
            const select = document.getElementById("selectTeaType");
            select.innerHTML = `<option value="">Select Tea Type</option>`; // reset
            result.data.forEach(product => {
                const option = document.createElement("option");
                option.value = product;
                option.textContent = formatProductName(product);
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error("Error loading tea products:", error);
    }
}

async function loadStockHistory(page = 0, filter = "ALL") {
    try {
        const res = await fetch(
            `${API_BASE_URL}/stockDashboard/getStockHistory?page=${page}&size=5&filter=${filter}`
        );
        const result = await res.json();

        if (result.code === 200) {
            const data = result.data;
            populateStockHistoryTable(data.content);
            renderPagination(data.totalPages, page);
        }
    } catch (err) {
        console.error("Error loading stock history:", err);
    }
}

async function loadTotalStockQuantity() {
    try {
        const response = await fetch(`${API_BASE_URL}/stockDashboard/getTotalStockQuantity`);
        const result = await response.json();

        if (result.code === 200) {
            const qty = result.data;
            document.getElementById("total-stock-quantity").textContent = `${qty} kg`;
        } else {
            console.error("Failed to load total stock quantity:", result);
        }
    } catch (error) {
        console.error("Error fetching total stock quantity:", error);
    }
}

function populateStockHistoryTable(stocks) {
    const tbody = document.getElementById("stockHistoryTableBody");
    tbody.innerHTML = "";

    stocks.forEach(s => {
        const badge =
            s.type === "INCOMING"
                ? `<span class="px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs">Incoming</span>`
                : `<span class="px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs">Outgoing</span>`;

        tbody.innerHTML += `
          <tr class="border-b hover:bg-gray-50">
              <td class="p-3">${s.date}</td>
              <td class="p-3">${s.teaType}</td>
              <td class="p-3">${s.expiryDate}</td>
              <td class="p-3">${s.quantity}</td>
              <td class="p-3">${s.note ?? "-"}</td>
              <td class="p-3">${badge}</td>
          </tr>
        `;
    });
}

function renderPagination(totalPages, activePage) {
    const container = document.getElementById("pagination");
    container.innerHTML = "";
    for (let i = 0; i < totalPages; i++) {
        const btn = document.createElement("button");
        btn.className =
            "mx-1 px-3 py-1 rounded-lg " +
            (i === activePage
                ? "bg-tea-green text-white"
                : "bg-gray-200 hover:bg-tea-green hover:text-white");
        btn.textContent = i + 1;
        btn.onclick = () => {
            currentPage = i;
            loadStockHistory(i, currentFilter);
        };
        container.appendChild(btn);
    }
}

document.querySelectorAll("#stock-history button").forEach(btn => {
    btn.addEventListener("click", e => {
        currentFilter = e.target.textContent.toUpperCase();
        currentPage = 0;
        loadStockHistory(0, currentFilter);
    });
});

function formatProductName(name) {
    return name.replace(/_/g, " ").toLowerCase()
        .replace(/\b\w/g, c => c.toUpperCase());
}

document.querySelector("#add-stock form").addEventListener("submit", async function (e) {
    e.preventDefault();

    const form = e.target;
    const productName = form.querySelector("select").value;
    const quantity = form.querySelector("input[type='number']").value;
    const expiryDateInput = form.querySelector("input[type='date']").value;
    const notes = form.querySelector("textarea").value;

    if (!productName || !quantity) {
        alert("Please select tea type and enter quantity!");
        return;
    }

    const expiryDate = expiryDateInput ? `${expiryDateInput}T00:00:00` : null;

    const payload = {
        stockId: null,
        productName,
        quantity,
        expiryDate,
        notes
    };

    try {
        const response = await fetch(`${API_BASE_URL}/stockDashboard/addTeaInStock`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        const result = await response.json();
        if (result.code === 200) {
            alert(result.data);
            form.reset();
            loadStockHistory();
            loadTotalStockQuantity();
            await loadStockLevels();
        } else {
            alert("Failed to add stock!");
        }
    } catch (error) {
        console.error("Error adding stock:", error);
    }
});


document.addEventListener("DOMContentLoaded", () => {
    loadStockLevels();
    loadTeaProducts();
    loadStockHistory();
    loadTotalStockQuantity();
});
