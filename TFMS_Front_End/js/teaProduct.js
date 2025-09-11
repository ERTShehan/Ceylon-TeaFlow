// teaProductManagement.js
$(document).ready(function() {
    let isEditMode = false;
    let teaProducts = [];
    const baseUrl = 'http://localhost:8080/teaProduct';

    const modal = document.getElementById("teaProductModal");
    const span = document.getElementsByClassName("close")[0];

    span.onclick = function() {
        modal.style.display = "none";
        resetForm();
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
            resetForm();
        }
    }

    function loadTeaProductNames() {
        $.ajax({
            url: `${baseUrl}/teaNames`,
            type: 'GET',
            success: function(response) {
                if (response.code === 200) {
                    const teaNames = response.data;
                    const dropdown = $('#teaProductName');
                    dropdown.empty();
                    dropdown.append('<option value="">-- Select Tea Product --</option>');

                    teaNames.forEach(name => {
                        dropdown.append(`<option value="${name}">${name}</option>`);
                    });
                } else {
                    showToast('error', 'Failed to load tea product names');
                }
            },
            error: function(xhr, status, error) {
                showToast('error', 'Error loading tea product names: ' + error);
            }
        });
    }

    function loadTeaProducts() {
        $.ajax({
            url: `${baseUrl}/getAll`,
            type: 'GET',
            success: function(response) {
                if (response.code === 200) {
                    teaProducts = response.data;
                    renderTeaProductTable(teaProducts);
                } else {
                    showToast('error', 'Failed to load tea products');
                }
            },
            error: function(xhr, status, error) {
                showToast('error', 'Error loading tea products: ' + error);
            }
        });
    }

    function renderTeaProductTable(products) {
        const tableBody = $('#teaProductTableBody');
        tableBody.empty();

        if (products.length === 0) {
            tableBody.append('<tr><td colspan="6" class="p-3 text-center">No tea products found</td></tr>');
            return;
        }

        products.forEach(product => {
            const row = `
                <tr class="border-b">
                    <td class="p-3">${product.id}</td>
                    <td class="p-3">${product.name}</td>
                    <td class="p-3">${product.price}</td>
                    <td class="p-3">${product.quantity}</td>
                    <td class="p-3">${product.description}</td>
                    <td class="p-3">
                        <button class="edit-btn bg-blue-500 text-white px-2 py-1 rounded mr-2" data-id="${product.id}">Edit</button>
                        <button class="delete-btn bg-red-500 text-white px-2 py-1 rounded" data-id="${product.id}">Delete</button>
                    </td>
                </tr>
            `;
            tableBody.append(row);
        });

        $('.edit-btn').click(function() {
            const productId = $(this).data('id');
            editTeaProduct(productId);
        });

        $('.delete-btn').click(function() {
            const productId = $(this).data('id');
            deleteTeaProduct(productId);
        });
    }

    function editTeaProduct(id) {
        const product = teaProducts.find(p => p.id === id);
        if (product) {
            isEditMode = true;
            $('#teaProductId').val(product.id);
            $('#teaProductName').val(product.name);
            $('#teaProductPrice').val(product.price);
            $('#teaProductQuantity').val(product.quantity);
            $('#teaProductDescription').val(product.description);

            $('#formTitle').text('Update Tea Product');
            $('#teaProductSubmitBtn').text('Update Tea Product');
        }
    }

    function deleteTeaProduct(id) {
        Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it!'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `${baseUrl}/delete?id=${id}`,
                    type: 'PUT',
                    success: function(response) {
                        if (response.code === 200) {
                            showToast('success', 'Tea product deleted successfully');
                            loadTeaProducts(); // Reload the table
                        } else {
                            showToast('error', 'Failed to delete tea product');
                        }
                    },
                    error: function(xhr, status, error) {
                        showToast('error', 'Error deleting tea product: ' + error);
                    }
                });
            }
        });
    }

    $('#teaProductSearch').on('input', function() {
        const keyword = $(this).val().trim();

        if (keyword.length === 0) {
            renderTeaProductTable(teaProducts);
            return;
        }

        $.ajax({
            url: `${baseUrl}/search/${keyword}`,
            type: 'GET',
            success: function(response) {
                if (response.code === 200) {
                    renderTeaProductTable(response.data);
                } else {
                    showToast('error', 'Search failed');
                }
            },
            error: function(xhr, status, error) {
                showToast('error', 'Search error: ' + error);
            }
        });
    });

    $('#teaProductForm').submit(function(e) {
        e.preventDefault();

        const productData = {
            id: $('#teaProductId').val(),
            name: $('#teaProductName').val(),
            price: parseFloat($('#teaProductPrice').val()),
            quantity: $('#teaProductQuantity').val(),
            description: $('#teaProductDescription').val()
        };

        if (isEditMode) {
            $.ajax({
                url: `${baseUrl}/update`,
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(productData),
                success: function(response) {
                    if (response.code === 200) {
                        showToast('success', 'Tea product updated successfully');
                        resetForm();
                        loadTeaProducts(); // Reload the table
                    } else {
                        showToast('error', 'Failed to update tea product');
                    }
                },
                error: function(xhr, status, error) {
                    showToast('error', 'Error updating tea product: ' + error);
                }
            });
        } else {
            $.ajax({
                url: `${baseUrl}/add`,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(productData),
                success: function(response) {
                    if (response.code === 201) {
                        showToast('success', 'Tea product added successfully');
                        resetForm();
                        loadTeaProducts();
                    } else {
                        showToast('error', 'Failed to add tea product');
                    }
                },
                error: function(xhr, status, error) {
                    showToast('error', 'Error adding tea product: ' + error);
                }
            });
        }
    });

    function resetForm() {
        isEditMode = false;
        $('#teaProductForm')[0].reset();
        $('#teaProductId').val('');
        $('#formTitle').text('Add New Tea Product');
        $('#teaProductSubmitBtn').text('Add Tea Product');
    }

    function showToast(type, message) {
        let container = document.getElementById('toastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toastContainer';
            container.style.cssText = 'position:fixed;top:20px;right:20px;z-index:9999;';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
        <span class="toast-icon">${getIcon(type)}</span>
        <span class="toast-message">${message}</span>
        <button class="toast-close">✕</button>
    `;

        if (!document.getElementById('toastStyles')) {
            const style = document.createElement('style');
            style.id = 'toastStyles';
            style.textContent = `
            .toast {
                min-width: 300px; padding: 15px 20px; margin-bottom: 10px;
                border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.15);
                display: flex; align-items: center; justify-content: space-between;
                animation: slideIn 0.3s ease forwards; color: white;
                transform: translateX(100%); opacity: 0;
            }
            .toast.success { background: linear-gradient(to right, #48bb78, #38a169); }
            .toast.error { background: linear-gradient(to right, #f56565, #e53e3e); }
            .toast.warning { background: linear-gradient(to right, #ed8936, #dd6b20); }
            .toast.info { background: linear-gradient(to right, #4299e1, #3182ce); }
            .toast-icon { font-size: 20px; margin-right: 12px; }
            .toast-message { font-size: 14px; flex-grow: 1; }
            .toast-close { background: none; border: none; color: white; 
                           cursor: pointer; opacity: 0.7; transition: opacity 0.2s; }
            .toast-close:hover { opacity: 1; }
            @keyframes slideIn { to { transform: translateX(0); opacity: 1; } }
            @keyframes slideOut { to { transform: translateX(100%); opacity: 0; } }
        `;
            document.head.appendChild(style);
        }

        container.appendChild(toast);
        setTimeout(() => toast.style.animation = 'slideIn 0.3s ease forwards', 10);

        const removeToast = () => {
            toast.style.animation = 'slideOut 0.3s ease forwards';
            setTimeout(() => toast.remove(), 300);
        };
        setTimeout(removeToast, 3000);

        toast.querySelector('.toast-close').addEventListener('click', removeToast);
    }

    function getIcon(type) {
        const icons = {
            success: '✅',
            error: '❌',
            warning: '⚠️',
            info: 'ℹ️'
        };
        return icons[type] || icons.info;
    }

    loadTeaProductNames();
    loadTeaProducts();
});