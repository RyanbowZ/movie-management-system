import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import com.google.gson.Gson;

public class DataServer {

    private static String currentUsername="admin";
    private static boolean web=false;
    public static void main(String[] args) throws Exception {


        System.out.println("Waiting for connection at port 7777.....");
        ServerSocket serverSocket = new ServerSocket(7777);
        Socket clientSocket = serverSocket.accept();


        OutputStream outSocket = clientSocket.getOutputStream();

        PrintWriter out = new PrintWriter(outSocket, true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


        Gson gson = new Gson();


        System.out.println("Connection successful");
        System.out.println("Waiting for input.....");



        while (true) {
            if(web){
                clientSocket = serverSocket.accept();
                outSocket = clientSocket.getOutputStream();
                out = new PrintWriter(outSocket, true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            }
            String inputLine = in.readLine();

            if (inputLine == null) {
                if(web) {
                    in.close();
                    out.close();
                    outSocket.close();
                    clientSocket.close();
                    continue;
                }
                else break;

            }

            System.out.println("inputLine: "+inputLine+"\nweb= "+web);
            if(inputLine.contains("http")||inputLine.contains("HTTP")) {
                web=true;
                String[] requestParts = inputLine.split(" ");
                String httpMethod = requestParts[0];
                inputLine = requestParts[1];
                System.out.println("httpmethod: " + httpMethod);
                Map<String, String> headers = new HashMap<>();
                String headerLine;
                while (!(headerLine = in.readLine()).isEmpty()) {
                    String[] headerParts = headerLine.split(": ", 2);
                    headers.put(headerParts[0], headerParts[1]);
                }
            }
            else web=false;
            System.out.println("web=="+web);

            String apiContent = inputLine.substring(inputLine.indexOf('/') + 1);

//            System.out.println("apicontent: "+apiContent);
//            if(inputLine.indexOf(' ')>0)
//            inputLine = inputLine.substring(0, inputLine.indexOf(' '));
            //System.out.println(inputLine);
            int idx = apiContent.indexOf("?");
            String method;
            String[] details = new String[2];
            if(idx >=0) {
                method = apiContent.substring(0, idx);
                details = apiContent.substring(idx+1).split("&");

            } else {
                method = apiContent;
            }

            //if (input.length>1&&input[1].toLowerCase().equals("quit")) break;
            System.out.println("method: "+method);
            if(method.equals("login")) {
                String username = details[0].substring(details[0].indexOf("=")+1);
                String password = details[1].substring(details[1].indexOf("=")+1);
                System.out.println("Client asks for Login username: " + username);
                System.out.println("Client asks for password: " + password);
                User user = Application.getInstance().getDataAdapter().loadUser(username, password);

//                if (user == null) {
//                    System.out.println("Invalid User");         // No user with that id
//                } else {
//                    System.out.println("Success Log in!"+user.getUserID() + "  " + user.getUsername()
//                            + "  " + user.getPassword() + "  " + user.getFullName() + "  " + user.getUsertype());
//                }
//                if (user != null) {
//                    String sessionId = UUID.randomUUID().toString();
//                    sessions.put(sessionId, user);
//                    String userData = gson.toJson(user);
//                    sendResponse(clientSocket, userData, "application/json", sessionId);
//                } else {
//                    sendResponse(clientSocket, "Invalid username or password.", "text/plain");
//                }
                if(user!=null){
//                    String sessionId = UUID.randomUUID().toString();
//                    sessions.put(sessionId, user);
                    currentUsername=user.getUsername();
                    String userData = gson.toJson(user);
                    if(web) {
                        outSocket.write(generateResponse(userData, "application/json").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    else {
                        out.println(user.getUserID() + "  " + user.getUsername()
                                + "  " + user.getPassword() + "  " + user.getFullName() + "  " + user.getUsertype());
                    }

                }
                else{
                    if(web) {
                        outSocket.write(generateResponse("Invalid username or password.", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    else
                    out.println("Invalid User");
                }

            }
            if(method.equals("getuser")) {

                System.out.println("current username: "+currentUsername);
                User user = Application.getInstance().getDataAdapter().loadUser(currentUsername);

                if(user!=null){
                    String userData = gson.toJson(user);
                    if(web) {
                        outSocket.write(generateResponse(userData, "application/json").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    else
                    out.println("Invalid User");
                }
                else{
                    if(web) {
                        outSocket.write(generateResponse("Invalid username.", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    out.println("Success Log in!"+user.getUserID() + "  " + user.getUsername()
                            + "  " + user.getPassword() + "  " + user.getFullName() + "  " + user.getUsertype());
                }

            }
            if(method.equals("sign")) {
                String username = details[0].substring(details[0].indexOf("=")+1);
                String password = details[1].substring(details[1].indexOf("=")+1);
                String displayname = details[2].substring(details[2].indexOf("=")+1);
                String usertype = details[3].substring(details[3].indexOf("=")+1);
                System.out.println("Client asks for Sign Up: " + username);
                if (Application.getInstance().getDataAdapter().saveUser(username, password, displayname, Integer.parseInt(usertype))) {
                    if(web) {
                        outSocket.write(generateResponse("Sign up Success", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    else out.println("Sign up Success");
                }
                else{
                    if(web) {
                        outSocket.write(generateResponse("Sign up Fail", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    else out.println("Sign up Fail");
                }
            }
            if(method.equals("product/loadList")) {
                System.out.println("Client asks for Load Movie List");
                List<Object[]> list = Application.getInstance().getDataAdapter().loadAllMovieList();
                StringBuilder sb= new StringBuilder();
                for(Object[] row : list) {
                    for (Object o : row) {
                        sb.append(o).append("+");
                    }
                }
                System.out.println("============movie list: "+sb.toString());
                if(web) {
                    outSocket.write(generateResponse(sb.toString(), "text/plain").getBytes("UTF-8"));
                    outSocket.flush();
                }
                else out.println(sb);
            }
            if(method.equals("product/update")) {
                Product product = new Product();
                String productID = details[0].substring(details[0].indexOf("=")+1);
                String name = URLDecoder.decode(details[1].substring(details[1].indexOf("=")+1),"UTF-8");
                String price = details[2].substring(details[2].indexOf("=")+1);
                String quantity = details[3].substring(details[3].indexOf("=")+1);
                String category = details[4].substring(details[4].indexOf("=")+1);
                product.setProductID(Integer.parseInt(productID));
                product.setName(name);
                product.setPrice(Double.parseDouble(price));
                product.setQuantity(Double.parseDouble(quantity));
                product.setCategoryID(Integer.parseInt(category));
                System.out.println("Client asks for update Product. ID = " + productID);
                if(Application.getInstance().getDataAdapter().saveProduct(product)){
                    if(web) {
                        outSocket.write(generateResponse("Update Product Success.", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    out.println("Update Success");
                }
                else{
                    if(web) {
                        outSocket.write(generateResponse("Update Product Failed.", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    out.println("Update Failed");
                }
            }

            if(method.equals("product/load")) {
                String productID = details[0].substring(details[0].indexOf("=")+1);
                System.out.println("Client asks for load Product. ID = " + productID);
                int id = Integer.parseInt(productID);
                Product product = Application.getInstance().getDataAdapter().loadProduct(id);

                if (product == null) {
                    if(web) {
                        outSocket.write(generateResponse("Invalid product.", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    out.println("Invalid Product");         // No product with that id
                }
                else {
                    String userData = gson.toJson(product);
                    if(web) {
                        outSocket.write(generateResponse(userData, "application/json").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    out.println(product.getName()
                            + "  " + product.getPrice() + "  " + product.getQuantity() + "  "
                            + product.getProductID() + "  " + product.getCategoryID());
                }
            }

            if(method.equals("order/save")) {
                System.out.println("Client asks for Save Order.");
                String orderID = details[0].substring(details[0].indexOf("=")+1);
//                String date = details[1].substring(details[1].indexOf("=")+1);
//
                String totalCost = details[2].substring(details[2].indexOf("=")+1);
//                String buyerID = details[3].substring(details[3].indexOf("=")+1);
                Order order = new Order();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();


                order.setOrderID(Integer.parseInt(orderID));
                order.setBuyerID(Application.getInstance().getDataAdapter().loadUser(currentUsername).getUserID());
                order.setDate(dtf.format(now));
                order.setTotalCost(Double.parseDouble(totalCost));

                if(Application.getInstance().getDataAdapter().saveOrder(order)) {
                    if(web) {
                        outSocket.write(generateResponse("Save order Success.", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    out.println("Save order Success.");
                }
                else{
                    if(web) {
                        outSocket.write(generateResponse("Invalid order.", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    out.println("Invalid order.");
                }
            }

            if(method.equals("payment/save")) {
                System.out.println("Client asks for Save Payment.");
                String address = details[0].substring(details[0].indexOf("=")+1);
                String creditCard = details[1].substring(details[1].indexOf("=")+1);
                String orderID = details[2].substring(details[2].indexOf("=")+1);
                Payment payment = new Payment();
                payment.setAddress(address);
                payment.setCreditcard(creditCard);
                payment.setOrderID(Integer.parseInt(orderID));
                if(Application.getInstance().getDataAdapter().savePayment(payment)){
                    if(web) {
                        outSocket.write(generateResponse("Save payment Success.", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    out.println("Save payment Success.");
                }
                else{
                    if(web) {
                        outSocket.write(generateResponse("Invalid payment.", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    out.println("Invalid order.");
                }
            }
            if(method.equals("receipt/save")) {
                System.out.println("Client asks for Save Receipt.");
                int id = Integer.parseInt(details[0].substring(details[0].indexOf("=")+1));
                String orderDetail = URLDecoder.decode(details[1].substring(details[1].indexOf("=")+1),"UTF-8");
//                System.out.println("!!!!!!!!--=details[1]:=========\n"+details[1]);
//                System.out.println("!!!!!=orderdetail:=========\n"+orderDetail);
//                System.out.println("!!!!!=orderdetaildecode:=========\n"+URLDecoder.decode(orderDetail,"UTF-8"));
                if(Application.getInstance().getDataAdapter().saveReceipt(id, orderDetail)) {
                    outSocket.write(generateResponse("Save Receipt Success.", "text/plain").getBytes("UTF-8"));
                    outSocket.flush();
                    out.println("Save Receipt Success.");
                }

            }
            if(method.equals("review/save")) {
                System.out.println("Client asks for Save review.");

                int id = Integer.parseInt(details[0].substring(details[0].indexOf("=")+1));
                int rate = Integer.parseInt(details[1].substring(details[1].indexOf("=")+1));
                String reviews = URLDecoder.decode(details[2].substring(details[2].indexOf("=")+1),"UTF-8");
                int userID = Integer.parseInt(details[3].substring(details[3].indexOf("=")+1));
                Rate review = new Rate();
                review.setRate(rate);
                review.setReview(reviews);
                review.setMovieID(id);
                review.setUserID(userID);
                review.setTime((int)new Date().getTime()/1000);
                if(Application.getInstance().getDataAdapter().saveReview(review)){
                    if(web) {
                        outSocket.write(generateResponse("Save Review Success", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    out.println("Save Review Success.");
                }
            }
            if(method.equals("recommend")) {
                System.out.println("Client asks for recommend movie.");
                String movieType =details[0].substring(details[0].indexOf("=")+1);
                Movie movie = Application.getInstance().getDataAdapter().recomMovie(movieType);
                if (movie == null) {
                    if(web) {
                        outSocket.write(generateResponse("No Movie found with this type", "text/plain").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    else
                        out.println("No Movie found with this type");         // No product with that id
                }
                else{
                    if(web){
                        outSocket.write(generateResponse(gson.toJson(movie), "application/json").getBytes("UTF-8"));
                        outSocket.flush();
                    }
                    else {
                        out.println(movie.getMovieID()
                                + "+" + movie.getMovieName() + "+" + movie.getRate() + "+"
                                + movie.getReview());
                    }
                }

            }
            if(web){
                out.close();
                outSocket.close();
                in.close();
                clientSocket.close();
            }

        }
        out.close();
        outSocket.close();
        in.close();
        clientSocket.close();
        serverSocket.close();


    }

        private static String generateResponse(String responseBody, String contentType) throws Exception{
        StringBuilder responseHeaders = new StringBuilder();
        responseHeaders.append("HTTP/1.1 200 OK\r\n");
        responseHeaders.append("Content-Type: ").append(contentType).append("\r\n");
        responseHeaders.append("Access-Control-Allow-Origin: http://localhost:63342\r\n");
        responseHeaders.append("Access-Control-Allow-Credentials: true\r\n");
        responseHeaders.append("Content-Length: ").append(responseBody.getBytes("UTF-8").length).append("\r\n");

        if (currentUsername!=null) {
            responseHeaders.append("Set-Cookie: sessionId=").append(currentUsername).append("; HttpOnly; SameSite=Strict\r\n");
        }
        return responseHeaders + "\r\n" + responseBody;
    }
}
