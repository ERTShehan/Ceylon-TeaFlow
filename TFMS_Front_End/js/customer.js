document.addEventListener('DOMContentLoaded', function() {
    // Function to fetch tea products from the backend
    function fetchTeaProducts() {
        fetch('http://localhost:8080/customer/teaProduction')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (data.data && Array.isArray(data.data)) {
                    displayTeaProducts(data.data);
                } else {
                    console.error('Unexpected data format:', data);
                }
            })
            .catch(error => {
                console.error('Error fetching tea products:', error);
                // Display error message in the products container
                document.getElementById('products-container').innerHTML =
                    '<p class="text-red-500 text-center col-span-full">Failed to load products. Please try again later.</p>';
            });
    }

    // Function to display tea products in the UI
    function displayTeaProducts(products) {
        const productsContainer = document.getElementById('products-container');
        productsContainer.innerHTML = ''; // Clear any existing content

        products.forEach(product => {
            const productCard = document.createElement('div');
            productCard.className = 'product-card border rounded-lg p-4 hover:border-tea-green transition';

            productCard.innerHTML = `
                        <h3 class="font-semibold">${escapeHtml(product.name)}</h3>
                        <p class="text-gray-600 text-sm mb-3">${escapeHtml(product.description)}</p>
                        <div class="flex justify-between items-center">
                            <p class="text-tea-green font-semibold">LKR ${formatPrice(product.price)}</p>
                            <p class="font-semibold">${escapeHtml(product.quantity)}</p>
                        </div>
                        <button class="mt-4 w-full py-2 bg-tea-green text-white rounded-lg font-semibold hover:bg-tea-green-light transition">
                            Order now
                        </button>
                    `;

            productsContainer.appendChild(productCard);
        });
    }

    // Helper function to escape HTML characters
    function escapeHtml(text) {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.toString().replace(/[&<>"']/g, function(m) { return map[m]; });
    }

    // Helper function to format price
    function formatPrice(price) {
        return parseFloat(price).toFixed(2);
    }

    // Fetch tea products when the page loads
    fetchTeaProducts();
});