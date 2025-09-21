const API_BASE = 'http://localhost:8080';

$(document).ready(function () {
    const $form = $('form');
    if ($form.length === 0) return;

    $form.on('submit', async function (e) {
        e.preventDefault();

        const username = $('#username').val()?.trim();
        const password = $('#password').val();

        if (!username || !password) {
            alert('Please enter both username and password.');
            return;
        }

        const $submitBtn = $form.find('button[type="submit"]');
        $submitBtn.prop('disabled', true);

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
            } else if(role === 'STOCK_MANAGER') {
                window.location.href = '../pages/stockManagerDashboard.html';
            }

        } catch (err) {
            console.error('Login error:', err);
            alert(err?.message || 'Login failed. See console for details.');
        } finally {
            $submitBtn.prop('disabled', false);
        }
    });
});
