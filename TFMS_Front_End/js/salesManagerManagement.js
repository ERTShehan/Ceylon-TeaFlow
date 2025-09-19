const SALES_MANAGER_API_BASE = "http://localhost:8080/salesManager";

function showToast(message, type = "success") {
    const existingToasts = document.querySelectorAll(".custom-toast");
    existingToasts.forEach(toast => toast.remove());

    const toast = document.createElement("div");
    toast.className = `custom-toast fixed top-4 right-4 px-4 py-3 rounded-lg shadow-lg z-50 ${
        type === "success" ? "bg-green-500 text-white" : "bg-red-500 text-white"
    }`;
    toast.innerText = message;
    document.body.appendChild(toast);

    setTimeout(() => toast.remove(), 3000);
}

async function loadSalesManagers() {
    try {
        const res = await fetch(`${SALES_MANAGER_API_BASE}/getAll`);
        const data = await res.json();

        if (data.code === 200) {
            renderSalesManagerTable(data.data);
        } else {
            showToast("Failed to load Sales Managers", "error");
        }
    } catch (error) {
        console.error("Error loading Sales Managers:", error);
        showToast("Error loading Sales Managers", "error");
    }
}

function renderSalesManagerTable(salesManagers) {
    const tbody = document.getElementById("sales-manager-table-body");
    tbody.innerHTML = "";

    salesManagers.forEach(sm => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td class="p-3">${sm.id}</td>
            <td class="p-3">${sm.fullName}</td>
            <td class="p-3">${sm.email}</td>
            <td class="p-3">${sm.phoneNumber}</td>
            <td class="p-3">${sm.username}</td>
            <td class="p-3">${sm.status}</td>
            <td class="p-3 space-x-2">
                <button class="bg-blue-500 text-white px-2 py-1 rounded update-btn" data-id="${sm.id}">Update</button>
                <button class="bg-red-500 text-white px-2 py-1 rounded delete-btn" data-id="${sm.id}">Delete</button>
                <button class="bg-yellow-500 text-white px-2 py-1 rounded status-btn" data-id="${sm.id}">Change Status</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    attachTableActions(salesManagers);
}

document.getElementById("salesManagerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const dto = {
        fullName: document.getElementById("salesManagerName").value,
        email: document.getElementById("salesManagerEmail").value,
        phoneNumber: document.getElementById("salesManagerPhone").value,
        basicSalary: document.getElementById("salesManagerSalary").value,
        username: document.getElementById("salesManagerUsername").value,
        password: document.getElementById("salesManagerPassword").value,
    };

    try {
        const res = await fetch(`${SALES_MANAGER_API_BASE}/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dto),
        });

        const data = await res.json();
        if (data.code === 201) {
            showToast("Sales Manager Registered Successfully");
            document.getElementById("salesManagerForm").reset();
            document.getElementById("salesManagerModal").style.display = "none";
            loadSalesManagers();
        } else {
            showToast(data.message || "Failed to register Sales Manager", "error");
        }
    } catch (error) {
        console.error("Error registering Sales Manager:", error);
        showToast("Error registering Sales Manager", "error");
    }
});

function attachTableActions(salesManagers) {
    document.querySelectorAll(".update-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            const sm = salesManagers.find(s => s.id === id);

            if (!sm) return;

            document.getElementById("updateSalesManagerId").value = sm.id;
            document.getElementById("updateSalesManagerName").value = sm.fullName;
            document.getElementById("updateSalesManagerEmail").value = sm.email;
            document.getElementById("updateSalesManagerPhone").value = sm.phoneNumber;
            document.getElementById("updateSalesManagerSalary").value = sm.basicSalary || "";
            document.getElementById("updateSalesManagerPassword").value = "";

            document.getElementById("salesManagerUpdateModal").style.display = "block";
        });
    });

    document.querySelectorAll(".delete-btn").forEach(btn => {
        btn.addEventListener("click", () => deleteSalesManager(btn.dataset.id));
    });

    document.querySelectorAll(".status-btn").forEach(btn => {
        btn.addEventListener("click", () => changeStatus(btn.dataset.id));
    });
}

document.getElementById("salesManagerUpdateForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const dto = {
        id: document.getElementById("updateSalesManagerId").value,
        fullName: document.getElementById("updateSalesManagerName").value,
        email: document.getElementById("updateSalesManagerEmail").value,
        phoneNumber: document.getElementById("updateSalesManagerPhone").value,
        basicSalary: document.getElementById("updateSalesManagerSalary").value,
        username: document.getElementById("updateSalesManagerEmail").value, // ⚠ username yawanna one - email wage ganna nathuwa backend ekata real username yawanna
        password: document.getElementById("updateSalesManagerPassword").value,
        status: "Active"
    };

    try {
        const res = await fetch(`${SALES_MANAGER_API_BASE}/update`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dto),
        });

        const data = await res.json();
        if (data.code === 200) {
            showToast("Sales Manager Updated Successfully");
            document.getElementById("salesManagerUpdateModal").style.display = "none";
            loadSalesManagers();
        } else {
            showToast(data.message || "Failed to update Sales Manager", "error");
        }
    } catch (error) {
        console.error("Error updating Sales Manager:", error);
        showToast("Error updating Sales Manager", "error");
    }
});

async function deleteSalesManager(id) {
    if (!confirm("Are you sure you want to delete this Sales Manager?")) return;

    try {
        const res = await fetch(`${SALES_MANAGER_API_BASE}/delete?id=${id}`, {
            method: "PUT",
        });
        const data = await res.json();

        if (data.code === 200) {
            showToast("Sales Manager Deleted Successfully");
            loadSalesManagers();
        } else {
            showToast(data.message || "Failed to delete Sales Manager", "error");
        }
    } catch (error) {
        console.error("Error deleting Sales Manager:", error);
        showToast("Error deleting Sales Manager", "error");
    }
}

async function changeStatus(id) {
    try {
        const res = await fetch(`${SALES_MANAGER_API_BASE}/changeStatus/${id}`, {
            method: "PATCH",
        });
        const data = await res.json();

        if (data.code === 200) {
            showToast("Sales Manager Status Changed Successfully");
            loadSalesManagers();
        } else {
            showToast(data.message || "Failed to change status", "error");
        }
    } catch (error) {
        console.error("Error changing status:", error);
        showToast("Error changing status", "error");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    loadSalesManagers();

    document.getElementById("addSalesManagerBtn").addEventListener("click", () => {
        document.getElementById("salesManagerModal").style.display = "block";
    });
});
