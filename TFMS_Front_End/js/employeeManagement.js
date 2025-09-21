const EMPLOYEE_API = "http://localhost:8080/employee";

$(document).ready(function () {
    const $addEmployeeBtn = $("#addEmployeeBtn");
    const $employeeModal = $("#employeeModal");
    const $employeeUpdateModal = $("#employeeUpdateModal");
    const $closeBtns = $(".modal .close");

    const $employeeForm = $("#employeeForm");
    const $employeeUpdateForm = $("#employeeUpdateForm");
    const $tableBody = $("#employee-table-body");
    const $searchInput = $("#employee-search");

    $addEmployeeBtn.on("click", function () {
        $employeeForm[0].reset();
        $employeeModal.css("display", "block");
    });

    $closeBtns.on("click", function () {
        $(this).closest(".modal").css("display", "none");
    });

    $(window).on("click", function (e) {
        if ($(e.target).hasClass("modal")) {
            $(e.target).css("display", "none");
        }
    });

    async function loadEmployees() {
        try {
            const res = await fetch(`${EMPLOYEE_API}/getAll`);
            if (!res.ok) throw new Error("Failed to fetch employees");
            const result = await res.json();

            $tableBody.empty();

            if (Array.isArray(result.data)) {
                result.data.forEach(emp => {
                    const row = `
                        <tr>
                            <td class="p-3">${emp.id}</td>
                            <td class="p-3">${emp.name}</td>
                            <td class="p-3">${emp.address}</td>
                            <td class="p-3">${emp.phone}</td>
                            <td class="p-3">${emp.department}</td>
                            <td class="p-3">${emp.basicSalary}</td>
                            <td class="p-3 space-x-2">
                                <button class="edit-btn bg-yellow-500 text-white px-3 py-1 rounded" data-id="${emp.id}">Edit</button>
                                <button class="delete-btn bg-red-500 text-white px-3 py-1 rounded" data-id="${emp.id}">Delete</button>
                            </td>
                        </tr>
                    `;
                    $tableBody.append(row);
                });

                attachActionEvents();
            }
        } catch (err) {
            console.error("Error loading employees:", err);
        }
    }

    function attachActionEvents() {
        $(".edit-btn").off("click").on("click", function () {
            const $row = $(this).closest("tr");
            const id = $(this).data("id");

            $("#updateEmployeeId").val(id);
            $("#updateEmployeeName").val($row.children().eq(1).text());
            $("#updateEmployeeAddress").val($row.children().eq(2).text());
            $("#updateEmployeePhone").val($row.children().eq(3).text());
            $("#updateEmployeeDepartment").val($row.children().eq(4).text());
            $("#updateEmployeeSalary").val($row.children().eq(5).text());

            $employeeUpdateModal.css("display", "block");
        });

        $(".delete-btn").off("click").on("click", function () {
            const id = $(this).data("id");

            Swal.fire({
                title: "Are you sure?",
                text: `Do you want to delete employee ${id}?`,
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#d33",
                cancelButtonColor: "#3085d6",
                confirmButtonText: "Yes, delete it!"
            }).then(async (result) => {
                if (result.isConfirmed) {
                    try {
                        const res = await fetch(`${EMPLOYEE_API}/delete?id=${id}`, {
                            method: "PUT",
                        });

                        const data = await res.json();

                        if (res.ok) {
                            Swal.fire({
                                toast: true,
                                position: "top-end",
                                icon: "success",
                                title: data.message || "Deleted successfully!",
                                showConfirmButton: false,
                                timer: 3000,
                                timerProgressBar: true
                            });
                            loadEmployees();
                        } else {
                            Swal.fire({
                                toast: true,
                                position: "top-end",
                                icon: "error",
                                title: data.message || "Delete failed!",
                                showConfirmButton: false,
                                timer: 3000,
                                timerProgressBar: true
                            });
                        }
                    } catch (err) {
                        console.error("Error deleting employee:", err);
                    }
                }
            });
        });
    }

    $employeeForm.on("submit", async function (e) {
        e.preventDefault();

        const dto = {
            name: $("#employeeName").val(),
            address: $("#employeeAddress").val(),
            phone: $("#employeePhone").val(),
            department: $("#employeeDepartment").val(),
            basicSalary: $("#employeeSalary").val()
        };

        try {
            const res = await fetch(`${EMPLOYEE_API}/register`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            });

            const result = await res.json();

            if (res.ok) {
                Swal.fire({
                    toast: true,
                    position: "top-end",
                    icon: "success",
                    title: result.message || "Employee added successfully!",
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
                $employeeModal.css("display", "none");
                loadEmployees();
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
        } catch (err) {
            console.error("Error adding employee:", err);
        }
    });

    $employeeUpdateForm.on("submit", async function (e) {
        e.preventDefault();

        const dto = {
            id: $("#updateEmployeeId").val(),
            name: $("#updateEmployeeName").val(),
            address: $("#updateEmployeeAddress").val(),
            phone: $("#updateEmployeePhone").val(),
            department: $("#updateEmployeeDepartment").val(),
            basicSalary: $("#updateEmployeeSalary").val()
        };

        try {
            const res = await fetch(`${EMPLOYEE_API}/update`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            });

            const result = await res.json();

            if (res.ok) {
                Swal.fire({
                    toast: true,
                    position: "top-end",
                    icon: "success",
                    title: result.message || "Employee updated successfully!",
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
                $employeeUpdateModal.css("display", "none");
                loadEmployees();
            } else {
                Swal.fire({
                    toast: true,
                    position: "top-end",
                    icon: "error",
                    title: result.message || "Update failed!",
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
            }
        } catch (err) {
            console.error("Error updating employee:", err);
        }
    });

    $searchInput.on("input", async function () {
        const keyword = $(this).val().trim();

        if (keyword === "") {
            loadEmployees();
            return;
        }

        try {
            const res = await fetch(`${EMPLOYEE_API}/search/${keyword}`);
            if (!res.ok) throw new Error("Search failed");
            const result = await res.json();

            $tableBody.empty();

            if (Array.isArray(result.data)) {
                result.data.forEach(emp => {
                    const row = `
                        <tr>
                            <td class="p-3">${emp.id}</td>
                            <td class="p-3">${emp.name}</td>
                            <td class="p-3">${emp.address}</td>
                            <td class="p-3">${emp.phone}</td>
                            <td class="p-3">${emp.department}</td>
                            <td class="p-3">${emp.basicSalary || "-"}</td>
                            <td class="p-3 space-x-2">
                                <button class="edit-btn bg-yellow-500 text-white px-3 py-1 rounded" data-id="${emp.id}">Edit</button>
                                <button class="delete-btn bg-red-500 text-white px-3 py-1 rounded" data-id="${emp.id}">Delete</button>
                            </td>
                        </tr>
                    `;
                    $tableBody.append(row);
                });

                attachActionEvents();
            }
        } catch (err) {
            console.error("Error searching employees:", err);
        }
    });

    loadEmployees();
});
