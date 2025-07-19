const loginForm = document.getElementById('login-form');
const signupForm = document.getElementById('sign-up-form');


// signup form submission handler
signupForm.addEventListener('submit', function(event) {
    event.preventDefault();
    const username = document.getElementById('signup-username').value;
    const email = document.getElementById('signup-email').value;
    const password = document.getElementById('signup-password').value;

    fetch('/api/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ 
            "username": username, 
            "email": email, 
            "password": password 
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Signup successful!');
            localStorage.setItem('accessToken', data.accessToken);
            localStorage.setItem('refreshToken', data.refreshToken);
            window.location.href = '/chatpage';
        } else {
            alert(data.message);
        }
    })
    .catch(error => console.error('Error:', error));
});

// login form submission handler
loginForm.addEventListener('submit', function(event) {
    event.preventDefault();
    const identifier = document.getElementById('login-identifier').value;
    const password = document.getElementById('login-password').value;

    fetch('/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ 
            "identifier": identifier, 
            "password": password 
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert("login successful!");
            localStorage.setItem('accessToken', data.accessToken);
            localStorage.setItem('refreshToken', data.refreshToken);
            window.location.href = '/chatpage';
        } else {
            alert(data.message);
        }
    })
    .catch(error => console.error('Error:', error));
});

/* accessibility logic */
const otpContainer = document.getElementById('otp-container');
const otpInput = document.querySelectorAll('.otp-input');

otpInput.forEach((input, index) => {
    input.addEventListener('input', (e) => {
        if (e.target.value >=0 && e.target.value <= 9) {

            if (input.value.length === 1 && index < otpInput.length - 1) {
                otpInput[index + 1].focus();
            }
        } else {
            input.value = '';
        }
    })

    input.addEventListener('keydown', (e) => {
            if (e.key === 'Backspace' && index > 0 && input.value === '') {
                otpInput[index - 1].focus();
            }
        });
})

