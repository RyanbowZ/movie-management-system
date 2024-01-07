import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import redis.clients.jedis.Jedis;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class DataAdapter {
    private Jedis jedis;
    private MongoDatabase database;

    public DataAdapter(Jedis jedis, MongoDatabase mongoDatabase) {
        this.jedis = jedis;
        this.database = mongoDatabase;
    }

    public Product loadProduct(int id) {
        try {
            System.out.println(id);
            String productId = jedis.hget("movie:" + id, "movieId");
            if (productId != null) {
                Product product = new Product();
                product.setProductID(Integer.parseInt(jedis.hget("movie:" + id, "movieId")));
                product.setName(jedis.hget("movie:" + id, "movieName"));
                product.setCategoryID(Integer.parseInt(jedis.hget("movie:" + id, "categoryId")));
                product.setPrice(Double.parseDouble(jedis.hget("movie:" + id, "price")));
                product.setQuantity(Double.parseDouble(jedis.hget("movie:" + id, "quantity")));
                return product;
            }
        } catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveProduct(Product product) {
        try {
            String key = "movie:" + product.getProductID();
            jedis.hset(key, "movieId", String.valueOf(product.getProductID()));
            jedis.hset(key, "movieName", product.getName());
            jedis.hset(key, "price", String.valueOf(product.getPrice()));
            jedis.hset(key, "quantity", String.valueOf(product.getQuantity()));
            jedis.hset(key, "rate", String.valueOf(3.0));
            jedis.hset(key, "categoryId", String.valueOf(product.getCategoryID()));
            return true;
        } catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
            return false;
        }
    }

    public Order loadOrder(int id) {
        try {
            MongoCollection<Order> collection = database.getCollection("orders", Order.class);
            Order order=collection.find(eq("orderID",id)).first();
            return order;

        } catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveOrder(Order order) {
        try {
            MongoCollection<Order> collection = database.getCollection("orders", Order.class);
//            System.out.println("=====\n"+order.getOrderID());
            collection.insertOne(order);

            return true;
        }
        catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
            return false;
        }
    }

    public User loadUser(String username, String password) {
        try {

            String username1 = jedis.hget("user:" + username, "username");
            String password1 = jedis.hget("user:" + username, "password");
            if (username.equals(username1) && password.equals(password1)) {
                User user = new User();
                user.setUsername(jedis.hget("user:" + username, "username"));
                user.setPassword(jedis.hget("user:" + username, "password"));
                user.setFullName(jedis.hget("user:" + username, "displayname"));
                user.setUsertype(Integer.parseInt(jedis.hget("user:" + username, "userType")));
                return user;
            }

        } catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
        }
        return null;
    }

    public User loadUser(String username) {
        try {

            String username1 = jedis.hget("user:" + username, "username");

            if (username.equals(username1)) {
                User user = new User();
                user.setUsername(jedis.hget("user:" + username, "username"));
                user.setPassword(jedis.hget("user:" + username, "password"));
                user.setFullName(jedis.hget("user:" + username, "displayname"));
                user.setUsertype(Integer.parseInt(jedis.hget("user:" + username, "userType")));
                return user;
            }

        } catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveUser(String username, String password, String displayName, int userType){
        try {
            String key = "user:" + username;
            jedis.hset(key, "username", username);
            jedis.hset(key, "password", password);
            jedis.hset(key, "displayname", displayName);
            jedis.hset(key, "userType", String.valueOf(userType));
            return true;
        } catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean savePayment(Payment payment) {
        try {
            String key1 = "address:" + payment.getOrderID();
            jedis.hset(key1, "orderId", String.valueOf(payment.getOrderID()));
            jedis.hset(key1, "address", payment.getAddress());
            String key2 = "creditCard:" + payment.getOrderID();
            jedis.hset(key2, "orderId", String.valueOf(payment.getOrderID()));
            jedis.hset(key2, "creditCardNumber", payment.getCreditcard());
            return true;
        } catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveReceipt(int id, String receipts) {
        try {
            MongoCollection<Order> collection = database.getCollection("orders", Order.class);
            Bson filter = eq("orderID", id);
            Bson update = set("receipt", URLDecoder.decode(receipts,"UTF-8"));
            collection.updateOne(filter,update);// new UpdateOptions().upsert(true)

            return true;
        }
        catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> loadAllMovieList(){
        List<Object[]> list= new ArrayList<>();
        try {
            Set<String> keys = jedis.keys("movie:*");
            for (String key : keys) {
                Object[] row = new Object[4];
                row[0]=jedis.hget(key, "movieId");

                row[1]=jedis.hget(key, "movieName");
                int categoryId = Integer.parseInt(jedis.hget(key, "categoryId"));
                String category = jedis.hget("category:"+categoryId, "categoryName");
                row[2] = category;
                row[3]=jedis.hget(key, "rate");
                list.add(row);
            }
        } catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
        }
        return list;
    }
//
//    public Movie loadMovie(int movieID) {
//        try {
//
//            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Movies WHERE MovieID = ?");
//            statement.setInt(1, movieID);
//            ResultSet resultSet = statement.executeQuery();
//
//            if (resultSet.next()) {
//                Movie m = new Movie();
//                m.setMovieID(resultSet.getInt(1));
//                m.setMovieName(resultSet.getString(2));
//                m.setCategoryID(resultSet.getInt(3));
//                m.setRate(resultSet.getDouble(4));
//                resultSet.close();
//                statement.close();
//
//                return m;
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Database access error!");
//            e.printStackTrace();
//        }
//        return null;
//    }
//
    public boolean saveReview(Rate review) {
        int weightBefore=10;
        try {
            String key = "movie:" + review.getMovieID();
            double beforeRate=Double.parseDouble(jedis.hget(key, "rate"));
            double afterRate=(beforeRate*weightBefore+review.getRate())/(weightBefore+1);

            jedis.hset(key, "rate", String.format("%.2f", afterRate));

            MongoCollection<Rate> collection = database.getCollection("reviews", Rate.class);

//            System.out.println("=====\n"+order.getOrderID());
            collection.insertOne(review);

            return true;
        } catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
            return false;
        }
    }

    public Movie recomMovie(String movieType) {
        try {
            Set<String> categoryKeys = jedis.keys("category:*");
            String id = "";
            for(String key: categoryKeys) {
                String category = jedis.hget(key, "categoryName");
                if(movieType.equals(category)) {
                    id = jedis.hget(key, "categoryId");
                }
            }

            System.out.println("categoryId: "+id);
            Movie movie = new Movie();
            if(id.isEmpty()) {
                return movie;
            }
            Set<String> keys = jedis.keys("movie:*");
            double maxRate = 0.0;
            String maxKey = "";
            for(String key: keys) {
                String categoryId = jedis.hget(key, "categoryId");
                double rate = Double.parseDouble(jedis.hget(key, "rate"));
                if(id.equals(categoryId)&&rate>maxRate) {
                    maxRate = rate;
                    maxKey = key;
                }
            }
            System.out.println(maxKey+" "+maxRate);
            if(maxKey.isEmpty()) {
                return movie;
            }
            movie.setMovieID(Integer.parseInt(jedis.hget(maxKey, "movieId")));
            movie.setMovieName(jedis.hget(maxKey, "movieName"));
            movie.setRate(Double.parseDouble(jedis.hget(maxKey, "rate")));
            movie.setCategoryID(Integer.parseInt(jedis.hget(maxKey, "categoryId")));

            MongoCollection<Rate> collection = database.getCollection("reviews", Rate.class);
            Bson projectionFields = Projections.fields(
                    Projections.include("review"),
                    Projections.excludeId());
            // Retrieves the first matching document, applying a projection and a descending sort to the results
            Rate doc = collection.find(eq("movieID", movie.getMovieID()))
                    .projection(projectionFields)
                    .sort(Sorts.descending("rate"))
                    .first();
            String review = "";
            if(doc!=null) {
                review = doc.getReview();
            }
            System.out.println(review);
            movie.setReview(review);
            return movie;
        } catch (Exception e) {
            System.out.println("Database access error!");
            e.printStackTrace();
        }
        return null;
    }
//        try {
//
//            System.out.println((int)new Date().getTime()/1000);
//            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Movies WHERE MovieID = ?");
//            statement.setInt(1, movieID);
//            ResultSet resultSet = statement.executeQuery();
//
//            if (resultSet.next()) {
//                double beforeRate=resultSet.getDouble("Rate");
//                resultSet.close();
//                statement.close();
//                double afterRate=(beforeRate*99+rate)/100f;
//                PreparedStatement st= connection.prepareStatement("UPDATE Movies SET Rate = ? WHERE MovieID = ?");
//                st.setDouble(1, Math.round(afterRate*100)/100.0);
//                st.setInt(2, movieID);
//                st.executeUpdate();
//                st.close();
//                st= connection.prepareStatement("INSERT INTO Reviews VALUES (?,?,?,?,?)");
//                st.setInt(1, (int)new Date().getTime()/1000);
//                st.setInt(2,userID);
//                st.setInt(3,movieID);
//                st.setInt(4,rate);
//                st.setString(5,review);
//                st.execute();
//                st.close();
//
//            }
//
//            return true;
//
//        } catch (SQLException e) {
//            System.out.println("Database access error!");
//            e.printStackTrace();
//        }
//        return false;
//    }
}
