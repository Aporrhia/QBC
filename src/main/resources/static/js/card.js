function validateCardNumber(input) {
    var value = input.value.replace(/[^0-9]/g, '');
    if (value.length !== 16) {
        input.setCustomValidity('Card number must be exactly 16 digits.');
    } else {
        input.setCustomValidity('');
    }
}