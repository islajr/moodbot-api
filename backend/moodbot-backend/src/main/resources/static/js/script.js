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
            alert('Signup successful! Please log in.');
            // Optionally redirect to login page
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
            window.location.href = '/chatpage';
        } else {
            alert(data.message);
        }
    })
    .catch(error => console.error('Error:', error));
});

