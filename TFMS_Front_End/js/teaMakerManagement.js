const API_BASE_URL = 'http://localhost:8080';

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

// API call helper function
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
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('API call failed:', error);
        showToast('API request failed: ' + error.message, 'error');
        throw error;
    }
}

const TeaMakerManager = {
    currentPage: 1,
    pageSize: 5,
    allTeaMakers: [],

    async getAll() {
        try {
            const response = await apiCall('/teaMaker/getAll');
            return response.data;
        } catch (error) {
            console.error('Error fetching tea makers:', error);
            return [];
        }
    },

    async search(keyword) {
        try {
            const response = await apiCall(`/teaMaker/search/${encodeURIComponent(keyword)}`);
            return response.data;
        } catch (error) {
            console.error('Error searching tea makers:', error);
            return [];
        }
    },

    async add(teaMakerData) {
        try {
            const response = await apiCall('/teaMaker/register', 'POST', teaMakerData);
            showToast(response.message || 'Tea maker added successfully');
            return response.data;
        } catch (error) {
            console.error('Error adding tea maker:', error);
            throw error;
        }
    },

    async update(teaMakerData) {
        try {
            const response = await apiCall('/teaMaker/update', 'PUT', teaMakerData);
            showToast(response.message || 'Tea maker updated successfully');
            return response.data;
        } catch (error) {
            console.error('Error updating tea maker:', error);
            throw error;
        }
    },

    async delete(id) {
        try {
            const response = await apiCall(`/teaMaker/delete?id=${id}`, 'PUT');
            showToast(response.message || 'Tea maker deleted successfully');
            return response.data;
        } catch (error) {
            console.error('Error deleting tea maker:', error);
            throw error;
        }
    },

    async changeStatus(id) {
        try {
            const response = await apiCall(`/teaMaker/changeStatus/${id}`, 'PATCH');
            showToast(response.message || 'Status changed successfully');
            return response.data;
        } catch (error) {
            console.error('Error changing tea maker status:', error);
            throw error;
        }
    },

    renderTable(teaMakers, tableBodyId) {
        const tableBody = document.getElementById(tableBodyId);
        if (!tableBody) return;

        tableBody.innerHTML = '';

        teaMakers.forEach(maker => {
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

        // Add event listeners
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const id = e.target.getAttribute('data-id');
                this.openEditModal(id);
            });
        });

        document.querySelectorAll('.status-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const id = e.target.getAttribute('data-id');
                if (confirm('Are you sure you want to change the status of this tea maker?')) {
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

    async openEditModal(id) {
        try {
            const teaMakers = await this.getAll();
            const maker = teaMakers.find(m => m.id === id);

            if (maker) {
                // Populate the form
                document.getElementById('teaMakerId').value = maker.id;
                document.getElementById('teaMakerName').value = maker.fullName;
                document.getElementById('teaMakerEmail').value = maker.email;
                document.getElementById('teaMakerPhone').value = maker.phoneNumber;
                document.getElementById('teaMakerSalary').value = maker.basicSalary;
                document.getElementById('teaMakerUsername').value = maker.username;

                // Change the form submit handler to update instead of create
                const form = document.getElementById('teaMakerForm');
                form.onsubmit = async (e) => {
                    e.preventDefault();
                    const updatedData = {
                        id: document.getElementById('teaMakerId').value,
                        fullName: document.getElementById('teaMakerName').value,
                        email: document.getElementById('teaMakerEmail').value,
                        phoneNumber: document.getElementById('teaMakerPhone').value,
                        basicSalary: parseFloat(document.getElementById('teaMakerSalary').value),
                        username: document.getElementById('teaMakerUsername').value,
                        password: document.getElementById('teaMakerPassword').value || undefined
                    };

                    await this.update(updatedData);
                    this.refreshTable();
                    document.getElementById('teaMakerModal').style.display = 'none';
                };

                // Show the modal
                document.getElementById('teaMakerModal').style.display = 'block';
            }
        } catch (error) {
            console.error('Error opening edit modal:', error);
        }
    },

    async deleteHandler(id) {
        if (confirm('Are you sure you want to delete this tea maker?')) {
            try {
                await this.delete(id);
                this.refreshTable();
            } catch (error) {
                console.error('Error deleting tea maker:', error);
            }
        }
    },

    async refreshTable() {
        this.allTeaMakers = await this.getAll();
        this.currentPage = 1;
        this.renderPaginatedTable();
    },
    renderPaginatedTable() {
        const start = (this.currentPage - 1) * this.pageSize;
        const end = start + this.pageSize;
        const paginatedTeaMakers = this.allTeaMakers.slice(start, end);

        this.renderTable(paginatedTeaMakers, 'tea-maker-table-body');

        // Update pagination info
        const total = this.allTeaMakers.length;
        const info = document.getElementById('tea-maker-pagination-info');
        if (info) {
            const showingFrom = total === 0 ? 0 : start + 1;
            const showingTo = Math.min(end, total);
            info.textContent = `Showing ${showingFrom} to ${showingTo} of ${total} entries`;
        }

        // Enable/disable buttons
        document.getElementById('tea-maker-prev').disabled = this.currentPage === 1;
        document.getElementById('tea-maker-next').disabled = end >= total;
    }
};


// Tea Product Management
const TeaProductManager = {
    async getAll() {
        try {
            const response = await apiCall('/teaProduct/getAll');
            return response.data;
        } catch (error) {
            console.error('Error fetching tea products:', error);
            return [];
        }
    },

    async search(keyword) {
        try {
            const response = await apiCall(`/teaProduct/search/${encodeURIComponent(keyword)}`);
            return response.data;
        } catch (error) {
            console.error('Error searching tea products:', error);
            return [];
        }
    },

    async add(productData) {
        try {
            const response = await apiCall('/teaProduct/add', 'POST', productData);
            showToast(response.message || 'Tea product added successfully');
            return response.data;
        } catch (error) {
            console.error('Error adding tea product:', error);
            throw error;
        }
    },

    async update(productData) {
        try {
            const response = await apiCall('/teaProduct/update', 'PUT', productData);
            showToast(response.message || 'Tea product updated successfully');
            return response.data;
        } catch (error) {
            console.error('Error updating tea product:', error);
            throw error;
        }
    },

    async delete(id) {
        try {
            const response = await apiCall(`/teaProduct/delete?id=${id}`, 'PUT');
            showToast(response.message || 'Tea product deleted successfully');
            return response.data;
        } catch (error) {
            console.error('Error deleting tea product:', error);
            throw error;
        }
    },

    renderTable(products, tableBodyId) {
        const tableBody = document.getElementById(tableBodyId);
        if (!tableBody) return;

        tableBody.innerHTML = '';

        products.forEach(product => {
            const row = document.createElement('tr');
            row.className = 'border-b';
            row.innerHTML = `
                <td class="p-3">${product.id}</td>
                <td class="p-3">${product.name}</td>
                <td class="p-3">${product.price}</td>
                <td class="p-3">${product.quantity}</td>
                <td class="p-3">${product.description}</td>
                <td class="p-3">
                    <button class="edit-btn px-2 py-1 bg-blue-500 text-white rounded mr-2" data-id="${product.id}">Edit</button>
                    <button class="delete-btn px-2 py-1 bg-red-500 text-white rounded" data-id="${product.id}">Delete</button>
                </td>
            `;
            tableBody.appendChild(row);
        });

        // Add event listeners
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const id = e.target.getAttribute('data-id');
                this.openEditModal(id);
            });
        });

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const id = e.target.getAttribute('data-id');
                this.deleteHandler(id);
            });
        });
    },

    async openEditModal(id) {
        try {
            const products = await this.getAll();
            const product = products.find(p => p.id === id);

            if (product) {
                // Populate the form
                document.getElementById('teaProductId').value = product.id;
                document.getElementById('teaProductName').value = product.name;
                document.getElementById('teaProductPrice').value = product.price;
                document.getElementById('teaProductQuantity').value = product.quantity;
                document.getElementById('teaProductDescription').value = product.description;

                // Change the form submit handler to update instead of create
                const form = document.getElementById('teaProductForm');
                form.onsubmit = async (e) => {
                    e.preventDefault();
                    const updatedData = {
                        id: document.getElementById('teaProductId').value,
                        name: document.getElementById('teaProductName').value,
                        price: parseFloat(document.getElementById('teaProductPrice').value),
                        quantity: document.getElementById('teaProductQuantity').value,
                        description: document.getElementById('teaProductDescription').value
                    };

                    await this.update(updatedData);
                    this.refreshTable();
                    document.getElementById('teaProductModal').style.display = 'none';
                };

                // Show the modal
                document.getElementById('teaProductModal').style.display = 'block';
            }
        } catch (error) {
            console.error('Error opening edit modal:', error);
        }
    },

    async deleteHandler(id) {
        if (confirm('Are you sure you want to delete this tea product?')) {
            try {
                await this.delete(id);
                this.refreshTable();
            } catch (error) {
                console.error('Error deleting tea product:', error);
            }
        }
    },

    async refreshTable() {
        const products = await this.getAll();
        this.renderTable(products, 'teaProductTableBody');
    }
};

