import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Updates.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.client.result.UpdateResult;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Connection;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;

public class Application {

    private static Application instance;   // Singleton pattern

    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }
    // Main components of this application

//    private Connection connection;
//
//    public Connection getDBConnection() {
//        return connection;
//    }

    private DataAdapter dataAdapter;

    private User currentUser = null;

    public User getCurrentUser() { return currentUser; }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private ProductView productView = new ProductView();

    private OrderView orderView = new OrderView();

    private PaymentView paymentView = new PaymentView();

    private MainScreen mainScreen = new MainScreen();
    private RecomView recomView = new RecomView();

    private Socket socket = null;

    public Socket getSocket() {return socket;}

    public void setSocket(Socket socket) {this.socket = socket;}
    public MainScreen getMainScreen() {
        return mainScreen;
    }

    public ProductView getProductView() {
        return productView;
    }

    public OrderView getOrderView() {
        return orderView;
    }

    public PaymentView getPaymentView() {
        return paymentView;
    }

    public LoginScreen loginScreen = new LoginScreen();

    public LoginScreen getLoginScreen() {
        return loginScreen;
    }

    public SignScreen signScreen=new SignScreen();

    public SignScreen getSignScreen(){return signScreen;}

    public MovieView movieView=new MovieView();

    public MovieView getMovieView() {
        return movieView;
    }

    public RateView rateView=new RateView();

    public RateView getRateView(){return rateView;}

    public ManagerScreen managerScreen=new ManagerScreen();

    public ManagerScreen getManagerScreen() {
        return managerScreen;
    }
    public RecomView getRecomView() {return recomView;}

    public DataAdapter getDataAdapter() {
        return dataAdapter;
    }

    private Connection redisConnection;

    private Application() {
        // create SQLite database connection here!
        //            Class.forName("org.sqlite.JDBC");
//
//            String url = "jdbc:sqlite:store.db";
//
//            connection = DriverManager.getConnection(url);
//            dataAdapter = new DataAdapter(connection);

        Jedis jedis = new Jedis("redis://admin:Jingcao123!@redis-16784.c93.us-east-1-3.ec2.cloud.redislabs.com:16784");
        redisConnection = jedis.getConnection();
        String connectionString = "mongodb+srv://test:test2@cluster0.wahxgxp.mongodb.net/?retryWrites=true&w=majority";
        try {
            MongoClientURI uri = new MongoClientURI(connectionString);
            MongoClient mongoClient = new MongoClient(uri);
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
            CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
            // Connect to the database
            MongoDatabase database = mongoClient.getDatabase("store").withCodecRegistry(pojoCodecRegistry);
            dataAdapter = new DataAdapter(jedis, database);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) throws IOException {
        String serverHostname = new String("127.0.0.1");
        int portNumber = 7777;
        System.out.println("Attemping to connect to host " + serverHostname + " on port " + portNumber);
        Socket socket = new Socket(serverHostname, portNumber);
        Application.getInstance().setSocket(socket);
        Application.getInstance().getLoginScreen().setVisible(true);
    }


}
