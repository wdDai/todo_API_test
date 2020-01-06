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
import static org.testng.Assert.assertEquals;

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
}
