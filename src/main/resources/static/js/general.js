document.addEventListener('DOMContentLoaded', function () {
    // Sample data from the database
    const dataFromDB = [
        { date: '22-01-2023', amount: 2000 },
        { date: '22-02-2023', amount: 3000 },
        { date: '22-03-2023', amount: 4000 },
        { date: '22-04-2023', amount: 2500 },
        { date: '22-05-2023', amount: 3500 },
        { date: '22-06-2023', amount: 2800 }
        // Add more data as needed
    ];

    // Define an array for month names
    const monthNames = ["January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"];

    // Parse the dates and amounts
    const months = [];
    const amounts = [];
    dataFromDB.forEach(entry => {
        const dateParts = entry.date.split('-');
        const monthIndex = parseInt(dateParts[1], 10) - 1; // Subtract 1 to get the correct index
        const year = parseInt(dateParts[2], 10);
        const monthName = monthNames[monthIndex];
        const monthYear = `${monthName} ${year}`; // Concatenate month name and year for label
        months.push(monthYear);
        amounts.push(entry.amount);
    });

    // Get the context of the canvas element we want to select
    const ctx = document.getElementById('monthlyGraph').getContext('2d');

    // Create the chart
    const monthlyChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: months,
            datasets: [{
                label: 'Amount transferred ($)',
                data: amounts,
                backgroundColor: 'rgba(54, 162, 235, 0.5)', // Light blue color for bars
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            },
            plugins: {
                tooltip: {
                    backgroundColor: '#333', // Tooltip background color
                    titleColor: '#000', // Tooltip title color
                    bodyColor: '#333' // Tooltip body color
                }
            },
            layout: {
                padding: {
                    left: 20,
                    right: 20,
                    top: 20,
                    bottom: 20
                }
            },
            elements: {
                bar: {
                    borderWidth: 2,
                    borderColor: 'rgba(54, 162, 235, 1)'
                }
            }
        }
    });
});
