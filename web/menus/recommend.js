console.clear();

document.getElementById('submitBtn').addEventListener('click', function(event) {
	event.preventDefault(); // 防止表单默认提交行为

	var movietype = document.querySelector('.input[type="movietype"]').value;


	if (!movietype.trim() ) {
		alert('All fields are required.');
		return;
	}
	var url = 'http://localhost:7777/recommend?movieType=' + movietype;

    	fetch(url, {
    		method: 'GET',
    		credentials: 'include'  // Ensure cookies are sent with the request
    	})
    		.then(response => {
    		console.log(response);
    			if (!response.ok) {
    				throw new Error('failed');
    			}
    			return response.json();
    		}) // Assuming the server returns a JSON response
    		.then(data => {
    		    console.log(data);
    			if (data) {  // Assuming if the user exists, the server would return an object with a userID field
    				// Display the user's full name (you can adjust this as needed)
    				alert('The best movie in the chosen category is ' + data.movieName);
    				document.getElementById("movieid").innerHTML=data.movieID
    				document.getElementById("moviename").innerHTML=data.movieName
    				document.getElementById("rate").innerHTML=data.rate
    				document.getElementById("review").innerHTML=data.review

    			} else {
    				// If login failed, show an error message
    				alert('ailed');
    			}
    		})
    		.catch(error => {console.error('Error:', error);
    		alert('Login failed: Invalid username or password.');
    		});


});