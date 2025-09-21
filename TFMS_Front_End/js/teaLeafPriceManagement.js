const API_BASE1 = "http://localhost:8080/addTeaLeafPrice";

$(document).ready(function () {
    const $teaLeafPriceModal = $("#teaLeafPriceModal");
    const $closeModalBtn = $teaLeafPriceModal.find(".close");
    const $changeTeaLeafPriceBtn = $("#changeTeaLeafPriceBtn");
    const $teaLeafPriceForm = $("#teaLeafPriceForm");
    const $teaLeafPriceIdInput = $("#teaLeafPriceId");
    const $effectiveMonthInput = $("#effectiveMonth");
    const $pricePerKgInput = $("#pricePerKg");
    const $tableBody = $("#leaf-price-table-body");

    let isEditMode = false;

    $changeTeaLeafPriceBtn.on("click", function () {
        isEditMode = false;
        $teaLeafPriceForm[0].reset();
        $teaLeafPriceIdInput.val("");
        $teaLeafPriceModal.css("display", "block");
    });

    $closeModalBtn.on("click", function () {
        $teaLeafPriceModal.css("display", "none");
    });

    $(window).on("click", function (e) {
        if (e.target === $teaLeafPriceModal[0]) {
            $teaLeafPriceModal.css("display", "none");
        }
    });

    async function loadTeaLeafPrices() {
        try {
            const res = await fetch(`${API_BASE1}/getAll`);
            if (!res.ok) throw new Error("Failed to fetch prices");
            const result = await res.json();

            $tableBody.empty();

            if (Array.isArray(result.data)) {
                result.data.forEach(price => {
                    const row = `
                        <tr>
                            <td class="p-3">${price.id}</td>
                            <td class="p-3">${price.yearMonth}</td>
                            <td class="p-3">${price.pricePerKg.toFixed(2)}</td>
                            <td class="p-3">
                                <button class="edit-btn bg-yellow-500 text-white px-3 py-1 rounded" data-id="${price.id}">
                                    Edit
                                </button>
                            </td>
                        </tr>
                    `;
                    $tableBody.append(row);
                });

                attachEditEvents();
            }
        } catch (error) {
            console.error("Error loading tea leaf prices:", error);
        }
    }

    function attachEditEvents() {
        $(".edit-btn").off("click").on("click", function () {
            const id = $(this).data("id");

            try {
                const $row = $(this).closest("tr");
                const effectiveMonth = $row.children().eq(1).text().trim();
                const pricePerKg = $row.children().eq(2).text().trim();

                $teaLeafPriceIdInput.val(id);
                $effectiveMonthInput.val(effectiveMonth);
                $pricePerKgInput.val(pricePerKg);

                isEditMode = true;
                $teaLeafPriceModal.css("display", "block");
            } catch (error) {
                console.error("Error fetching tea leaf price details:", error);
            }
        });
    }

    $teaLeafPriceForm.on("submit", async function (e) {
        e.preventDefault();

        const dto = {
            id: $teaLeafPriceIdInput.val() || null,
            yearMonth: $effectiveMonthInput.val(),
            pricePerKg: parseFloat($pricePerKgInput.val()),
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
                Swal.fire({
                    toast: true,
                    position: "top-end",
                    icon: "success",
                    title: result.message || "Success!",
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
                $teaLeafPriceModal.css("display", "none");
                loadTeaLeafPrices();
            } else {
                Swal.fire({
                    toast: true,
                    position: "top-end",
                    icon: "error",
                    title: result.message || "Something went wrong!",
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
            }
        } catch (error) {
            console.error("Error saving tea leaf price:", error);
            Swal.fire({
                icon: "error",
                title: "Failed!",
                text: "Failed to save tea leaf price!"
            });
        }
    });

    loadTeaLeafPrices();
});
