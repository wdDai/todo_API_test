package restAssured;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;

public class BaseTest {
    static final String REPORTING_URL = "http://localhost:8081/api/reporting/mail";
    static final String TODOLIST_URL = "http://localhost:8081/api/todolist";
    static final String TODO_URL = "http://localhost:8081/api/todo";
    static ArrayList<Entry> todoList = new ArrayList<>();

    @BeforeMethod
    public void setUp() {
        Utils.deleteAllEntries();
        Utils.addNewEntries();
        Utils.updateTodoList();
    }

    @AfterMethod
    public void cleanUp() {
        Utils.deleteAllEntries();
    }
}