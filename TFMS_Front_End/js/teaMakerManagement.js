// Add this code to your existing JavaScript section

// Base API URL (adjust based on your backend URL)
const API_BASE_URL = 'http://localhost:8080';

// Tea Maker Management Functions
function loadTeaMakers() {
    fetch(`${API_BASE_URL}/teaMaker/getAll`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                const teaMakers = result.data;
                const tableBody = document.getElementById('tea-maker-table-body');
                tableBody.innerHTML = '';

                teaMakers.forEach(tm => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td class="p-3">${tm.id}</td>
                        <td class="p-3">${tm.fullName}</td>
                        <td class="p-3">${tm.email}</td>
                        <td class="p-3">${tm.phoneNumber}</td>
                        <td class="p-3 status-text">${tm.status ? 'Active' : 'Inactive'}</td>
                        <td class="p-3">
                            <label class="toggle-switch">
                                <input type="checkbox" data-id="${tm.id}" class="status-toggle" ${tm.status ? 'checked' : ''}>
                                <span class="slider"></span>
                            </label>
                            <button class="update-user-btn bg-tea-green text-white px-3 py-1 rounded mr-2" 
                                    data-id="${tm.id}" data-role="tea_maker">Update</button>
                            <button class="delete-user-btn bg-red-500 text-white px-3 py-1 rounded" 
                                    data-id="${tm.id}">Delete</button>
                        </td>
                    `;
                    tableBody.appendChild(row);
                });

                // Reattach event listeners
                attachEventListeners();
                setupTablePagination('tea-maker-table-body', 'tea-maker-search', 'tea-maker-prev', 'tea-maker-next', 'tea-maker-pagination-info');
            } else {
                showToast('Error loading tea makers: ' + result.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Error loading tea makers');
        });
}

function addTeaMaker(teaMakerData) {
    fetch(`${API_BASE_URL}/teaMaker/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(teaMakerData)
    })
        .then(response => response.json())
        .then(result => {
            if (result.code === 201) {
                showToast(result.message);
                document.getElementById('addUserModal').style.display = 'none';
                loadTeaMakers(); // Reload the table
            } else {
                showToast('Error: ' + result.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Error adding tea maker');
        });
}

function updateTeaMaker(teaMakerData) {
    fetch(`${API_BASE_URL}/teaMaker/update`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(teaMakerData)
    })
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                showToast(result.message);
                document.getElementById('updateUserModal').style.display = 'none';
                loadTeaMakers(); // Reload the table
            } else {
                showToast('Error: ' + result.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Error updating tea maker');
        });
}

function deleteTeaMaker(id) {
    fetch(`${API_BASE_URL}/teaMaker/delete?id=${id}`, {
        method: 'PUT'
    })
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                showToast(result.message);
                loadTeaMakers(); // Reload the table
            } else {
                showToast('Error: ' + result.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Error deleting tea maker');
        });
}

function changeTeaMakerStatus(id, checked) {
    fetch(`${API_BASE_URL}/teaMaker/changeStatus/${id}`, {
        method: 'PATCH'
    })
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                showToast(result.message);

                // Update the status text in the table
                const rows = document.querySelectorAll('#tea-maker-table-body tr');
                rows.forEach(row => {
                    const toggle = row.querySelector('.status-toggle');
                    if (toggle && toggle.getAttribute('data-id') === id) {
                        const statusCell = row.querySelector('.status-text');
                        if (statusCell) {
                            statusCell.textContent = checked ? 'Active' : 'Inactive';
                        }
                    }
                });
            } else {
                // Revert the toggle if failed
                const toggle = document.querySelector(`.status-toggle[data-id="${id}"]`);
                if (toggle) {
                    toggle.checked = !checked;
                }
                showToast('Error: ' + result.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            // Revert the toggle
            const toggle = document.querySelector(`.status-toggle[data-id="${id}"]`);
            if (toggle) {
                toggle.checked = !checked;
            }
            showToast('Error changing status');
        });
}

