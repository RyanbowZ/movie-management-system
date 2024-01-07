var movieid = document.querySelector('.input[type="movieid"]')
var cateid = document.querySelector('.input[type="cateid"]')
var moviename = document.querySelector('.input[type="moviename"]')
var price = document.querySelector('.input[type="price"]')
var quantity = document.querySelector('.input[type="quantity"]')
document.getElementById('loadBtn').addEventListener('click', function(event) {
	event.preventDefault();  // Prevent the default form submission behavior

	
	if (!movieid.value.trim() ) {
		alert('MovieID are required.');
		return;
	}

	var url = 'http://localhost:7777/product/load?productID=' + movieid.value;
    console.log(url)
	fetch(url, {
		method: 'GET',
		credentials: 'include'  // Ensure cookies are sent with the request
	})
		.then(response => {
		console.log(response);
			if (!response.ok) {
				throw new Error('Login failed');
			}
			return response.json();
		}) // Assuming the server returns a JSON response
		.then(data => {
		    console.log(data);
			if (data ) {  
				cateid.value=data.categoryID;
                moviename.value=data.name;
                price.value=data.price;
                quantity.value=data.quantity;
				
			} 
		})
		.catch(error => {console.error('Error:', error);
		alert('failed: Invalid .');
		});
});

document.getElementById('saveBtn').addEventListener('click', function(event) {
	event.preventDefault();  // Prevent the default form submission behavior

	
	if (!movieid.value.trim() ) {
		alert('MovieID are required.');
		return;
	}

	var url = 'http://localhost:7777/product/update?productID=' + movieid.value+'&productName='+moviename.value+'&productPrice='+price.value+
    '&productQuantity='+quantity.value+'&categoryID='+cateid.value;
    console.log(url)
	fetch(url, {
		method: 'GET',
		credentials: 'include'  // Ensure cookies are sent with the request
	})
		.then(response => {
		console.log(response);
			if (!response.ok) {
				throw new Error('Login failed');
			}
			return response;
		}) // Assuming the server returns a JSON response
		.catch(error => {console.error('Error:', error);
		alert('failed: Invalid .');
		});
});

