function showToast(message, type = 'success') {
    const existingToasts = document.querySelectorAll('.custom-toast');
    existingToasts.forEach(toast => toast.remove());

    const toast = document.createElement('div');
    toast.className = `custom-toast fixed top-4 right-4 px-4 py-3 rounded-lg shadow-lg z-50 ${
        type === 'success' ? 'bg-green-500 text-white' : 'bg-red-500 text-white'
    }`;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 3000);
}

async function apiCall(endpoint, method = 'GET', data = null) {
    const url = `${API_BASE_URL}${endpoint}`;
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
        },
    };

    if (data && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            let errorDetails = '';
            try {
                const errorResponse = await response.json();
                errorDetails = errorResponse.message || '';
            } catch (e) {
                errorDetails = await response.text();
            }
            throw new Error(`HTTP error! status: ${response.status}, details: ${errorDetails}`);
        }
        return await response.json();
    } catch (error) {
        console.error('API call failed:', error);
        showToast('API request failed: ' + error.message, 'error');
        throw error;
    }
}

const StockManagerManager = {
    currentPage: 1,
    pageSize: 5,
    allStockManagers: [],

    async getAll() {
        try {
            const response = await apiCall('/stockManager/getAll');
            return response.data;
        } catch (error) {
            console.error('Error fetching stock managers:', error);
            return [];
        }
    },

    async search(keyword) {
        try {
            const response = await apiCall(`/stockManager/search/${encodeURIComponent(keyword)}`);
            return response.data;
        } catch (error) {
            console.error('Error searching stock managers:', error);
            return [];
        }
    },

    async add(data) {
        try {
            const response = await apiCall('/stockManager/register', 'POST', data);
            showToast(response.message || 'Stock Manager added successfully');
            return response.data;
        } catch (error) {
            console.error('Error adding stock manager:', error);
            throw error;
        }
    },

    async update(data) {
        try {
            const response = await apiCall('/stockManager/update', 'PUT', data);
            showToast(response.message || 'Stock Manager updated successfully');
            return response.data;
        } catch (error) {
            console.error('Error updating stock manager:', error);
            throw error;
        }
    },

    async delete(id) {
        try {
            const response = await apiCall(`/stockManager/delete?id=${id}`, 'PUT');
            showToast(response.message || 'Stock Manager deleted successfully');
            return response.data;
        } catch (error) {
            console.error('Error deleting stock manager:', error);
            throw error;
        }
    },

    async changeStatus(id) {
        try {
            const response = await apiCall(`/stockManager/changeStatus/${id}`, 'PATCH');
            showToast(response.message || 'Status changed successfully');
            return response.data;
        } catch (error) {
            console.error('Error changing stock manager status:', error);
            throw error;
        }
    },

    renderTable(stockManagers, tableBodyId) {
        const tableBody = document.getElementById(tableBodyId);
        if (!tableBody) return;

        tableBody.innerHTML = '';

        stockManagers.forEach(maker => {
            const row = document.createElement('tr');
            row.className = 'border-b';
            row.innerHTML = `
                <td class="p-3">${maker.id}</td>
                <td class="p-3">${maker.fullName}</td>
                <td class="p-3">${maker.email}</td>
                <td class="p-3">${maker.phoneNumber}</td>
                <td class="p-3">${maker.username}</td>
                <td class="p-3">
                    <span class="px-2 py-1 rounded ${maker.status === 'Active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}">${maker.status}</span>
                </td>
                <td class="p-3">
                    <button class="edit-btn px-2 py-1 bg-blue-500 text-white rounded mr-2" data-id="${maker.id}">Edit</button>
                    <button class="status-btn px-2 py-1 bg-yellow-500 text-white rounded mr-2" data-id="${maker.id}">Change Status</button>
                    <button class="delete-btn px-2 py-1 bg-red-500 text-white rounded" data-id="${maker.id}">Delete</button>
                </td>
            `;
            tableBody.appendChild(row);
        });

        // Event listeners
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const id = e.target.getAttribute('data-id');
                this.openUpdateModal(id);
            });
        });

        document.querySelectorAll('.status-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const id = e.target.getAttribute('data-id');
                const result = await Swal.fire({
                    title: 'Are you sure?',
                    text: 'Do you want to change the status of this stock manager?',
                    icon: 'question',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Yes, change it!'
                });
                if (result.isConfirmed) {
                    await this.changeStatus(id);
                    this.refreshTable();
                }
            });
        });

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const id = e.target.getAttribute('data-id');
                this.deleteHandler(id);
            });
        });
    },

    async openUpdateModal(id) {
        try {
            const stockManagers = await this.getAll();
            const maker = stockManagers.find(m => m.id === id);

            if (maker) {
                document.getElementById('updateStockManagerId').value = maker.id;
                document.getElementById('updateStockManagerName').value = maker.fullName;
                document.getElementById('updateStockManagerEmail').value = maker.email;
                document.getElementById('updateStockManagerPhone').value = maker.phoneNumber;
                document.getElementById('updateStockManagerSalary').value = maker.basicSalary || '';
                document.getElementById('updateStockManagerPassword').value = '';

                const updateForm = document.getElementById('stockManagerUpdateForm');
                updateForm.onsubmit = async (e) => {
                    e.preventDefault();
                    const updatedData = {
                        id: document.getElementById('updateStockManagerId').value,
                        fullName: document.getElementById('updateStockManagerName').value,
                        email: document.getElementById('updateStockManagerEmail').value,
                        phoneNumber: document.getElementById('updateStockManagerPhone').value,
                        basicSalary: parseFloat(document.getElementById('updateStockManagerSalary').value),
                        status: maker.status
                    };
                    const password = document.getElementById('updateStockManagerPassword').value;
                    if (password) {
                        updatedData.password = password;
                    }

                    try {
                        await this.update(updatedData);
                        this.refreshTable();
                        document.getElementById('stockManagerUpdateModal').style.display = 'none';
                    } catch (error) {
                        console.error('Error updating stock manager:', error);
                    }
                };
                document.getElementById('stockManagerUpdateModal').style.display = 'block';
            }
        } catch (error) {
            console.error('Error opening update modal:', error);
            showToast('Error loading stock manager details', 'error');
        }
    },

    async deleteHandler(id) {
        const result = await Swal.fire({
            title: 'Are you sure?',
            text: "This will permanently delete the stock manager!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it!'
        });

        if (result.isConfirmed) {
            try {
                await this.delete(id);
                this.refreshTable();
                Swal.fire('Deleted!', 'Stock manager has been deleted.', 'success');
            } catch (error) {
                Swal.fire('Error!', 'Failed to delete stock manager.', 'error');
            }
        }
    },

    async refreshTable() {
        this.allStockManagers = await this.getAll();
        this.currentPage = 1;
        this.renderPaginatedTable();
    },

    renderPaginatedTable() {
        const start = (this.currentPage - 1) * this.pageSize;
        const end = start + this.pageSize;
        const paginated = this.allStockManagers.slice(start, end);

        this.renderTable(paginated, 'stock-manager-table-body');

        const total = this.allStockManagers.length;
        const info = document.getElementById('stock-manager-pagination-info');
        if (info) {
            const showingFrom = total === 0 ? 0 : start + 1;
            const showingTo = Math.min(end, total);
            info.textContent = `Showing ${showingFrom} to ${showingTo} of ${total} entries`;
        }

        document.getElementById('stock-manager-prev').disabled = this.currentPage === 1;
        document.getElementById('stock-manager-next').disabled = end >= total;
    }
};

