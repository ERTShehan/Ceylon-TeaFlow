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

document.addEventListener("DOMContentLoaded", loadTotalSuppliers, loadDashboardData);