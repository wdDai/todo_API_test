package httpClient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.util.ArrayList;

public class TestBase {
    static final String REPORTING_URL = "http://localhost:8081/api/reporting/mail";
    static final String TODOLIST_URL = "http://localhost:8081/api/todolist";
    static final String TODO_URL = "http://localhost:8081/api/todo";
    static CloseableHttpClient client;
    static CloseableHttpResponse response;
    static ArrayList<Entry> todoList = new ArrayList<>();

    @BeforeMethod
    public void setUp() throws IOException {
        client = HttpClientBuilder.create().build();
        Utils.deleteAllEntries();
        Utils.addNewEntries();
        Utils.updateTodolist();
    }

    @AfterMethod
    public void cleanUp() throws IOException {
        Utils.deleteAllEntries();
        client.close();
        response.close();
    }
}
