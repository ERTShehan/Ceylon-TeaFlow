const API_BASE = "http://localhost:8080/teaCard";

document.addEventListener("DOMContentLoaded", () => {
    const addTeaCardBtn = document.getElementById("addTeaCardBtn");
    const teaCardModal = document.getElementById("teaCardModal");
    const closeButtons = document.querySelectorAll(".modal .close");

    const teaCardForm = document.getElementById("teaCardForm");
    const teaCardTableBody = document.getElementById("tea-card-table-body");
    const teaCardSearch = document.getElementById("tea-card-search");

    addTeaCardBtn.addEventListener("click", () => {
        teaCardModal.style.display = "block";
    });

    closeButtons.forEach(btn =>
        btn.addEventListener("click", () => {
            teaCardModal.style.display = "none";
        })
    );

    async function loadTeaCards() {
        try {
            const res = await fetch(`${API_BASE}/getAll`);
            const result = await res.json();

            teaCardTableBody.innerHTML = "";
            result.data.forEach(card => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td class="p-3">${card.id}</td>
                    <td class="p-3">${card.number}</td>
                    <td class="p-3">${card.name}</td>
                    <td class="p-3">${card.issuedAt}</td>
                    <td class="p-3">
                        <button class="bg-red-500 text-white px-3 py-1 rounded delete-btn" data-id="${card.id}">Delete</button>
                    </td>
                `;
                teaCardTableBody.appendChild(row);
            });

            bindDeleteButtons();
        } catch (error) {
            console.error("Error loading tea cards:", error);
        }
    }

    teaCardForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const data = {
            name: document.getElementById("supplierName").value,
            number: document.getElementById("teaCardNumber").value,
        };

        try {
            const res = await fetch(`${API_BASE}/add`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            const result = await res.json();
            Swal.fire({
                icon: "success",
                title: "Success",
                text: result.message
            });

            teaCardModal.style.display = "none";
            teaCardForm.reset();
            loadTeaCards();
        } catch (error) {
            console.error("Error adding tea card:", error);
        }
    });

    async function deleteTeaCard(id) {
        Swal.fire({
            title: "Are you sure?",
            text: "You won't be able to revert this!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d33",
            cancelButtonColor: "#3085d6",
            confirmButtonText: "Yes, delete it!"
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const res = await fetch(`${API_BASE}/delete?id=${id}`, {
                        method: "PUT"
                    });

                    const data = await res.json();
                    Swal.fire({
                        icon: "success",
                        title: "Deleted!",
                        text: data.message
                    });
                    loadTeaCards();
                } catch (error) {
                    console.error("Error deleting tea card:", error);
                }
            }
        });
    }

    function bindDeleteButtons() {
        document.querySelectorAll(".delete-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                deleteTeaCard(btn.dataset.id);
            });
        });
    }

    teaCardSearch.addEventListener("keyup", async () => {
        const keyword = teaCardSearch.value.trim();
        if (keyword === "") {
            loadTeaCards();
            return;
        }

        try {
            const res = await fetch(`${API_BASE}/search/${keyword}`);
            const result = await res.json();

            teaCardTableBody.innerHTML = "";
            result.data.forEach(card => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td class="p-3">${card.id}</td>
                    <td class="p-3">${card.number}</td>
                    <td class="p-3">${card.name}</td>
                    <td class="p-3">${card.issuedAt}</td>
                    <td class="p-3">
                        <button class="bg-red-500 text-white px-3 py-1 rounded delete-btn" data-id="${card.id}">Delete</button>
                    </td>
                `;
                teaCardTableBody.appendChild(row);
            });

            bindDeleteButtons();
        } catch (error) {
            console.error("Error searching tea card:", error);
        }
    });

    loadTeaCards();
});
