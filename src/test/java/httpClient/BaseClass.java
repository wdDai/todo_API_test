package httpClient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.util.ArrayList;

public class BaseClass {
    static final String BASE_URL = "http://localhost:8081/api";
    static CloseableHttpClient client;
    static CloseableHttpResponse response;
    static ArrayList<Entry> todoList = new ArrayList<>();

    @BeforeMethod
    public void setUp(){
        client = HttpClientBuilder.create().build();
    }

    @AfterMethod
    public void cleanUp() throws IOException {
        client.close();
        response.close();
    }
}
