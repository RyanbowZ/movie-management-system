const checkOutBtn = document.getElementById('checkoutBtn');
var allprice=localStorage.getItem("sumPrice");
var user=JSON.parse(localStorage.getItem("currentuserJson"));
console.log(user)
var orderid=localStorage.getItem("orderID");
document.querySelector('[id="userid"]').innerHTML=user.userID;
document.querySelector('[id="username"]').innerHTML=user.username;
document.querySelector('[id="orderid"]').innerHTML=orderid;
document.querySelector('[id="subtotal-price"]').innerHTML=
document.querySelector('[id="subtotal-price"]').innerHTML="$"+allprice;
var totalprice=(parseFloat(allprice)*1.08).toFixed(2);
document.querySelector('[id="total-price"]').innerHTML="$"+totalprice;
document.querySelector('[id="check-amt"]').innerHTML="$"+totalprice;


// var cnum2=document.getElementById("cnum")
// console.log(cnum);
// console.log(cnum2);
checkOutBtn.addEventListener('click', function(event) {
	event.preventDefault();
    var cnum=document.querySelector('input[id="cnum"]').value
    var ad=document.querySelector('input[id="street"]').value
    var url = 'http://localhost:7777/payment/save?address='+ad+'&creditCard='+cnum+'&orderID='+orderid;
    console.log(url);
    fetch(url, {
        method: 'GET',
        credentials: 'include'  // Ensure cookies are sent with the request
    })
        .then(response => {
        console.log(response);
            if (!response.ok) {
                alert("Failed1!");
                throw new Error('failed');
            }
            else{
                var receiptContent="====================Receipt=====================\n"+
                "Customer ID: "+user.userID+"\nCustomer Name: "+user.username+
                "\nCredit Card Number (last 4 digits):"+cnum.substring(cnum.length-4)+
                "\nShipping address: "+ad+
                "\nOrder ID: "+orderid+
                "\nOrder Cost: "+allprice+
                "\n------Purchase Details-----\n"+localStorage.getItem("checkValues");
                console.log(receiptContent)
                alert(receiptContent);
                alert('Payment Successfully! Redirecting to index page...');
                window.location.href = '../homepage/index.html';
            }
            return response;
        }) // Assuming the server returns a JSON response
        .catch(error => {console.error('Error:', error);
        alert('RFailed2');
        });
});