const API_BASE1 = "http://localhost:8080/addTeaLeafPrice";

document.addEventListener("DOMContentLoaded", () => {
    const teaLeafPriceModal = document.getElementById("teaLeafPriceModal");
    const closeModalBtn = teaLeafPriceModal.querySelector(".close");
    const changeTeaLeafPriceBtn = document.getElementById("changeTeaLeafPriceBtn");
    const teaLeafPriceForm = document.getElementById("teaLeafPriceForm");
    const teaLeafPriceIdInput = document.getElementById("teaLeafPriceId");
    const effectiveMonthInput = document.getElementById("effectiveMonth");
    const pricePerKgInput = document.getElementById("pricePerKg");
    const tableBody = document.getElementById("leaf-price-table-body");

    let isEditMode = false;

    changeTeaLeafPriceBtn.addEventListener("click", () => {
        isEditMode = false;
        teaLeafPriceForm.reset();
        teaLeafPriceIdInput.value = "";
        teaLeafPriceModal.style.display = "block";
    });

    closeModalBtn.addEventListener("click", () => {
        teaLeafPriceModal.style.display = "none";
    });

    window.addEventListener("click", (e) => {
        if (e.target === teaLeafPriceModal) {
            teaLeafPriceModal.style.display = "none";
        }
    });

    async function loadTeaLeafPrices() {
        try {
            const res = await fetch(`${API_BASE1}/getAll`);
            if (!res.ok) throw new Error("Failed to fetch prices");
            const result = await res.json();

            tableBody.innerHTML = "";

            if (Array.isArray(result.data)) {
                result.data.forEach(price => {
                    const row = document.createElement("tr");

                    row.innerHTML = `
                        <td class="p-3">${price.id}</td>
                        <td class="p-3">${price.yearMonth}</td>
                        <td class="p-3">${price.pricePerKg.toFixed(2)}</td>
                        <td class="p-3">
                            <button class="edit-btn bg-yellow-500 text-white px-3 py-1 rounded" data-id="${price.id}">
                                Edit
                            </button>
                        </td>
                    `;

                    tableBody.appendChild(row);
                });

                attachEditEvents();
            }
        } catch (error) {
            console.error("Error loading tea leaf prices:", error);
        }
    }

    function attachEditEvents() {
        document.querySelectorAll(".edit-btn").forEach(btn => {
            btn.addEventListener("click", async (e) => {
                const id = e.target.dataset.id;

                try {
                    const row = e.target.closest("tr");
                    const effectiveMonth = row.children[1].textContent.trim();
                    const pricePerKg = row.children[2].textContent.trim();

                    teaLeafPriceIdInput.value = id;
                    effectiveMonthInput.value = effectiveMonth;
                    pricePerKgInput.value = pricePerKg;

                    isEditMode = true;
                    teaLeafPriceModal.style.display = "block";
                } catch (error) {
                    console.error("Error fetching tea leaf price details:", error);
                }
            });
        });
    }

    teaLeafPriceForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const dto = {
            id: teaLeafPriceIdInput.value || null,
            yearMonth: effectiveMonthInput.value,
            pricePerKg: parseFloat(pricePerKgInput.value),
        };

        try {
            const url = isEditMode ? `${API_BASE1}/update` : `${API_BASE1}/add`;
            const method = isEditMode ? "PUT" : "POST";

            const res = await fetch(url, {
                method: method,
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(dto),
            });

            const result = await res.json();

            if (res.ok) {
                alert(result.message || "Success!");
                teaLeafPriceModal.style.display = "none";
                loadTeaLeafPrices();
            } else {
                alert(result.message || "Something went wrong!");
            }
        } catch (error) {
            console.error("Error saving tea leaf price:", error);
            alert("Failed to save tea leaf price!");
        }
    });

    loadTeaLeafPrices();
});
