console.clear();

document.getElementById('submitBtn').addEventListener('click', function(event) {
	event.preventDefault(); // 防止表单默认提交行为

	var movieid = document.querySelector('.input[id="movieid"]').value;
	var ratenum = document.querySelector('.input[id="ratenum"]').value;
	var review = document.querySelector('[id="review"]').value;

	if (!movieid.trim() || !ratenum.trim() || !review.trim()) {
		alert('All fields are required.');
		return;
	}
	var userID=-1;
    fetch("http://localhost:7777/getuser", {
        		method: 'GET',
        		credentials: 'include'  // Ensure cookies are sent with the request
        	})
        		.then(response => {
        		console.log(response);
        			if (!response.ok) {
        			    alert("Failed1!");
        				throw new Error('Registration failed');
        			}

        			return response.json();
        		}) // Assuming the server returns a JSON response
        		.then(data => {

                			if (data && data.fullName!=null) {  // Assuming if the user exists, the server would return an object with a userID field
                				// Display the user's full name (you can adjust this as needed)

                				console.log(data);
                				// If logged in successfully, redirect to ../homepage/index.html
                				userID=data.userID;
                					var url = "http://localhost:7777/review/save?movieID="+movieid+"&rate="+ratenum+"&review="+encodeURIComponent(review)
                                                                      +"&userID="+userID;
                                	fetch(url, {
                                    		method: 'GET',
                                    		credentials: 'include'  // Ensure cookies are sent with the request
                                    	})
                                    		.then(response => {
                                    		console.log(response);
                                    			if (!response.ok) {
                                    			    alert("Failed1!");
                                    				throw new Error('Registration failed');
                                    			}
                                    			else{
                                    			    alert('Review submitted successfully!');
                                    			    //window.location.href = '../login/login.html';

                                    			}
                                    			return response;
                                    		}) // Assuming the server returns a JSON response
                                    		.catch(error => {console.error('Error:', error);
                                    		alert('Failed2');
                                    		});

                			} else {
                				// If login failed, show an error message
                				alert('Login failed: Invalid username or password.');
                			}
                		})
        		.catch(error => {console.error('Error:', error);
        		alert('Failed2');
        		});


    });