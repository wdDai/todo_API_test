package restAssured;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static org.testng.Assert.*;
import static restAssured.Utils.*;

public class TestTodoAPI extends BaseTest {
    @DataProvider
    private Object[] newEntries() {
        return getNewEntries().toArray();
    }

    @Test(dataProvider = "newEntries")
    public void testAddTodoEntries(JsonObject entryJson) {
        Entry entry = new Gson().fromJson(entryJson, Entry.class);
        updateTodoList();

        // Count existing entries that have the same value as the new entry
        int n = Utils.numberOfEntries(todoList, entry);
        given().
                contentType(JSON).
                body(entryJson.toString()).
                when().
                // Act
                        post(TODOLIST_URL).
                then().
                // Assert
                        assertThat().statusCode(HttpStatus.SC_OK);

        updateTodoList();
        int m = Utils.numberOfEntries(todoList, entry);
        assertEquals(m, n + 1);
    }

    @Test
    public void testDeleteAllEntries() {
        // Act
        delete(TODOLIST_URL).then().

                // Assert
                        assertThat().statusCode(HttpStatus.SC_OK);

        updateTodoList();
        assertEquals(todoList.size(), 0);
    }

    @Test
    public void testListEntries() {
        // Act
        Response response = get(TODOLIST_URL).then().
                // Assert
                        assertThat().statusCode(HttpStatus.SC_OK).
                        extract().response();

        updateTodoList();
    }

    @Test
    public void testGetNumberOfEntries() {
        // Act
        Response response = get(TODOLIST_URL + "/count").then().

                // Assert
                        assertThat().statusCode(HttpStatus.SC_OK).
                        extract().response();

        int numberOfEntries = Integer.parseInt(response.asString());
        updateTodoList();
        assertEquals(numberOfEntries, todoList.size());
    }

    @DataProvider
    private Object[] limitNumber() {
        return new Integer[]{1, 3, 5};
    }

    @Test(dataProvider = "limitNumber")
    public void testListWithLimit(int limitNumber) {
        todoList.clear();

        Response response =

                // Act
                get(TODOLIST_URL + "/" + limitNumber).then().

                        // Assert
                                assertThat().statusCode(HttpStatus.SC_OK).
                        extract().response();
        extractResToList(response);
        assertTrue(todoList.size() <= limitNumber);
    }

    @Test
    public void testDeleteEntriesByTitle() {
        updateTodoList();
        Entry entry = todoList.get(0);

        // Act
        Response response = delete(TODOLIST_URL + "?title=" + entry.title).then().

                // Assert
                        assertThat().statusCode(HttpStatus.SC_OK).
                        extract().response();

        updateTodoList();
        assertFalse(todoList.stream().anyMatch(entryI -> entryI.title.equals(entry.title)));
    }
}
