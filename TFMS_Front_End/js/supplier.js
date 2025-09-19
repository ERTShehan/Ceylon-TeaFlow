// const API_BASE = "http://localhost:8080";
//
// document.addEventListener('DOMContentLoaded', function() {
//     function fetchTeaProducts() {
//         fetch('http://localhost:8080/supplier/teaProduction')
//             .then(response => {
//                 if (!response.ok) {
//                     throw new Error('Network response was not ok');
//                 }
//                 return response.json();
//             })
//             .then(data => {
//                 if (data.data && Array.isArray(data.data)) {
//                     displayTeaProducts(data.data);
//                 } else {
//                     console.error('Unexpected data format:', data);
//                 }
//             })
//             .catch(error => {
//                 console.error('Error fetching tea products:', error);
//                 document.getElementById('products-container').innerHTML =
//                     '<p class="text-red-500 text-center col-span-full">Failed to load products. Please try again later.</p>';
//             });
//     }
//
//     function displayTeaProducts(products) {
//         const productsContainer = document.getElementById('products-container');
//         productsContainer.innerHTML = '';
//
//         products.forEach(product => {
//             const productCard = document.createElement('div');
//             productCard.className = 'product-card border rounded-lg p-4 hover:border-tea-green transition';
//
//             productCard.innerHTML = `
//                         <h3 class="font-semibold">${escapeHtml(product.name)}</h3>
//                         <p class="text-gray-600 text-sm mb-3">${escapeHtml(product.description)}</p>
//                         <div class="flex justify-between items-center">
//                             <p class="text-tea-green font-semibold">LKR ${formatPrice(product.price)}</p>
//                             <p class="font-semibold">${escapeHtml(product.quantity)}</p>
//                         </div>
//                         <button class="mt-4 w-full py-2 bg-tea-green text-white rounded-lg font-semibold hover:bg-tea-green-light transition">
//                             Order now
//                         </button>
//                     `;
//
//             productsContainer.appendChild(productCard);
//         });
//     }
//
//     function escapeHtml(text) {
//         const map = {
//             '&': '&amp;',
//             '<': '&lt;',
//             '>': '&gt;',
//             '"': '&quot;',
//             "'": '&#039;'
//         };
//         return text.toString().replace(/[&<>"']/g, function(m) { return map[m]; });
//     }
//
//     function formatPrice(price) {
//         return parseFloat(price).toFixed(2);
//     }
//
//     fetchTeaProducts();
//
//     const form = document.getElementById("advanceForm");
//     if (!form) return;
//
//     form.addEventListener("submit", async (e) => {
//         e.preventDefault();
//
//         const amount = document.getElementById("amount")?.value?.trim();
//         const reason = document.getElementById("reason")?.value?.trim();
//
//         if (!amount) {
//             alert("Please enter the advance amount.");
//             return;
//         }
//
//         const token = localStorage.getItem("ctf_access_token");
//         if (!token) {
//             alert("You must log in first.");
//             window.location.href = "./login.html";
//             return;
//         }
//
//         try {
//             const res = await fetch(`${API_BASE}/supplier/applyAdvance`, {
//                 method: "POST",
//                 headers: {
//                     "Content-Type": "application/json",
//                     "Authorization": "Bearer " + token
//                 },
//                 body: JSON.stringify({
//                     amount,
//                     reason
//                 })
//             });
//
//             const body = await res.json().catch(() => null);
//
//             if (!res.ok) {
//                 const msg = body?.data || body?.status || res.statusText || "Request failed";
//                 throw new Error(msg);
//             }
//
//             alert(body?.data || "Advance application submitted successfully!");
//             form.reset();
//             loadAdvanceRequests();
//
//         } catch (err) {
//             console.error("Advance apply error:", err);
//             alert(err.message || "Error submitting advance payment.");
//         }
//     });
// });