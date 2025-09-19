const FINANCE_MANAGER_API_BASE = "http://localhost:8080/financeManager";

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

async function loadFinanceManagers() {
    try {
        const res = await fetch(`${FINANCE_MANAGER_API_BASE}/getAll`);
        const data = await res.json();

        if (data.code === 200) {
            renderFinanceManagerTable(data.data);
        } else {
            showToast("Failed to load Finance Managers", "error");
        }
    } catch (error) {
        console.error("Error loading Finance Managers:", error);
        showToast("Error loading Finance Managers", "error");
    }
}

function renderFinanceManagerTable(financeManagers) {
    const tbody = document.getElementById("finance-manager-table-body");
    tbody.innerHTML = "";

    financeManagers.forEach(fm => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td class="p-3">${fm.id}</td>
            <td class="p-3">${fm.fullName}</td>
            <td class="p-3">${fm.email}</td>
            <td class="p-3">${fm.phoneNumber}</td>
            <td class="p-3">${fm.username}</td>
            <td class="p-3">${fm.status}</td>
            <td class="p-3 space-x-2">
                <button class="bg-blue-500 text-white px-2 py-1 rounded update-btn" data-id="${fm.id}">Update</button>
                <button class="bg-red-500 text-white px-2 py-1 rounded delete-btn" data-id="${fm.id}">Delete</button>
                <button class="bg-yellow-500 text-white px-2 py-1 rounded status-btn" data-id="${fm.id}">Change Status</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    attachFinanceManagerActions(financeManagers);
}

document.getElementById("financeManagerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const dto = {
        fullName: document.getElementById("financeManagerName").value,
        email: document.getElementById("financeManagerEmail").value,
        phoneNumber: document.getElementById("financeManagerPhone").value,
        basicSalary: document.getElementById("financeManagerSalary").value,
        username: document.getElementById("financeManagerUsername").value,
        password: document.getElementById("financeManagerPassword").value,
    };

    try {
        const res = await fetch(`${FINANCE_MANAGER_API_BASE}/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dto),
        });

        const data = await res.json();
        if (data.code === 201) {
            showToast("Finance Manager Registered Successfully");
            document.getElementById("financeManagerForm").reset();
            document.getElementById("financeManagerModal").style.display = "none";
            loadFinanceManagers();
        } else {
            showToast(data.message || "Failed to register Finance Manager", "error");
        }
    } catch (error) {
        console.error("Error registering Finance Manager:", error);
        showToast("Error registering Finance Manager", "error");
    }
});

function attachFinanceManagerActions(financeManagers) {
    document.querySelectorAll(".update-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            const fm = financeManagers.find(f => f.id === id);

            if (!fm) return;

            document.getElementById("updateFinanceManagerId").value = fm.id;
            document.getElementById("updateFinanceManagerName").value = fm.fullName;
            document.getElementById("updateFinanceManagerEmail").value = fm.email;
            document.getElementById("updateFinanceManagerPhone").value = fm.phoneNumber;
            document.getElementById("updateFinanceManagerSalary").value = fm.basicSalary || "";
            document.getElementById("updateFinanceManagerPassword").value = "";

            document.getElementById("financeManagerUpdateModal").style.display = "block";
        });
    });

    document.querySelectorAll(".delete-btn").forEach(btn => {
        btn.addEventListener("click", () => deleteFinanceManager(btn.dataset.id));
    });

    document.querySelectorAll(".status-btn").forEach(btn => {
        btn.addEventListener("click", () => changeFinanceManagerStatus(btn.dataset.id));
    });
}

document.getElementById("financeManagerUpdateForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const dto = {
        id: document.getElementById("updateFinanceManagerId").value,
        fullName: document.getElementById("updateFinanceManagerName").value,
        email: document.getElementById("updateFinanceManagerEmail").value,
        phoneNumber: document.getElementById("updateFinanceManagerPhone").value,
        basicSalary: document.getElementById("updateFinanceManagerSalary").value,
        username: document.getElementById("updateFinanceManagerEmail").value, // ⚠ Fix: should send fm.username, else DB error
        password: document.getElementById("updateFinanceManagerPassword").value,
        status: "Active"
    };

    try {
        const res = await fetch(`${FINANCE_MANAGER_API_BASE}/update`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dto),
        });

        const data = await res.json();
        if (data.code === 200) {
            showToast("Finance Manager Updated Successfully");
            document.getElementById("financeManagerUpdateModal").style.display = "none";
            loadFinanceManagers();
        } else {
            showToast(data.message || "Failed to update Finance Manager", "error");
        }
    } catch (error) {
        console.error("Error updating Finance Manager:", error);
        showToast("Error updating Finance Manager", "error");
    }
});

async function deleteFinanceManager(id) {
    if (!confirm("Are you sure you want to delete this Finance Manager?")) return;

    try {
        const res = await fetch(`${FINANCE_MANAGER_API_BASE}/delete?id=${id}`, {
            method: "PUT",
        });
        const data = await res.json();

        if (data.code === 200) {
            showToast("Finance Manager Deleted Successfully");
            loadFinanceManagers();
        } else {
            showToast(data.message || "Failed to delete Finance Manager", "error");
        }
    } catch (error) {
        console.error("Error deleting Finance Manager:", error);
        showToast("Error deleting Finance Manager", "error");
    }
}

async function changeFinanceManagerStatus(id) {
    try {
        const res = await fetch(`${FINANCE_MANAGER_API_BASE}/changeStatus/${id}`, {
            method: "PATCH",
        });
        const data = await res.json();

        if (data.code === 200) {
            showToast("Finance Manager Status Changed Successfully");
            loadFinanceManagers();
        } else {
            showToast(data.message || "Failed to change status", "error");
        }
    } catch (error) {
        console.error("Error changing Finance Manager status:", error);
        showToast("Error changing Finance Manager status", "error");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    loadFinanceManagers();

    document.getElementById("addFinanceManagerBtn").addEventListener("click", () => {
        document.getElementById("financeManagerModal").style.display = "block";
    });
});
