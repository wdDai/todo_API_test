package restAssured;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
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
                assertThat().statusCode(HttpStatus.SC_OK);

        // Assert
        TestUtil.updateTodoList();
        int m = TestUtil.getNumberOfEntries(todoList, entry);
        assertEquals(m, n + 1);
    }
}
