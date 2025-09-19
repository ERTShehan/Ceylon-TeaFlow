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
                this.openUpdateModal(id);
            });
        });

        document.querySelectorAll('.status-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const id = e.target.getAttribute('data-id');

                const result = await Swal.fire({
                    title: 'Are you sure?',
                    text: 'Do you want to change the status of this tea maker?',
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
            const teaMakers = await this.getAll();
            const maker = teaMakers.find(m => m.id === id);

            if (maker) {
                document.getElementById('teaMakerUpdateForm').dataset.originalUsername = maker.username;

                document.getElementById('updateTeaMakerId').value = maker.id;
                document.getElementById('updateTeaMakerName').value = maker.fullName;
                document.getElementById('updateTeaMakerEmail').value = maker.email;
                document.getElementById('updateTeaMakerPhone').value = maker.phoneNumber;
                document.getElementById('updateTeaMakerSalary').value = maker.basicSalary;
                document.getElementById('updateTeaMakerPassword').value = '';

                const updateForm = document.getElementById('teaMakerUpdateForm');
                updateForm.onsubmit = async (e) => {
                    e.preventDefault();

                    const updatedData = {
                        id: document.getElementById('updateTeaMakerId').value,
                        fullName: document.getElementById('updateTeaMakerName').value,
                        email: document.getElementById('updateTeaMakerEmail').value,
                        phoneNumber: document.getElementById('updateTeaMakerPhone').value,
                        basicSalary: parseFloat(document.getElementById('updateTeaMakerSalary').value),
                        status: maker.status
                    };

                    const password = document.getElementById('updateTeaMakerPassword').value;
                    if (password) {
                        updatedData.password = password;
                    }

                    try {
                        await this.update(updatedData);
                        this.refreshTable();
                        document.getElementById('teaMakerUpdateModal').style.display = 'none';
                    } catch (error) {
                        console.error('Error updating tea maker:', error);
                    }
                };

                document.getElementById('teaMakerUpdateModal').style.display = 'block';
            }
        } catch (error) {
            console.error('Error opening update modal:', error);
            showToast('Error loading tea maker details', 'error');
        }
    },

    async deleteHandler(id) {
        const result = await Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
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
                Swal.fire(
                    'Deleted!',
                    'Tea maker has been deleted.',
                    'success'
                );
            } catch (error) {
                console.error('Error deleting tea maker:', error);
                Swal.fire(
                    'Error!',
                    'Failed to delete tea maker.',
                    'error'
                );
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

        const total = this.allTeaMakers.length;
        const info = document.getElementById('tea-maker-pagination-info');
        if (info) {
            const showingFrom = total === 0 ? 0 : start + 1;
            const showingTo = Math.min(end, total);
            info.textContent = `Showing ${showingFrom} to ${showingTo} of ${total} entries`;
        }

        document.getElementById('tea-maker-prev').disabled = this.currentPage === 1;
        document.getElementById('tea-maker-next').disabled = end >= total;
    }
};

document.addEventListener('DOMContentLoaded', function() {
    TeaMakerManager.refreshTable();

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

    document.getElementById('addTeaMakerBtn').addEventListener('click', () => {
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

    window.addEventListener('click', function(event) {
        const modals = document.querySelectorAll('.modal');
        modals.forEach(modal => {
            if (event.target === modal) {
                modal.style.display = 'none';
            }
        });
    });

    document.querySelectorAll('.close').forEach(btn => {
        btn.addEventListener('click', function() {
            this.closest('.modal').style.display = 'none';
        });
    });
});

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