const API_BASE = 'http://localhost:8080';

document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    if (!form) return;

    const roleRadios = document.querySelectorAll('input[name="role"]');
    const teaCardField = document.getElementById('teaCardField');
    const teaCardInput = document.getElementById('teaCard');

    const updateTeaCardVisibility = () => {
        const selected = document.querySelector('input[name="role"]:checked');
        if (!selected) return;
        teaCardField.style.display = selected.value === 'supplier' ? 'block' : 'none';
    };

    roleRadios.forEach(r => r.addEventListener('change', updateTeaCardVisibility));
    updateTeaCardVisibility();

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const selectedRoleEl = document.querySelector('input[name="role"]:checked');
        const role = selectedRoleEl ? selectedRoleEl.value : 'customer';

        const firstName = (document.getElementById('firstName') || {}).value?.trim();
        const lastName  = (document.getElementById('lastName') || {}).value?.trim();
        const email     = (document.getElementById('email') || {}).value?.trim() || null;
        const address   = (document.getElementById('address') || {}).value?.trim() || null;
        const username  = (document.getElementById('username') || {}).value?.trim();
        const password  = (document.getElementById('password') || {}).value;
        const confirmPassword = (document.getElementById('confirmPassword') || {}).value;
        const teaCardNumber = teaCardInput ? teaCardInput.value?.trim() : null;

        if (!username || !password) {
            alert('Username and password are required.');
            return;
        }
        if (password !== confirmPassword) {
            alert('Passwords do not match.');
            return;
        }
        if (role === 'supplier' && !teaCardNumber) {
            alert('Tea card number is required for suppliers.');
            return;
        }

        const payload = {
            username,
            password,
            firstName,
            lastName,
            email,
            address
        };

        let endpoint = `${API_BASE}/auth/register/customer`;
        if (role === 'supplier') {
            endpoint = `${API_BASE}/auth/register/supplier`;
            payload.teaCardNumber = teaCardNumber;
        }

        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) submitBtn.disabled = true;

        try {
            const res = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const body = await res.json().catch(() => null);

            if (!res.ok) {
                const msg = body?.data || body?.status || res.statusText || 'Registration failed';
                throw new Error(msg);
            }

            const msg = body?.data || 'Registration successful';
            alert(String(msg));

            window.location.href = './login.html';

        } catch (err) {
            console.error('Registration error:', err);
            alert(err?.message || 'Registration failed. See console for details.');
        } finally {
            if (submitBtn) submitBtn.disabled = false;
        }
    });
});
