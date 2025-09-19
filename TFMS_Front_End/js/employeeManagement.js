const EMPLOYEE_API = "http://localhost:8080/employee";

document.addEventListener("DOMContentLoaded", () => {
    const addEmployeeBtn = document.getElementById("addEmployeeBtn");
    const employeeModal = document.getElementById("employeeModal");
    const employeeUpdateModal = document.getElementById("employeeUpdateModal");
    const closeBtns = document.querySelectorAll(".modal .close");

    const employeeForm = document.getElementById("employeeForm");
    const employeeUpdateForm = document.getElementById("employeeUpdateForm");
    const tableBody = document.getElementById("employee-table-body");
    const searchInput = document.getElementById("employee-search");

    addEmployeeBtn.addEventListener("click", () => {
        employeeForm.reset();
        employeeModal.style.display = "block";
    });

    closeBtns.forEach(btn => {
        btn.addEventListener("click", () => {
            btn.closest(".modal").style.display = "none";
        });
    });

    window.addEventListener("click", (e) => {
        if (e.target.classList.contains("modal")) {
            e.target.style.display = "none";
        }
    });

    async function loadEmployees() {
        try {
            const res = await fetch(`${EMPLOYEE_API}/getAll`);
            if (!res.ok) throw new Error("Failed to fetch employees");
            const result = await res.json();

            tableBody.innerHTML = "";

            if (Array.isArray(result.data)) {
                result.data.forEach(emp => {
                    const row = document.createElement("tr");

                    row.innerHTML = `
                        <td class="p-3">${emp.id}</td>
                        <td class="p-3">${emp.name}</td>
                        <td class="p-3">${emp.address}</td>
                        <td class="p-3">${emp.phone}</td>
                        <td class="p-3">${emp.department}</td>
                        <td class="p-3">${emp.basicSalary || "-"}</td>
                        <td class="p-3 space-x-2">
                            <button class="edit-btn bg-yellow-500 text-white px-3 py-1 rounded" data-id="${emp.id}">
                                Edit
                            </button>
                            <button class="delete-btn bg-red-500 text-white px-3 py-1 rounded" data-id="${emp.id}">
                                Delete
                            </button>
                        </td>
                    `;

                    tableBody.appendChild(row);
                });

                attachActionEvents();
            }
        } catch (err) {
            console.error("Error loading employees:", err);
        }
    }

    function attachActionEvents() {
        document.querySelectorAll(".edit-btn").forEach(btn => {
            btn.addEventListener("click", (e) => {
                const row = e.target.closest("tr");
                const id = e.target.dataset.id;

                document.getElementById("updateEmployeeId").value = id;
                document.getElementById("updateEmployeeName").value = row.children[1].textContent;
                document.getElementById("updateEmployeeAddress").value = row.children[2].textContent;
                document.getElementById("updateEmployeePhone").value = row.children[3].textContent;
                document.getElementById("updateEmployeeDepartment").value = row.children[4].textContent;
                document.getElementById("updateEmployeeSalary").value = row.children[5].textContent;

                employeeUpdateModal.style.display = "block";
            });
        });

        document.querySelectorAll(".delete-btn").forEach(btn => {
            btn.addEventListener("click", async (e) => {
                const id = e.target.dataset.id;
                if (!confirm(`Are you sure you want to delete employee ${id}?`)) return;

                try {
                    const res = await fetch(`${EMPLOYEE_API}/delete?id=${id}`, {
                        method: "PUT",
                    });

                    const result = await res.json();

                    if (res.ok) {
                        alert(result.message || "Deleted successfully!");
                        loadEmployees();
                    } else {
                        alert(result.message || "Delete failed!");
                    }
                } catch (err) {
                    console.error("Error deleting employee:", err);
                }
            });
        });
    }

    employeeForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const dto = {
            name: document.getElementById("employeeName").value,
            address: document.getElementById("employeeAddress").value,
            phone: document.getElementById("employeePhone").value,
            department: document.getElementById("employeeDepartment").value,
            basicSalary: document.getElementById("employeeSalary").value
        };

        try {
            const res = await fetch(`${EMPLOYEE_API}/register`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            });

            const result = await res.json();

            if (res.ok) {
                alert(result.message || "Employee added successfully!");
                employeeModal.style.display = "none";
                loadEmployees();
            } else {
                alert(result.message || "Something went wrong!");
            }
        } catch (err) {
            console.error("Error adding employee:", err);
        }
    });

    employeeUpdateForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const dto = {
            id: document.getElementById("updateEmployeeId").value,
            name: document.getElementById("updateEmployeeName").value,
            address: document.getElementById("updateEmployeeAddress").value,
            phone: document.getElementById("updateEmployeePhone").value,
            department: document.getElementById("updateEmployeeDepartment").value,
            basicSalary: document.getElementById("updateEmployeeSalary").value
        };

        try {
            const res = await fetch(`${EMPLOYEE_API}/update`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            });

            const result = await res.json();

            if (res.ok) {
                alert(result.message || "Employee updated successfully!");
                employeeUpdateModal.style.display = "none";
                loadEmployees();
            } else {
                alert(result.message || "Update failed!");
            }
        } catch (err) {
            console.error("Error updating employee:", err);
        }
    });

    searchInput.addEventListener("input", async (e) => {
        const keyword = e.target.value.trim();

        if (keyword === "") {
            loadEmployees();
            return;
        }

        try {
            const res = await fetch(`${EMPLOYEE_API}/search/${keyword}`);
            if (!res.ok) throw new Error("Search failed");
            const result = await res.json();

            tableBody.innerHTML = "";

            if (Array.isArray(result.data)) {
                result.data.forEach(emp => {
                    const row = document.createElement("tr");

                    row.innerHTML = `
                        <td class="p-3">${emp.id}</td>
                        <td class="p-3">${emp.name}</td>
                        <td class="p-3">${emp.address}</td>
                        <td class="p-3">${emp.phone}</td>
                        <td class="p-3">${emp.department}</td>
                        <td class="p-3">${emp.basicSalary || "-"}</td>
                        <td class="p-3 space-x-2">
                            <button class="edit-btn bg-yellow-500 text-white px-3 py-1 rounded" data-id="${emp.id}">
                                Edit
                            </button>
                            <button class="delete-btn bg-red-500 text-white px-3 py-1 rounded" data-id="${emp.id}">
                                Delete
                            </button>
                        </td>
                    `;

                    tableBody.appendChild(row);
                });

                attachActionEvents();
            }
        } catch (err) {
            console.error("Error searching employees:", err);
        }
    });

    loadEmployees();
});
