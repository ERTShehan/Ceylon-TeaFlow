// teaLeafCount.js
const API_BASE = "http://localhost:8080/teaMakerDashboard";

document.addEventListener("DOMContentLoaded", () => {
    // ===================== Toast System =====================
    if (!document.getElementById('toast-container')) {
        const toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
        toastContainer.style.position = 'fixed';
        toastContainer.style.top = '20px';
        toastContainer.style.right = '20px';
        toastContainer.style.zIndex = '10000';
        document.body.appendChild(toastContainer);
    }

    window.showToast = function (message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <div class="toast-content">
                <span class="toast-message">${message}</span>
                <button class="toast-close">&times;</button>
            </div>
        `;

        toast.style.minWidth = '300px';
        toast.style.background = type === 'success' ? '#4CAF50' :
            type === 'error' ? '#F44336' : '#2196F3';
        toast.style.color = 'white';
        toast.style.padding = '12px 16px';
        toast.style.marginBottom = '10px';
        toast.style.borderRadius = '4px';
        toast.style.boxShadow = '0 2px 10px rgba(0,0,0,0.2)';
        toast.style.display = 'flex';
        toast.style.alignItems = 'center';
        toast.style.justifyContent = 'space-between';
        toast.style.animation = 'slideIn 0.3s ease';

        const closeBtn = toast.querySelector('.toast-close');
        closeBtn.style.background = 'none';
        closeBtn.style.border = 'none';
        closeBtn.style.color = 'white';
        closeBtn.style.fontSize = '18px';
        closeBtn.style.cursor = 'pointer';
        closeBtn.addEventListener('click', () => {
            toast.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        });

        setTimeout(() => {
            if (toast.parentNode) {
                toast.style.animation = 'slideOut 0.3s ease';
                setTimeout(() => toast.remove(), 300);
            }
        }, 5000);

        document.getElementById('toast-container').appendChild(toast);

        if (!document.getElementById('toast-animations')) {
            const style = document.createElement('style');
            style.id = 'toast-animations';
            style.textContent = `
                @keyframes slideIn {
                    from { transform: translateX(100%); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
                @keyframes slideOut {
                    from { transform: translateX(0); opacity: 1; }
                    to { transform: translateX(100%); opacity: 0; }
                }
            `;
            document.head.appendChild(style);
        }
    };

    // ===================== Form Elements =====================
    const teaCardInput = document.getElementById("supplierTeaCardNumber");
    const supplierNameInput = document.getElementById("supplierName");
    const grossWeightInput = document.getElementById("teaGrossWeight");
    const sackWeightInput = document.getElementById("teaSackWeight");
    const moistureWeightInput = document.getElementById("teaMoistureWeight");
    const netWeightInput = document.getElementById("teaNetWeight");
    const saveBtn = document.getElementById("teaCountSubmit");

    const updateGrossWeightInput = document.getElementById("updateTeaGrossWeight");
    const updateSackWeightInput = document.getElementById("updateTeaSackWeight");
    const updateMoistureWeightInput = document.getElementById("updateTeaMoistureWeight");
    const updateNetWeightInput = document.getElementById("updateTeaNetWeight");

    // ===================== Helpers =====================
    function clearFormFields() {
        teaCardInput.value = '';
        supplierNameInput.value = '';
        supplierNameInput.placeholder = '';
        grossWeightInput.value = '';
        sackWeightInput.value = '';
        moistureWeightInput.value = '';
        netWeightInput.value = '';
        document.getElementById("notes").value = '';

        document.querySelectorAll("input[name='quality']").forEach(radio => radio.checked = false);
    }

    function calculateNetWeight() {
        const gross = parseFloat(grossWeightInput.value) || 0;
        const sack = parseFloat(sackWeightInput.value) || 0;
        const moisture = parseFloat(moistureWeightInput.value) || 0;
        const net = gross - sack - moisture;
        netWeightInput.value = net > 0 ? net.toFixed(2) : 0;
    }

    function calculateNetWeightUpdate() {
        const gross = parseFloat(updateGrossWeightInput.value) || 0;
        const sack = parseFloat(updateSackWeightInput.value) || 0;
        const moisture = parseFloat(updateMoistureWeightInput.value) || 0;
        const net = gross - sack - moisture;
        updateNetWeightInput.value = net > 0 ? net.toFixed(2) : 0;
    }

    // ===================== Event Listeners =====================
    teaCardInput.addEventListener("blur", () => {
        const cardNo = teaCardInput.value.trim();
        if (cardNo) {
            fetch(`${API_BASE}/getSupplierByCard/${cardNo}`)
                .then(res => res.json())
                .then(data => {
                    if (data.code === 200) {
                        supplierNameInput.value = data.data;
                    } else {
                        supplierNameInput.value = "";
                        supplierNameInput.placeholder = "Supplier Not Found";
                    }
                })
                .catch(() => {
                    supplierNameInput.value = "";
                    supplierNameInput.placeholder = "Supplier Not Found";
                });
        }
    });

    [grossWeightInput, sackWeightInput, moistureWeightInput].forEach(i => i.addEventListener("input", calculateNetWeight));
    [updateGrossWeightInput, updateSackWeightInput, updateMoistureWeightInput].forEach(i => i.addEventListener("input", calculateNetWeightUpdate));

    // Save New Tea Leaf Record
    saveBtn.addEventListener("click", (e) => {
        e.preventDefault();

        const quality = document.querySelector("input[name='quality']:checked")?.value;
        const dto = {
            teaCardNumber: teaCardInput.value,
            supplierName: supplierNameInput.value,
            grossWeight: grossWeightInput.value,
            sackWeight: sackWeightInput.value,
            moistureWeight: moistureWeightInput.value,
            netWeight: netWeightInput.value,
            quality: quality ? quality.toUpperCase() : null,
            note: document.getElementById("notes").value
        };

        fetch(`${API_BASE}/addTeaLeafCount`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dto)
        })
            .then(res => res.json())
            .then(data => {
                if (data.code === 201) {
                    showToast("Tea Leaf Count Saved Successfully!", "success");
                    loadTodayRecords();
                    clearFormFields();
                    fetchQualityDistribution();
                } else {
                    showToast("Failed to save Tea Leaf Count", "error");
                }
            })
            .catch(() => showToast("Error saving Tea Leaf Count", "error"));
    });

    // ===================== Load Today Records =====================
    loadTodayRecords();

    // ===================== Modal Handling (Update) =====================
    document.querySelector("#teaLeafCountUpdateModal .close").addEventListener("click", () => {
        document.getElementById("teaLeafCountUpdateModal").classList.add("hidden");
    });

    document.getElementById("teaLeafSupplyForm").addEventListener("submit", function (e) {
        e.preventDefault();
        updateTeaLeafRecord();
    });

    // ===================== Modal Handling (All Records) =====================
    const allRecordsModal = document.getElementById("All-records");
    const allRecordsTableBody = document.getElementById("allRecordsTableBody");
    const loadMoreBtn = document.querySelector(".mt-6 button");
    const closeModalBtn = document.querySelector("#All-records .close");
    const searchInput = document.getElementById("searchInput");
    let allRecords = [];

    // Open modal
    loadMoreBtn.addEventListener("click", () => {
        fetch(`${API_BASE}/getAllTeaLeafCounts`)
            .then(res => res.json())
            .then(data => {
                allRecords = data.data || [];
                renderAllRecords(allRecords);
                allRecordsModal.classList.remove("hidden");
            })
            .catch(() => showToast("Failed to load all records", "error"));
    });

    // Close modal
    closeModalBtn.addEventListener("click", () => {
        allRecordsModal.classList.add("hidden");
    });

    window.addEventListener("click", (event) => {
        if (event.target === allRecordsModal) {
            allRecordsModal.classList.add("hidden");
        }
    });

    // Search
    searchInput.addEventListener("input", function () {
        const query = this.value.toLowerCase();
        const filtered = allRecords.filter(r =>
            r.teaCardNumber.toLowerCase().includes(query)
        );
        renderAllRecords(filtered);
    });

    function renderAllRecords(records) {
        allRecordsTableBody.innerHTML = "";
        records.forEach(record => {
            const row = `
              <tr class="border-b">
                <td class="p-3">${record.time}</td>
                <td class="p-3">${record.supplierName}</td>
                <td class="p-3">${record.grossWeight}</td>
                <td class="p-3">${record.netWeight}</td>
                <td class="p-3">${record.quality}</td>
                <td class="p-3">
                  <button class="text-blue-500 hover:underline">View</button>
                </td>
              </tr>
            `;
            allRecordsTableBody.insertAdjacentHTML("beforeend", row);
        });
    }

    fetchQualityDistribution();
});

// ===================== Functions Outside =====================
function loadTodayRecords() {
    fetch("http://localhost:8080/teaMakerDashboard/getAllTodayTeaLeafCounts")
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById("todayRecordsTableBody");
            tbody.innerHTML = "";

            if (data.data && data.data.length > 0) {
                data.data.forEach(record => {
                    const row = document.createElement("tr");
                    row.classList.add("border-b");
                    row.dataset.record = JSON.stringify(record);

                    row.innerHTML = `
                        <td class="p-3">${record.time}</td>
                        <td class="p-3">${record.supplierName}</td>
                        <td class="p-3">${record.grossWeight}</td>
                        <td class="p-3">${record.netWeight}</td>
                        <td class="p-3">${record.quality}</td>
                        <td class="p-3">
                            <button class="text-teal-600 hover:text-teal-800" onclick="updateTeaLeaf('${record.id}')">
                                <i class="fas fa-edit"></i>
                            </button>
                        </td>
                    `;
                    tbody.appendChild(row);
                });
            } else {
                tbody.innerHTML = `<tr><td colspan="6" class="p-3 text-center">No records found</td></tr>`;
            }
        })
        .catch(error => console.error("Error loading today records:", error));
}

function updateTeaLeaf(id) {
    const rows = document.querySelectorAll("#todayRecordsTableBody tr");
    let recordData = null;

    for (const row of rows) {
        const record = JSON.parse(row.dataset.record);
        if (record.id === id) {
            recordData = record;
            break;
        }
    }

    if (!recordData) {
        showToast("Record data not found", "error");
        return;
    }

    document.getElementById("updateTeaLeafId").value = recordData.id;
    document.getElementById("updateTeaGrossWeight").value = recordData.grossWeight;
    document.getElementById("updateTeaSackWeight").value = recordData.sackWeight;
    document.getElementById("updateTeaMoistureWeight").value = recordData.moistureWeight;
    document.getElementById("updateTeaNetWeight").value = recordData.netWeight;
    document.getElementById("updateTeaLeafNotes").value = recordData.note || "";

    document.getElementById("teaLeafCountUpdateModal").classList.remove("hidden");
}

function updateTeaLeafRecord() {
    const id = document.getElementById("updateTeaLeafId").value;
    const dto = {
        id: id,
        grossWeight: parseFloat(document.getElementById("updateTeaGrossWeight").value),
        sackWeight: parseFloat(document.getElementById("updateTeaSackWeight").value),
        moistureWeight: parseFloat(document.getElementById("updateTeaMoistureWeight").value),
        netWeight: parseFloat(document.getElementById("updateTeaNetWeight").value),
        note: document.getElementById("updateTeaLeafNotes").value
    };

    fetch(`${API_BASE}/updateTeaLeafCount`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dto)
    })
        .then(res => res.json())
        .then(data => {
            if (data.code === 200) {
                showToast("Tea Leaf Count Updated Successfully!", "success");
                document.getElementById("teaLeafCountUpdateModal").classList.add("hidden");
                loadTodayRecords();
            } else {
                showToast("Failed to update Tea Leaf Count", "error");
            }
        })
        .catch(() => showToast("Error updating Tea Leaf Count", "error"));
}

function fetchQualityDistribution() {
    fetch("http://localhost:8080/teaMakerDashboard/todayQualityDistribution")
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch quality distribution");
            }
            return response.json();
        })
        .then(data => {
            const dist = data.data; // APIResponse wrapper -> data

            document.getElementById("quality-excellent-percentage").innerText =
                `Excellent: ${dist.excellentPercentage.toFixed(1)}%`;
            document.getElementById("quality-good-percentage").innerText =
                `Good: ${dist.goodPercentage.toFixed(1)}%`;
            document.getElementById("quality-average-percentage").innerText =
                `Average: ${dist.averagePercentage.toFixed(1)}%`;
            document.getElementById("quality-poor-percentage").innerText =
                `Poor: ${dist.poorPercentage.toFixed(1)}%`;
        })
        .catch(error => {
            console.error("Error loading quality distribution:", error);
        });
}