function searchTeaMakers(keyword) {
    fetch(`${API_BASE_URL}/teaMaker/search/${encodeURIComponent(keyword)}`)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                const teaMakers = result.data;
                const tableBody = document.getElementById('tea-maker-table-body');
                tableBody.innerHTML = '';

                teaMakers.forEach(tm => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                    <td class="p-3">${tm.id}</td>
                    <td class="p-3">${tm.fullName}</td>
                    <td class="p-3">${tm.email}</td>
                    <td class="p-3">${tm.phoneNumber}</td>
                    <td class="p-3 status-text">${tm.status ? 'Active' : 'Inactive'}</td>
                    <td class="p-3">
                        <label class="toggle-switch">
                            <input type="checkbox" data-id="${tm.id}" class="status-toggle" ${tm.status ? 'checked' : ''}>
                            <span class="slider"></span>
                        </label>
                        <button class="update-user-btn bg-tea-green text-white px-3 py-1 rounded mr-2" 
                                data-id="${tm.id}" data-role="tea_maker">Update</button>
                        <button class="delete-user-btn bg-red-500 text-white px-3 py-1 rounded" 
                                data-id="${tm.id}">Delete</button>
                    </td>
                `;
                    tableBody.appendChild(row);
                });

                // Reattach event listeners
                attachEventListeners();
            } else {
                showToast('Error searching tea makers: ' + result.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Error searching tea makers');
        });
}

// Enhanced Event Listener Attachment
function attachEventListeners() {
    // Status toggle change handler
    document.querySelectorAll('.status-toggle').forEach(toggle => {
        toggle.addEventListener('change', function() {
            const id = this.getAttribute('data-id');
            const checked = this.checked;
            changeTeaMakerStatus(id, checked);
        });
    });

    // Update button click handler
    document.querySelectorAll('.update-user-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const role = this.getAttribute('data-role');

            // Fetch tea maker details
            fetch(`${API_BASE_URL}/teaMaker/getAll`)
                .then(response => response.json())
                .then(result => {
                    if (result.code === 200) {
                        const teaMaker = result.data.find(tm => tm.id === id);
                        if (teaMaker) {
                            // Fill the update form with tea maker data
                            document.getElementById('updateId').value = teaMaker.id;
                            document.getElementById('updateName').value = teaMaker.fullName;
                            document.getElementById('updateEmail').value = teaMaker.email;
                            document.getElementById('updatePhoneNumber').value = teaMaker.phoneNumber;
                            document.getElementById('updateBasicSalary').value = teaMaker.basicSalary;
                            document.getElementById('updateUsername').value = teaMaker.username;

                            // Show the update modal
                            document.getElementById('updateUserModal').style.display = 'block';
                        } else {
                            showToast('Tea maker not found');
                        }
                    } else {
                        showToast('Error fetching tea maker details');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showToast('Error fetching tea maker details');
                });
        });
    });

    // Delete button click handler
    document.querySelectorAll('.delete-user-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const id = this.getAttribute('data-id');

            Swal.fire({
                title: 'Delete Tea Maker?',
                text: "Are you sure you want to delete this tea maker? This action cannot be undone.",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#2E8B57',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes, delete!'
            }).then((result) => {
                if (result.isConfirmed) {
                    deleteTeaMaker(id);
                }
            });
        });
    });
}

// Modified Form Submission Handlers
document.getElementById('userForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const role = document.getElementById('role').value;
    const teaMakerData = {
        fullName: document.getElementById('name').value,
        email: document.getElementById('email').value,
        phoneNumber: document.getElementById('phoneNumber').value,
        basicSalary: document.getElementById('basicSalary').value,
        username: document.getElementById('username').value,
        password: document.getElementById('password').value,
        status: true // Default to active
    };

    if (role === 'tea_maker') {
        addTeaMaker(teaMakerData);
    }
    // Add other role handlers here
});

document.getElementById('updateUserForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const teaMakerData = {
        id: document.getElementById('updateId').value,
        fullName: document.getElementById('updateName').value,
        email: document.getElementById('updateEmail').value,
        phoneNumber: document.getElementById('updatePhoneNumber').value,
        basicSalary: document.getElementById('updateBasicSalary').value,
        username: document.getElementById('updateUsername').value,
        password: document.getElementById('updatePassword') ? document.getElementById('updatePassword').value : null
    };

    updateTeaMaker(teaMakerData);
});

// Search functionality
document.getElementById('tea-maker-search').addEventListener('input', function() {
    const keyword = this.value.trim();
    if (keyword.length > 2) {
        searchTeaMakers(keyword);
    } else if (keyword.length === 0) {
        loadTeaMakers(); // Reload all if search is cleared
    }
});

// Load tea makers when the page loads
document.addEventListener('DOMContentLoaded', function() {
    loadTeaMakers();
});