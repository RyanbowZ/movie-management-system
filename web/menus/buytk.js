console.clear();
var productQuantArray=[]
function clickBox(){
    let checkDomArr = document.querySelectorAll('input[type=number]');
    let checkValues=[]
    for(let i=0;i<checkDomArr.length;i++){
        var checkobj={}
        checkobj.id=checkDomArr[i].parentNode.parentNode.children[0].innerHTML
        checkobj.name=checkDomArr[i].parentNode.parentNode.children[1].innerHTML
        checkobj.cate=checkDomArr[i].parentNode.parentNode.children[2].innerHTML
        checkobj.rate=checkDomArr[i].parentNode.parentNode.children[3].innerHTML
        checkobj.price=checkDomArr[i].parentNode.parentNode.children[4].innerHTML
        checkobj.num=checkDomArr[i].value;
        checkValues.push(checkobj);
    }
    localStorage.setItem("checkValues",JSON.stringify(checkValues));
    console.log(checkValues);
    let sumall=document.querySelector('[id="sumall"]');
    var order=parseInt(Date.now()/1000);
    for(let i=0;i<productQuantArray.length;i++){
        var proi=productQuantArray[i];
        var url="http://localhost:7777/product/update?productID="+proi.productID+"&productName="+
        proi.name+"&productPrice="+proi.price+"&productQuantity="+parseInt(proi.quantity-checkValues[i].num)+"&categoryID="+proi.categoryID;
        console.log(url)
        fetch(url, {
        method: 'GET',
        credentials: 'include'  // Ensure cookies are sent with the request
    })
        .then(response => {
        console.log(response);
            if (!response.ok) {
                throw new Error('failed');
            }
            else{

                console.log("product id "+proi.productID+" save successfully!");

            }
            return response;
        }) // Assuming the server returns a JSON response
        .catch(error => {console.error('Error:', error);
        });
    }
    var url="http://localhost:7777/order/save?orderID="+order+"&date=232&totalCost="+sumall.innerHTML
    +"&buyerID=323";
    console.log("url"+url)
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

                alert('Order Successfully! Redirecting to payment page...');
                localStorage.setItem("sumPrice", sumall.innerHTML);
                localStorage.setItem("orderID",order);
                window.location.href = '../Payment/checkout.html';

            }
            return response;
        }) // Assuming the server returns a JSON response
        .catch(error => {console.error('Error:', error);
        alert('RFailed2');
        });
}

function changeVal(el){
    // console.log(productQuantArray)
    let quantDom=document.querySelectorAll('[id="quantity"]');
    for(let i=0;i<quantDom.length;i++){
        if(quantDom[i].value>productQuantArray[i].quantity){
            alert("Too many tickets! Availiability is not enough!")
            quantDom[i].value=productQuantArray[i].quantity;
        }
    }

    var e=el.parentNode.parentNode;
    if(el.value==0)e.children[7].innerHTML=0;
    else{
    e.children[7].innerHTML=parseFloat(el.value)*parseFloat(e.children[4].innerHTML);
    }
    // var sumall=document.querySelectorAll('td[tagName="sumallprice"]');
    // let priceDom=document.querySelectorAll('td[tagName="price"]');
    //let sumall=document.getElementsByTagName("sumallprice");
    let sumall=document.querySelector('[id="sumall"]');
    let priceDom=document.querySelectorAll('[id="price2"]');
    
    var allprice=0;
    for(let i=0;i<priceDom.length;i++){
        allprice+=parseFloat(priceDom[i].innerHTML);
    }
    
    sumall.innerHTML=allprice;
}

document.getElementById('refreshBtn').addEventListener('click', function(event) {
	event.preventDefault();  // Prevent the default form submission behavior

	var url = 'http://localhost:7777/product/loadList';
    var tbody = document.querySelector('tbody');
	fetch(url, {
		method: 'GET',
		credentials: 'include'  // Ensure cookies are sent with the request
	})
		.then(response => {
//		console.log(response);
//        console.log(response.body);
        var text=response.text()
//        console.log(text);
			return text;
		})
        
        .then(data=>{
//            console.log(data);
            const dataArray = data.split("+");
            //console.log(dataArray);
            
            
            for(let i=0;i<dataArray.length;i+=4){
            if(dataArray[i]=="")continue;
            fetch('http://localhost:7777/product/load?productID='+dataArray[i], {
                method: 'GET',
                credentials: 'include'  // Ensure cookies are sent with the request
            }).then(response => {
                console.log(response);
                    if (!response.ok) {
                        throw new Error('get product failed');
                    }
                    return response.json();
                }) // Assuming the server returns a JSON response
                .then(data => {
                    console.log(data);
                    productQuantArray.push(data);
                    var tr = document.createElement('tr');
                    tbody.appendChild(tr);
                    for(let j=i;j<i+4;j++){
                    // 创建td元素
                    var td = document.createElement('td');
                    // 将每个对象中的属性值传给td
                    td.innerHTML = dataArray[j];
                    //给tr添加td子元素
                    tr.appendChild(td);
                    }
                   
                    var td = document.createElement('td');
                    td.innerHTML = data.price;
                    tr.appendChild(td);
                    var td = document.createElement('td');
                    td.innerHTML = data.quantity;
                    
                    tr.appendChild(td);
                    var td = document.createElement('td');
                    td.innerHTML="<input type='number' id='quantity' name='quantity' placeholder='Quantity' oninput='changeVal(this)'>";
                    tr.appendChild(td);
                    var td = document.createElement('td');
                    td.id="price2";
                    td.innerHTML=0;
                    tr.appendChild(td);
                    
                })
                .then(d=>{
                    console.log("i="+i);
                    console.log(dataArray.length);
                    if(i+4==dataArray.length-1){
                    var tr = document.createElement('tr');
                    tbody.appendChild(tr);
                    for(let i=0;i<7;i++){
                        var td = document.createElement('td'); 
                        td.innerHTML=""; 
                        tr.appendChild(td);
                    }
                    var td = document.createElement('td');  
                    td.tagName="sumallprice";
                    td.id="sumall";
                    td.innerHTML=0;
                        tr.appendChild(td);
                        return d;
                }
                })
                .catch(error => {console.error('Error:', error);});

            }
            
            return data;
        })
        
		.catch(error => {console.error('Error:', error);
		alert('Login failed: Invalid username or password.');
		});
});

document.getElementById('addCartBtn').addEventListener('click', function(event) {
	event.preventDefault();  // Prevent the default form submission behavior
	clickBox();
});



