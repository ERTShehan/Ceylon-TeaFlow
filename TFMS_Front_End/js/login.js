const API_BASE = 'http://localhost:8080';

document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const username = (document.getElementById('username') || {}).value?.trim();
        const password = (document.getElementById('password') || {}).value;

        if (!username || !password) {
            alert('Please enter both username and password.');
            return;
        }

        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) submitBtn.disabled = true;

        try {
            const res = await fetch(`${API_BASE}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            const body = await res.json().catch(() => null);

            if (!res.ok) {
                const msg = body?.data || body?.status || res.statusText || 'Login failed';
                throw new Error(msg);
            }

            const auth = body?.data;
            if (!auth || !auth.accessToken) {
                throw new Error('Invalid server response (missing token).');
            }

            localStorage.setItem('ctf_access_token', auth.accessToken);
            localStorage.setItem('ctf_role', auth.role || '');
            if (auth.accessExpiresAtEpochMs) {
                localStorage.setItem('ctf_access_expires_at', String(auth.accessExpiresAtEpochMs));
            }

            const role = auth.role || '';
            if (role === 'CUSTOMER') {
                window.location.href = '../pages/customerDashboard.html';
            } else if (role === 'TEA_LEAF_SUPPLIER') {
                window.location.href = '../pages/supplierDashboard.html';
            } else if (role === 'ADMIN') {
                window.location.href = '../pages/adminDashboard.html';
            } else if (role === 'TEA_MAKER') {
                window.location.href = '../pages/teaMakerDashboard.html';
            } else {
                alert('Unknown user role. Cannot redirect.');
            }

        } catch (err) {
            console.error('Login error:', err);
            alert(err?.message || 'Login failed. See console for details.');
        } finally {
            if (submitBtn) submitBtn.disabled = false;
        }
    });
});
