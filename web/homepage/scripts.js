// Mocked database result, this would be your actual data from the database.
// const eventsFromDatabase = [
//     {
//         num: '23',
//         day: 'Feb',
//         eventType: 'Music Event',
//         title: 'Concert 1',
//         dateDetails: 'Monday 15th 2016 <br/> 15:20Pm & 11:00Am',
//         locationDetails: 'Shanghai',
//         buttonLabel: 'View Tickets' //
//     },
//     // ... other events
// ];

var user=JSON.parse(localStorage.getItem("currentuserJson"));
document.getElementById('username').innerHTML=user.username;
console.log(user)
if(user.usertype==0){
document.getElementById('button-list').innerHTML=`
<button class="btn-block btn-blue" onclick="window.location.href='../menus/mvls.html'">

                                <span id="mvls">Movie List</span>

    </button>
    <button class="btn-block btn-blue" onclick="window.location.href='../menus/buytk.html'">

                                <span id="buytk">Buy Ticket</span>

    </button>
    <button class="btn-block btn-blue" onclick="window.location.href='../menus/rate.html'">

                                <span id="rate">Rate Movies</span>

    </button>
    <button class="btn-block btn-blue" onclick="window.location.href='../menus/recommend.html'">

        <span id="recom">Recommendation</span>

    </button>
`
}
else{
    document.getElementById('button-list1').innerHTML=`
<button class="btn-block btn-blue" onclick="window.location.href='../menus/managemovie.html'">

                                <span id="mvls">Manage Movie</span>

    </button>
    <button class="btn-block btn-blue" onclick="window.location.href='../menus/mvls.html'">

                                        <span id="mvls">Browse All Movie</span>

            </button>
    
`
}
// Function to create the event item HTML
function createEventItem(event) {
    return `
    <div class="item">
        <div class="item-right">
            <h2 class="num">${event.eventID}</h2>
    
            <span class="up-border"></span>
            <span class="down-border"></span>
        </div> 
        <div class="item-left">
            <p class="event">${event.eventDate}</p>
            <h2 class="title">${event.eventName}</h2>
            <div class="sce">
                <div class="icon">
                    <i class="fa fa-table"></i>
                </div>
                <p>${event.eventDescription}</p>
            </div>
            <div class="fix"></div>
            <div class="loc">
                <div class="icon">
                    <i class="fa fa-map-marker"></i>
                </div>
                <p>${event.eventLocation}</p>
            </div>
            <div class="fix"></div>
            <button class="tickets" id="select" data-eventid=${event.eventID} onclick="openTicketWindow(this)">Select Tickets</button>
        </div>
    </div>`;
}
function openTicketWindow(button) {
    var eventId = button.getAttribute('data-eventid');
    window.open(`../tickets/index.html?eventID=${eventId}`, 'popUpWindow', 'height=800,width=600,left=100,top=100,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no, status=yes');
}

// Append events to the DOM

document.addEventListener('DOMContentLoaded', function() {

    var currentDate = new Date();
    var hour = currentDate.getHours();
    var timePeriod;

    if (hour >= 0 && hour < 6) {
        timePeriod = "night";
    } else if (hour >= 6 && hour < 12) {
        timePeriod = "morning";
    } else if (hour >= 12 && hour < 18) {
        timePeriod = "afternoon";
    } else {
        timePeriod = "evening";
    }

    document.getElementById('time-period').textContent = timePeriod;

//    // Fetch user fullName from the server
//    fetchUserFullName();
//
//    // Fetch all events
//    fetchAllEvents();
//    const signOutButton = document.querySelector('.user-actions a[href="../login/login.html"]');
//    if (signOutButton) {
//        signOutButton.addEventListener('click', handleSignOut);
//    }


});
//
//function fetchUserFullName() {
//    fetch('http://localhost:8080/getUserName', {
//        method: 'GET',
//        credentials: 'include'
//    })
//        .then(response => {
//            // 检查响应的内容类型
//            const contentType = response.headers.get("content-type");
//            if (contentType && contentType.indexOf("application/json") !== -1) {
//                return response.json();
//            } else {
//                throw new TypeError("Oops, we didn't get JSON!");
//            }
//        })
//        .then(data => {
//            if (data && data.fullName) {
//                document.getElementById('user-fullname').textContent = data.fullName;
//            } else if (data && data.error) {
//                console.error(data.error);
//            }
//        })
//        .catch(error => console.error('Error:', error));
//}
//
//function fetchAllEvents() {
//
//    fetch('http://localhost:8080/allEvent/', {
//        method: 'GET',
//        credentials: 'include'
//    })
//        .then(response => {
//            if (!response.ok) {
//                throw new Error("Network response was not ok");
//            }
//            return response.json();
//        })
//        .then(events => {
//            const eventList = document.querySelector('.event-list');
//            events.forEach(event => {
//                eventList.innerHTML += createEventItem(event);
//            });
//        })
//        .catch(error => console.error('Error:', error));
//}
//
//function handleSignOut() {
//    // 向服务器发送请求来清除会话
//    fetch('http://localhost:8080/signout', {
//        method: 'GET',
//        credentials: 'include' // 确保包含凭证（如cookies）
//    })
//        .then(response => {
//            if (response.ok) {
//                // 如果服务器响应成功，清除本地存储的用户信息
//                // （例如，删除保存在 localStorage 或 sessionStorage 中的信息）
//                //localStorage.removeItem('userSession'); // 例如
//                // 重定向到登录页面或更新页面状态
//                alert('Signed out successfully.')
//                window.location.href = '../login/login.html'; // 假设有一个名为 'login.html' 的登录页面
//            } else {
//                throw new Error('Failed to sign out');
//            }
//        })
//        .catch(error => {
//            console.error('Error:', error);
//            alert('Error signing out');
//        });
//}

