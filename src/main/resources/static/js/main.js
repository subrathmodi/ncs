document.getElementById('loginForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const usernameVal = document.getElementById('username').value;
    const passwordVal = document.getElementById('password').value;

    const errorBox = document.getElementById('errorBox');
    const successBox = document.getElementById('successBox');

    // Clear alerts on a new attempt
    errorBox.style.display = 'none';
    errorBox.textContent = '';
    successBox.style.display = 'none';
    successBox.textContent = '';

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: usernameVal, password: passwordVal })
        });

        const data = await response.json();

        if (response.ok) {
            // ==========================================================
            // EXTRACT & DROP THE JWT TOKEN INTO THE BROWSER COOKIE LEDGER
            // ==========================================================
            // if (data.token) {
            //     // Save token in localStorage for your regular API authorization headers
            //     localStorage.setItem("authToken", data.token);
            //
            //     // Save token in Cookie so target="_blank" tabs read it automatically
            //     document.cookie = `JWT_TOKEN=${data.token}; path=/; max-age=${24 * 60 * 60}; SameSite=Strict;`;
            // }
            // ==========================================================

            // Show verification success state smoothly right before bouncing
            successBox.textContent = "Access Granted! Redirecting...";
            successBox.style.display = 'block';

            setTimeout(() => {
                window.location.href = "/dashboard";
            }, 800);
        } else {
            // Drop back structural message mapping
            errorBox.textContent = data.error || 'Authentication rejected.';
            errorBox.style.display = 'block';
        }
    } catch (err) {
        errorBox.textContent = 'Server communication error. Please try again.';
        errorBox.style.display = 'block';
    }
});