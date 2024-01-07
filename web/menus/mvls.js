console.clear();

function clickBox(){
    let checkDomArr = document.querySelectorAll('tbody input[type=checkbox]:checked');
    let checkValues=[]
    for(let i=0;i<checkDomArr.length;i++){
        var checkobj={}
        checkobj.id=checkDomArr[i].parentNode.parentNode.children[0].innerHTML
        checkobj.name=checkDomArr[i].parentNode.parentNode.children[1].innerHTML
        checkobj.cate=checkDomArr[i].parentNode.parentNode.children[2].innerHTML
        checkobj.rate=checkDomArr[i].parentNode.parentNode.children[3].innerHTML
        checkValues.push(checkobj);
    }
    console.log(checkValues);
}

document.getElementById('refreshBtn').addEventListener('click', function(event) {
	event.preventDefault();  // Prevent the default form submission behavior

	var url = 'http://localhost:7777/product/loadList';

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
            console.log(dataArray);
            var tbody = document.querySelector('tbody');
            for(let i=0;i<dataArray.length;i+=4){
            if(dataArray[i]=="")continue;
                //创建行tr
                var tr = document.createElement('tr');
                
                //将新创建的行tr添加给tbody
                tbody.appendChild(tr);
                for(let j=i;j<i+4;j++){
                // 创建td元素
                var td = document.createElement('td');
                // 将每个对象中的属性值传给td
                td.innerHTML = dataArray[j];
                //给tr添加td子元素
                tr.appendChild(td);
                }

                // td.appendChild(input);
                // var input=document.createElement('input');
                // input.type="checkbox";
                // input.value=i/4;
                // input.oninput="clickBox()";

                
                

            }
            return data;
        })
		.catch(error => {console.error('Error:', error);
		alert('Login failed: Invalid username or password.');
		});
});



