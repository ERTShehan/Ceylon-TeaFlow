// const API_BASE = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", () => {
    loadAdvanceRequests();
});

async function loadAdvanceRequests() {
    const token = localStorage.getItem("ctf_access_token");
    if (!token) {
        alert("You must log in first.");
        window.location.href = "./login.html";
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
        alert(err.message || "Error fetching advance requests");
    }
}

function renderAdvanceRequests(requests) {
    const tbody = document.getElementById("advanceRequestsTable");
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
