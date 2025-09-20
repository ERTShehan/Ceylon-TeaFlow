const API_BASE_URL = "http://localhost:8080";

async function loadStockLevels() {
    console.log("Loading stock levels...");
    try {
        const response = await fetch(`${API_BASE_URL}/stockDashboard/getStockLevels`);
        const result = await response.json();

        const stockGrid = document.getElementById("stock-grid");
        stockGrid.innerHTML = "";

        result.data.forEach(item => {
            let levelClass = "high";
            const qty = parseInt(item.quantity.replace(/\D/g, "")); // extract number

            if (qty < 200) {
                levelClass = "low";
            } else if (qty < 500) {
                levelClass = "medium";
            }

            const stockDiv = document.createElement("div");
            stockDiv.className = `stock-level ${levelClass} p-4 rounded-lg`;

            stockDiv.innerHTML = `
                <div class="flex justify-between items-center mb-2">
                    <h3 class="font-semibold">${formatProductName(item.productName)}</h3>
                </div>
                <p class="text-2xl font-bold">${item.quantity}</p>
            `;

            stockGrid.appendChild(stockDiv);
        });
    } catch (error) {
        console.error("Error loading stock levels:", error);
    }
}

async function loadTeaProducts() {
    console.log("Loading tea products...");
    try {
        const response = await fetch(`${API_BASE_URL}/stockDashboard/loadTeaProductInDropdown`);
        const result = await response.json();

        if (result.code === 200) {
            console.log(result)
            const select = document.getElementById("selectTeaType");
            result.data.forEach(product => {
                const option = document.createElement("option");
                option.value = product;
                option.textContent = product.replace(/_/g, " ").toLowerCase()
                    .replace(/\b\w/g, c => c.toUpperCase());
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
    const expiryDate = form.querySelector("input[type='date']").value;
    const notes = form.querySelector("textarea").value;

    if (!productName) {
        alert("Please select a tea type!");
        return;
    }

    const payload = {
        stockId: null, // auto-generate
        productName: productName,
        quantity: quantity,
        expiryDate: expiryDate,
        notes: notes
    };

    try {
        const response = await fetch(`${API_BASE_URL}/stockDashboard/addTeaInStock`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const result = await response.json();
        if (result.code === 200) {
            alert(result.status + ": " + result.data);
            form.reset();
        } else {
            alert("Failed to add stock!");
        }
    } catch (error) {
        console.error("Error adding stock:", error);
    }
});

// Load on page start
document.addEventListener("DOMContentLoaded", () => {
    loadStockLevels();
    loadTeaProducts();
})