document.getElementById('tea-maker-prev').addEventListener('click', () => {
    if (TeaMakerManager.currentPage > 1) {
        TeaMakerManager.currentPage--;
        TeaMakerManager.renderPaginatedTable();
    }
});

document.getElementById('tea-maker-next').addEventListener('click', () => {
    const totalPages = Math.ceil(TeaMakerManager.allTeaMakers.length / TeaMakerManager.pageSize);
    if (TeaMakerManager.currentPage < totalPages) {
        TeaMakerManager.currentPage++;
        TeaMakerManager.renderPaginatedTable();
    }
});

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    // Initialize Tea Maker Manager
    TeaMakerManager.refreshTable();

    // Initialize Tea Product Manager
    TeaProductManager.refreshTable();

    // Set up search functionality for tea makers
    const teaMakerSearch = document.getElementById('tea-maker-search');
    if (teaMakerSearch) {
        teaMakerSearch.addEventListener('input', async (e) => {
            const keyword = e.target.value;
            if (keyword.length > 2) {
                const results = await TeaMakerManager.search(keyword);
                TeaMakerManager.renderTable(results, 'tea-maker-table-body');
            } else if (keyword.length === 0) {
                TeaMakerManager.refreshTable();
            }
        });
    }

    // Set up search functionality for tea products
    const teaProductSearch = document.getElementById('teaProductSearch');
    if (teaProductSearch) {
        teaProductSearch.addEventListener('input', async (e) => {
            const keyword = e.target.value;
            if (keyword.length > 2) {
                const results = await TeaProductManager.search(keyword);
                TeaProductManager.renderTable(results, 'teaProductTableBody');
            } else if (keyword.length === 0) {
                TeaProductManager.refreshTable();
            }
        });
    }

    // Set up modal open buttons
    document.getElementById('addTeaMakerBtn').addEventListener('click', () => {
        // Reset form and set to create mode
        document.getElementById('teaMakerForm').reset();
        document.getElementById('teaMakerForm').onsubmit = async (e) => {
            e.preventDefault();
            const teaMakerData = {
                fullName: document.getElementById('teaMakerName').value,
                email: document.getElementById('teaMakerEmail').value,
                phoneNumber: document.getElementById('teaMakerPhone').value,
                basicSalary: parseFloat(document.getElementById('teaMakerSalary').value),
                username: document.getElementById('teaMakerUsername').value,
                password: document.getElementById('teaMakerPassword').value
            };

            console.log(teaMakerData);

            try {
                await TeaMakerManager.add(teaMakerData);
                TeaMakerManager.refreshTable();
                document.getElementById('teaMakerModal').style.display = 'none';
            } catch (error) {
                console.error('Error adding tea maker:', error);
            }
        };

        document.getElementById('teaMakerModal').style.display = 'block';
    });

    document.getElementById('addTeaProductBtn').addEventListener('click', () => {
        // Reset form and set to create mode
        document.getElementById('teaProductForm').reset();
        document.getElementById('teaProductForm').onsubmit = async (e) => {
            e.preventDefault();
            const productData = {
                name: document.getElementById('teaProductName').value,
                price: parseFloat(document.getElementById('teaProductPrice').value),
                quantity: document.getElementById('teaProductQuantity').value,
                description: document.getElementById('teaProductDescription').value
            };

            try {
                await TeaProductManager.add(productData);
                TeaProductManager.refreshTable();
                document.getElementById('teaProductModal').style.display = 'none';
            } catch (error) {
                console.error('Error adding tea product:', error);
            }
        };

        document.getElementById('teaProductModal').style.display = 'block';
    });

    // Set up other modal open buttons similarly for other entities

    // Close modals when clicking outside
    window.addEventListener('click', function(event) {
        const modals = document.querySelectorAll('.modal');
        modals.forEach(modal => {
            if (event.target === modal) {
                modal.style.display = 'none';
            }
        });
    });

    // Close modals with close buttons
    document.querySelectorAll('.close').forEach(btn => {
        btn.addEventListener('click', function() {
            this.closest('.modal').style.display = 'none';
        });
    });
});