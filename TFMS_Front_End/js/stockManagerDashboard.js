const API_BASE_URL = "http://localhost:8080";

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

function formatProductName(name) {
    return name.replace(/_/g, " ").toLowerCase()
        .replace(/\b\w/g, c => c.toUpperCase());
}

document.querySelector("#add-stock form").addEventListener("submit", async function (e) {
    e.preventDefault();

    const form = e.target;
    const productName = form.querySelector("select").value;
    const quantity = form.querySelector("input[type='number']").value;
    const expiryDateInput = form.querySelector("input[type='date']").value; // YYYY-MM-DD
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
});
