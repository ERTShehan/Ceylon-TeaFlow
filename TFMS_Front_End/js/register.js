const API_BASE = 'http://localhost:8080';

$(document).ready(function () {
    const $form = $('form');
    if ($form.length === 0) return;

    const $firstName = $('#firstName');
    const $lastName = $('#lastName');
    const $email = $('#email');
    const $username = $('#username');
    const $address = $('#address');
    const $password = $('#password');
    const $confirmPassword = $('#confirmPassword');
    const $teaCardInput = $('#teaCard');
    const $termsCheckbox = $('#terms');
    const $roleRadios = $('input[name="role"]');
    const $teaCardField = $('#teaCardField');

    function showError($input, message) {
        const $formControl = $input.parent();
        $formControl.removeClass('success').addClass('error');

        let $errorElement = $formControl.find('.error-message');
        if ($errorElement.length === 0) {
            $errorElement = $('<small class="error-message text-red-500"></small>');
            $formControl.append($errorElement);
        }

        $errorElement.text(message);
    }

    function showSuccess($input) {
        const $formControl = $input.parent();
        $formControl.removeClass('error').addClass('success');
        $formControl.find('.error-message').remove();
    }

    function isValidEmail(email) {
        const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }

    function checkRequired($inputs) {
        let isAllValid = true;
        $inputs.each(function () {
            const $input = $(this);
            if (!$input.val().trim()) {
                showError($input, `${getFieldName($input)} is required`);
                isAllValid = false;
            } else {
                showSuccess($input);
            }
        });
        return isAllValid;
    }

    function getFieldName($input) {
        const id = $input.attr('id') || 'Field';
        return id.charAt(0).toUpperCase() + id.slice(1).replace(/([A-Z])/g, ' $1');
    }

    function checkLength($input, min, max) {
        const val = $input.val();
        if (val.length < min) {
            showError($input, `${getFieldName($input)} must be at least ${min} characters`);
            return false;
        } else if (val.length > max) {
            showError($input, `${getFieldName($input)} must be less than ${max} characters`);
            return false;
        } else {
            showSuccess($input);
            return true;
        }
    }

    function checkPasswordsMatch($input1, $input2) {
        if ($input1.val() !== $input2.val()) {
            showError($input2, 'Passwords do not match');
            return false;
        } else {
            showSuccess($input2);
            return true;
        }
    }

    function checkPasswordStrength($input) {
        const strongRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})/;
        if (!strongRegex.test($input.val())) {
            showError($input, 'Password must be at least 8 characters with uppercase, lowercase, and number');
            return false;
        } else {
            showSuccess($input);
            return true;
        }
    }

    function validateTeaCard($input) {
        const teaCardRegex = /^TC-\d{4}-\d{3}$/;
        if (!teaCardRegex.test($input.val())) {
            showError($input, 'Tea Card must be in format TC-1234-567');
            return false;
        } else {
            showSuccess($input);
            return true;
        }
    }

    function checkTerms($checkbox) {
        if (!$checkbox.prop('checked')) {
            const $termsControl = $checkbox.parent();
            $termsControl.addClass('error');

            let $errorElement = $termsControl.find('.error-message');
            if ($errorElement.length === 0) {
                $errorElement = $('<small class="error-message text-red-500 block mt-1"></small>');
                $termsControl.append($errorElement);
            }

            $errorElement.text('You must agree to the terms and conditions');
            return false;
        } else {
            const $termsControl = $checkbox.parent();
            $termsControl.removeClass('error');
            $termsControl.find('.error-message').remove();
            return true;
        }
    }

    function setupRealTimeValidation() {
        $email.on('blur', function () {
            if ($email.val().trim() && !isValidEmail($email.val())) {
                showError($email, 'Email is not valid');
            } else if ($email.val().trim()) {
                showSuccess($email);
            }
        });

        $password.on('input', function () {
            if ($password.val().trim()) {
                checkPasswordStrength($password);
                if ($confirmPassword.val().trim()) {
                    checkPasswordsMatch($password, $confirmPassword);
                }
            }
        });

        $confirmPassword.on('input', function () {
            if ($confirmPassword.val().trim() && $password.val().trim()) {
                checkPasswordsMatch($password, $confirmPassword);
            }
        });

        $teaCardInput.on('blur', function () {
            const isSupplier = $('input[name="role"]:checked').val() === 'supplier';
            if (isSupplier && $teaCardInput.val().trim()) {
                validateTeaCard($teaCardInput);
            }
        });

        $username.on('blur', function () {
            if ($username.val().trim()) {
                checkLength($username, 3, 20);
            }
        });

        $firstName.on('blur', function () {
            if ($firstName.val().trim()) {
                checkLength($firstName, 2, 30);
            }
        });

        $lastName.on('blur', function () {
            if ($lastName.val().trim()) {
                checkLength($lastName, 2, 30);
            }
        });
    }

    function updateTeaCardVisibility() {
        const selected = $('input[name="role"]:checked').val();
        if ($teaCardField.length) {
            $teaCardField.css('display', selected === 'supplier' ? 'block' : 'none');
        }
    }

    $roleRadios.on('change', updateTeaCardVisibility);
    updateTeaCardVisibility();

    $form.on('submit', async function (e) {
        e.preventDefault();

        const isRequiredValid = checkRequired($([$firstName[0], $lastName[0], $email[0], $username[0], $address[0], $password[0], $confirmPassword[0]]));

        let isEmailValid = !$email.val().trim() || isValidEmail($email.val());
        if (!isEmailValid) showError($email, 'Email is not valid'); else showSuccess($email);

        let isPasswordStrong = !$password.val().trim() || checkPasswordStrength($password);
        let isPasswordMatch = (!$password.val().trim() || !$confirmPassword.val().trim()) || checkPasswordsMatch($password, $confirmPassword);

        let isUsernameValid = !$username.val().trim() || checkLength($username, 3, 20);
        let isFirstNameValid = !$firstName.val().trim() || checkLength($firstName, 2, 30);
        let isLastNameValid = !$lastName.val().trim() || checkLength($lastName, 2, 30);

        const isSupplier = $('input[name="role"]:checked').val() === 'supplier';
        let isTeaCardValid = true;
        if (isSupplier) {
            if (!$teaCardInput.val().trim()) {
                showError($teaCardInput, 'Tea Card is required for suppliers');
                isTeaCardValid = false;
            } else {
                isTeaCardValid = validateTeaCard($teaCardInput);
            }
        }

        const isTermsAgreed = checkTerms($termsCheckbox);

        if (!(isRequiredValid && isEmailValid && isPasswordStrong && isPasswordMatch &&
            isUsernameValid && isFirstNameValid && isLastNameValid && isTeaCardValid && isTermsAgreed)) {
            return;
        }

        const role = $('input[name="role"]:checked').val() || 'customer';
        const payload = {
            username: $username.val().trim(),
            password: $password.val(),
            firstName: $firstName.val().trim(),
            lastName: $lastName.val().trim(),
            email: $email.val().trim() || null,
            address: $address.val().trim() || null
        };

        let endpoint = `${API_BASE}/auth/register/customer`;
        if (role === 'supplier') {
            endpoint = `${API_BASE}/auth/register/supplier`;
            payload.teaCardNumber = $teaCardInput.val().trim();
        }

        const $submitBtn = $form.find('button[type="submit"]');
        $submitBtn.prop('disabled', true);

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

            alert(String(body?.data || 'Registration successful'));
            window.location.href = './login.html';

        } catch (err) {
            console.error('Registration error:', err);
            alert(err?.message || 'Registration failed. See console for details.');
        } finally {
            $submitBtn.prop('disabled', false);
        }
    });

    setupRealTimeValidation();
});
