package restAssured;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static org.testng.Assert.*;

public class TestTodoAPI extends BaseTest {
    @DataProvider
    private Object[] newEntries() {
        JsonArray newEntriesJsonArray;
        ArrayList<JsonObject> newEntriesJson = new ArrayList<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("newEntries.json");
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        newEntriesJsonArray = JsonParser.parseReader(reader).getAsJsonArray();
        newEntriesJsonArray.forEach(item -> newEntriesJson.add(item.getAsJsonObject()));
        return newEntriesJson.toArray();
    }

    @DataProvider
    private Object[] limitNumber() {
        return new Integer[]{1, 3, 5};
    }

    @Test(dataProvider = "newEntries")
    public void testAddTodoEntries(JsonObject entryJson) {
        Gson gson = new Gson();
        Entry entry = gson.fromJson(entryJson, Entry.class);
        TestUtil.updateTodoList();

        // Count existing entries that have the same value as the new entry
        int n = TestUtil.getNumberOfEntries(todoList, entry);
        given().
                contentType(JSON).
                body(entryJson.toString()).
                when().
                // Act
                        post(TODO_LIST_URL).
                then().
                // Assert
                        assertThat().statusCode(HttpStatus.SC_OK);

        TestUtil.updateTodoList();
        int m = TestUtil.getNumberOfEntries(todoList, entry);
        assertEquals(m, n + 1);
    }

    @Test
    public void testDeleteAllEntries() {
        // Act
        delete(TODO_LIST_URL).then().

                // Assert
                        assertThat().statusCode(HttpStatus.SC_OK);

        TestUtil.updateTodoList();
        assertEquals(todoList.size(), 0);
    }

    @Test
    public void testListEntries() {
        // Act
        Response response = get(TODO_LIST_URL).then().
                // Assert
                        assertThat().statusCode(HttpStatus.SC_OK).
                        extract().response();

        JsonArray todolistArray = JsonParser.parseString(response.asString()).getAsJsonArray();
        todolistArray.forEach(entryJson -> {
            Gson gson = new Gson();
            Entry entry = gson.fromJson(entryJson, Entry.class);
            todoList.add(entry);
        });
    }

    @Test
    public void testGetNumberOfEntries() {
        // Act
        Response response = get(TODO_LIST_URL + "/count").then().

                // Assert
                        assertThat().statusCode(HttpStatus.SC_OK).
                        extract().response();

        int numberOfEntries = Integer.parseInt(response.asString());
        TestUtil.updateTodoList();
        assertEquals(numberOfEntries, todoList.size());
    }

    @Test(dataProvider = "limitNumber")
    public void testListWithLimit(int limitNumber) {
        todoList.clear();

        Response response =

                // Act
                get(TODO_LIST_URL + "/" + limitNumber).then().

                        // Assert
                                assertThat().statusCode(HttpStatus.SC_OK).
                        extract().response();

        JsonArray todolistArray = JsonParser.parseString(response.asString()).getAsJsonArray();
        todolistArray.forEach(entryJson -> {
            Gson gson = new Gson();
            Entry entry = gson.fromJson(entryJson, Entry.class);
            todoList.add(entry);
        });
        assertTrue(todoList.size() <= limitNumber);
    }

    @Test
    public void testDeleteEntriesByTitle() {
        TestUtil.updateTodoList();
        Entry entry = todoList.get(0);

        // Act
        Response response = delete(TODO_LIST_URL + "?title=" + entry.title).then().

                // Assert
                        assertThat().statusCode(HttpStatus.SC_OK).
                        extract().response();

        TestUtil.updateTodoList();
        assertFalse(todoList.stream().anyMatch(entryI -> entryI.title.equals(entry.title)));
    }
}
