let allDeliveries = [];
let currentPage = 1;
const itemsPerPage = 6;

function loadTotalSuppliers() {
    $.getJSON(`${API_BASE_URL}/adminDashboard/totalSuppliers`, function (result) {
        if (result.code === 200) {
            $("#totalSuppliers").text(result.data);
            $("#newSuppliers").text("+" + result.data + " this month");
        } else {
            console.error("Error:", result.status);
        }
    }).fail(function (err) {
        console.error("Failed to load suppliers:", err);
    });
}

function loadDashboardData() {
    $.getJSON(`${API_BASE_URL}/adminDashboard/totalNormalCustomers`, function (result) {
        if (result.status === 200) {
            $("#totalCustomers").text(result.data);
        }
    }).fail(function (err) {
        console.error("Error loading dashboard data:", err);
    });
}

function loadTotalStockQuantity() {
    $.getJSON(`${API_BASE_URL}/adminDashboard/getTotalStockQuantity`, function (result) {
        if (result.code === 200) {
            $("#total-stock-quantity-adminDashboard").text(result.data + " kg");
        } else {
            console.error("Failed to load total stock quantity:", result);
        }
    }).fail(function (err) {
        console.error("Error fetching total stock quantity:", err);
    });
}

function fetchTotalWeightThisMonth() {
    $.getJSON(`${API_BASE_URL}/adminDashboard/totalWeightThisMonth`, function (data) {
        if (data.code === 200) {
            const formattedWeight = new Intl.NumberFormat().format(data.data) + " kg";
            $("#total-weight-value-adminDashboard").text(formattedWeight);
        } else {
            console.error("Error:", data.message);
        }
    }).fail(function (err) {
        console.error("Fetch error:", err);
    });
}

function fetchBestSupplierToday() {
    $.getJSON(`${API_BASE_URL}/adminDashboard/bestSupplierToday`, function (res) {
        if (res.data) {
            $("#today-top-supplier-name").text(res.data.supplierName);
            $("#today-top-supplier-total-weight").text(res.data.totalSupplied + " kg supplied");
        } else {
            $("#today-top-supplier-name").text("No Suppliers Found Today");
            $("#today-top-supplier-total-weight").text("0 kg supplied");
        }
    }).fail(function (err) {
        console.error("Error fetching top supplier:", err);
        $("#today-top-supplier-name").text("Error loading data");
        $("#today-top-supplier-total-weight").text("--");
    });
}

function getAllSuppliesData() {
    $.getJSON(`${API_BASE_URL}/adminDashboard/getAllTeaLeafCounts`, function (res) {
        if (res.data && res.data.length > 0) {
            allDeliveries = res.data;
            renderTable();
            renderPagination();
        } else {
            $("#all-tea-supplies-table-body").html(
                `<tr><td colspan="6" class="p-2 text-center text-gray-500">No deliveries found</td></tr>`
            );
        }
    }).fail(function (err) {
        console.error("Error fetching tea leaf counts:", err);
        $("#all-tea-supplies-table-body").html(
            `<tr><td colspan="6" class="p-2 text-center text-red-500">Error loading data</td></tr>`
        );
    });
}

function renderTable() {
    const $tableBody = $("#all-tea-supplies-table-body");
    $tableBody.empty();

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageData = allDeliveries.slice(startIndex, endIndex);

    pageData.forEach(item => {
        $tableBody.append(`
            <tr class="border-b">
                <td class="p-2">${item.supplierName}</td>
                <td class="p-2">${item.teaCardNumber}</td>
                <td class="p-2">${item.date} ${item.time}</td>
                <td class="p-2">${item.netWeight} kg</td>
                <td class="p-2">${item.quality}</td>
                <td class="p-2">${item.note || "-"}</td>
            </tr>
        `);
    });
}

function renderPagination() {
    const $pagination = $("#pagination");
    $pagination.empty();

    const totalPages = Math.ceil(allDeliveries.length / itemsPerPage);

    const $prevBtn = $(`<button class="px-3 py-1 border rounded">&laquo; Prev</button>`);
    $prevBtn.prop("disabled", currentPage === 1).on("click", function () {
        if (currentPage > 1) {
            currentPage--;
            renderTable();
            renderPagination();
        }
    });
    $pagination.append($prevBtn);

    for (let i = 1; i <= totalPages; i++) {
        const $pageBtn = $(`<button class="px-3 py-1 border rounded">${i}</button>`);
        if (i === currentPage) $pageBtn.addClass("bg-tea-green text-white");
        $pageBtn.on("click", function () {
            currentPage = i;
            renderTable();
            renderPagination();
        });
        $pagination.append($pageBtn);
    }

    const $nextBtn = $(`<button class="px-3 py-1 border rounded">Next &raquo;</button>`);
    $nextBtn.prop("disabled", currentPage === totalPages).on("click", function () {
        if (currentPage < totalPages) {
            currentPage++;
            renderTable();
            renderPagination();
        }
    });
    $pagination.append($nextBtn);
}

function loadChartInStockLevels() {
    $.getJSON(`${API_BASE_URL}/adminDashboard/getStockLevels`, function (res) {
        if (res.data && res.data.length > 0) {
            const labels = res.data.map(item => item.productName.replace(/_/g, " "));
            const values = res.data.map(item => parseFloat(item.quantity));

            const colors = ["#2E8B57", "#3CB371", "#DAA520", "#FFD700", "#3333ff", "#ff7f50", "#8a2be2"];

            const ctx = $("#teaTypeChart")[0].getContext("2d");
            new Chart(ctx, {
                type: "doughnut",
                data: {
                    labels: labels,
                    datasets: [{
                        data: values,
                        backgroundColor: colors.slice(0, values.length)
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: "right" }
                    }
                }
            });
        } else {
            console.warn("No stock data found");
        }
    }).fail(function (err) {
        console.error("Error fetching stock levels:", err);
    });
}

$(document).ready(function () {
    loadTotalSuppliers();
    loadDashboardData();
    loadTotalStockQuantity();
    fetchTotalWeightThisMonth();
    fetchBestSupplierToday();
    getAllSuppliesData();
    loadChartInStockLevels();
});
