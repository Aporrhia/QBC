document.addEventListener("DOMContentLoaded", function() {
    const movingLetter = document.getElementById('movingLetter');
    const wordContainer = document.getElementById('wordContainer');

    // Move the letter to the left after the page has loaded
    setTimeout(() => {
        movingLetter.style.transform = 'translateX(-100%)';
    }, 500);
});