function validatePassword() {
    var password = document.getElementById("password");
    var confirmPassword = document.getElementById("confirmPassword");

    if (password.value != confirmPassword.value) {
        confirmPassword.setCustomValidity("Passwords do not match");
    } else {
        confirmPassword.setCustomValidity("");
    }
}

function showWindow(windowId) {
    // Hide all windows
    document.getElementById('window1').style.display = 'none';
    document.getElementById('window2').style.display = 'none';

    // Show the requested window
    document.getElementById(windowId).style.display = 'block';
}