document.addEventListener('DOMContentLoaded', () => {
    StockManagerManager.refreshTable();

    const searchInput = document.getElementById('stock-manager-search');
    if (searchInput) {
        searchInput.addEventListener('input', async (e) => {
            const keyword = e.target.value;
            if (keyword.length > 2) {
                const results = await StockManagerManager.search(keyword);
                StockManagerManager.renderTable(results, 'stock-manager-table-body');
            } else if (keyword.length === 0) {
                StockManagerManager.refreshTable();
            }
        });
    }

    document.getElementById('addStockManagerBtn').addEventListener('click', () => {
        document.getElementById('stockManagerForm').reset();
        document.getElementById('stockManagerForm').onsubmit = async (e) => {
            e.preventDefault();
            const data = {
                fullName: document.getElementById('stockManagerName').value,
                email: document.getElementById('stockManagerEmail').value,
                phoneNumber: document.getElementById('stockManagerPhone').value,
                basicSalary: parseFloat(document.getElementById('stockManagerSalary').value),
                username: document.getElementById('stockManagerUsername').value,
                password: document.getElementById('stockManagerPassword').value
            };
            try {
                await StockManagerManager.add(data);
                StockManagerManager.refreshTable();
                document.getElementById('stockManagerModal').style.display = 'none';
            } catch (error) {
                console.error('Error adding stock manager:', error);
            }
        };
        document.getElementById('stockManagerModal').style.display = 'block';
    });

    document.getElementById('stock-manager-prev').addEventListener('click', () => {
        if (StockManagerManager.currentPage > 1) {
            StockManagerManager.currentPage--;
            StockManagerManager.renderPaginatedTable();
        }
    });

    document.getElementById('stock-manager-next').addEventListener('click', () => {
        const total = StockManagerManager.allStockManagers.length;
        if (StockManagerManager.currentPage * StockManagerManager.pageSize < total) {
            StockManagerManager.currentPage++;
            StockManagerManager.renderPaginatedTable();
        }
    });
});
