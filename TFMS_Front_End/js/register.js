const API_BASE = 'http://localhost:8080';

document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    if (!form) return;

    // Get all input elements
    const firstName = document.getElementById('firstName');
    const lastName = document.getElementById('lastName');
    const email = document.getElementById('email');
    const username = document.getElementById('username');
    const address = document.getElementById('address');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const teaCardInput = document.getElementById('teaCard');
    const termsCheckbox = document.getElementById('terms');
    const roleRadios = document.querySelectorAll('input[name="role"]');
    const teaCardField = document.getElementById('teaCardField');

    // Helper function to show error messages
    function showError(input, message) {
        const formControl = input.parentElement;
        formControl.classList.remove('success');
        formControl.classList.add('error');

        // Check if error message already exists
        let errorElement = formControl.querySelector('.error-message');
        if (!errorElement) {
            errorElement = document.createElement('small');
            errorElement.className = 'error-message text-red-500';
            formControl.appendChild(errorElement);
        }

        errorElement.innerText = message;
    }

    // Helper function to show success state
    function showSuccess(input) {
        const formControl = input.parentElement;
        formControl.classList.remove('error');
        formControl.classList.add('success');

        // Remove any existing error message
        const errorElement = formControl.querySelector('.error-message');
        if (errorElement) {
            errorElement.remove();
        }
    }

    // Validate email format
    function isValidEmail(email) {
        const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }

    // Check required fields
    function checkRequired(inputArr) {
        let isAllValid = true;
        inputArr.forEach(function(input) {
            if (!input || input.value.trim() === '') {
                showError(input, `${getFieldName(input)} is required`);
                isAllValid = false;
            } else {
                showSuccess(input);
            }
        });
        return isAllValid;
    }

    // Get field name for error messages
    function getFieldName(input) {
        if (!input || !input.id) return 'Field';
        return input.id.charAt(0).toUpperCase() + input.id.slice(1).replace(/([A-Z])/g, ' $1');
    }

    // Check input length
    function checkLength(input, min, max) {
        if (!input) return false;
        if (input.value.length < min) {
            showError(input, `${getFieldName(input)} must be at least ${min} characters`);
            return false;
        } else if (input.value.length > max) {
            showError(input, `${getFieldName(input)} must be less than ${max} characters`);
            return false;
        } else {
            showSuccess(input);
            return true;
        }
    }

    // Check passwords match
    function checkPasswordsMatch(input1, input2) {
        if (!input1 || !input2) return false;
        if (input1.value !== input2.value) {
            showError(input2, 'Passwords do not match');
            return false;
        } else {
            showSuccess(input2);
            return true;
        }
    }

    // Check password strength
    function checkPasswordStrength(input) {
        if (!input) return false;
        // At least 8 characters, one uppercase, one lowercase, one number
        const strongRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})/;

        if (!strongRegex.test(input.value)) {
            showError(input, 'Password must be at least 8 characters with uppercase, lowercase, and number');
            return false;
        } else {
            showSuccess(input);
            return true;
        }
    }

    // Validate tea card format (if supplier is selected)
    function validateTeaCard(input) {
        if (!input) return false;
        const teaCardRegex = /^TC-\d{6}$/;
        if (!teaCardRegex.test(input.value)) {
            showError(input, 'Tea Card must be in format TC-123456');
            return false;
        } else {
            showSuccess(input);
            return true;
        }
    }

    // Check if terms are agreed to
    function checkTerms(checkbox) {
        if (!checkbox) return false;
        if (!checkbox.checked) {
            const termsControl = checkbox.parentElement;
            termsControl.classList.add('error');

            let errorElement = termsControl.querySelector('.error-message');
            if (!errorElement) {
                errorElement = document.createElement('small');
                errorElement.className = 'error-message text-red-500 block mt-1';
                termsControl.appendChild(errorElement);
            }

            errorElement.innerText = 'You must agree to the terms and conditions';
            return false;
        } else {
            const termsControl = checkbox.parentElement;
            termsControl.classList.remove('error');

            const errorElement = termsControl.querySelector('.error-message');
            if (errorElement) {
                errorElement.remove();
            }
            return true;
        }
    }

    // Real-time validation for inputs
    function setupRealTimeValidation() {
        // Email validation
        if (email) {
            email.addEventListener('blur', function() {
                if (email.value.trim() !== '') {
                    if (!isValidEmail(email.value)) {
                        showError(email, 'Email is not valid');
                    } else {
                        showSuccess(email);
                    }
                }
            });
        }

        // Password validation
        if (password) {
            password.addEventListener('input', function() {
                if (password.value.trim() !== '') {
                    checkPasswordStrength(password);

                    // Check if confirm password matches when both have values
                    if (confirmPassword && confirmPassword.value.trim() !== '') {
                        checkPasswordsMatch(password, confirmPassword);
                    }
                }
            });
        }

        // Confirm password validation
        if (confirmPassword) {
            confirmPassword.addEventListener('input', function() {
                if (confirmPassword.value.trim() !== '' && password && password.value.trim() !== '') {
                    checkPasswordsMatch(password, confirmPassword);
                }
            });
        }

        // Tea card validation (if supplier is selected)
        if (teaCardInput) {
            teaCardInput.addEventListener('blur', function() {
                const isSupplier = document.querySelector('input[name="role"]:checked')?.value === 'supplier';
                if (isSupplier && teaCardInput.value.trim() !== '') {
                    validateTeaCard(teaCardInput);
                }
            });
        }

        // Username validation
        if (username) {
            username.addEventListener('blur', function() {
                if (username.value.trim() !== '') {
                    checkLength(username, 3, 20);
                }
            });
        }

        // Name validation
        if (firstName) {
            firstName.addEventListener('blur', function() {
                if (firstName.value.trim() !== '') {
                    checkLength(firstName, 2, 30);
                }
            });
        }

        if (lastName) {
            lastName.addEventListener('blur', function() {
                if (lastName.value.trim() !== '') {
                    checkLength(lastName, 2, 30);
                }
            });
        }
    }

    // Update tea card visibility based on role selection
    const updateTeaCardVisibility = () => {
        const selected = document.querySelector('input[name="role"]:checked');
        if (!selected || !teaCardField) return;
        teaCardField.style.display = selected.value === 'supplier' ? 'block' : 'none';
    };

    if (roleRadios.length) {
        roleRadios.forEach(r => r.addEventListener('change', updateTeaCardVisibility));
        updateTeaCardVisibility();
    }

    // Form submission handler
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Validate all fields
        const isRequiredValid = checkRequired([firstName, lastName, email, username, address, password, confirmPassword]);

        // Check email format
        let isEmailValid = false;
        if (email && email.value.trim() !== '') {
            isEmailValid = isValidEmail(email.value);
            if (!isEmailValid) {
                showError(email, 'Email is not valid');
            } else {
                showSuccess(email);
            }
        }

        // Check password strength
        let isPasswordStrong = false;
        if (password && password.value.trim() !== '') {
            isPasswordStrong = checkPasswordStrength(password);
        }

        // Check passwords match
        let isPasswordMatch = false;
        if (password && password.value.trim() !== '' && confirmPassword && confirmPassword.value.trim() !== '') {
            isPasswordMatch = checkPasswordsMatch(password, confirmPassword);
        }

        // Check username length
        let isUsernameValid = false;
        if (username && username.value.trim() !== '') {
            isUsernameValid = checkLength(username, 3, 20);
        }

        // Check name lengths
        let isFirstNameValid = false;
        if (firstName && firstName.value.trim() !== '') {
            isFirstNameValid = checkLength(firstName, 2, 30);
        }

        let isLastNameValid = false;
        if (lastName && lastName.value.trim() !== '') {
            isLastNameValid = checkLength(lastName, 2, 30);
        }

        // Check tea card if supplier is selected
        const isSupplier = document.querySelector('input[name="role"]:checked')?.value === 'supplier';
        let isTeaCardValid = true;

        if (isSupplier && teaCardInput) {
            if (teaCardInput.value.trim() === '') {
                showError(teaCardInput, 'Tea Card is required for suppliers');
                isTeaCardValid = false;
            } else {
                isTeaCardValid = validateTeaCard(teaCardInput);
            }
        }

        // Check terms agreement
        const isTermsAgreed = checkTerms(termsCheckbox);

        // If validation fails, stop form submission
        if (!(isRequiredValid && isEmailValid && isPasswordStrong && isPasswordMatch &&
            isUsernameValid && isFirstNameValid && isLastNameValid && isTeaCardValid && isTermsAgreed)) {
            return;
        }

        // Proceed with form submission if validation passes
        const selectedRoleEl = document.querySelector('input[name="role"]:checked');
        const role = selectedRoleEl ? selectedRoleEl.value : 'customer';

        const firstNameValue = firstName ? firstName.value.trim() : '';
        const lastNameValue = lastName ? lastName.value.trim() : '';
        const emailValue = email ? email.value.trim() : null;
        const addressValue = address ? address.value.trim() : null;
        const usernameValue = username ? username.value.trim() : '';
        const passwordValue = password ? password.value : '';
        const confirmPasswordValue = confirmPassword ? confirmPassword.value : '';
        const teaCardNumber = teaCardInput ? teaCardInput.value.trim() : null;

        if (!usernameValue || !passwordValue) {
            alert('Username and password are required.');
            return;
        }
        if (passwordValue !== confirmPasswordValue) {
            alert('Passwords do not match.');
            return;
        }
        if (role === 'supplier' && !teaCardNumber) {
            alert('Tea card number is required for suppliers.');
            return;
        }

        const payload = {
            username: usernameValue,
            password: passwordValue,
            firstName: firstNameValue,
            lastName: lastNameValue,
            email: emailValue,
            address: addressValue
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

    // Initialize real-time validation
    setupRealTimeValidation();
});