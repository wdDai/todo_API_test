package httpClient;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static org.testng.Assert.assertEquals;

public class TestClass extends BaseClass {
    @DataProvider
    private Object[] newEntries() {
        JsonArray newEntriesJsonArray;
        ArrayList<JsonObject> newEntriesJson = new ArrayList<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("newEntries.json");
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        newEntriesJsonArray = JsonParser.parseReader(reader).getAsJsonArray();
        for (JsonElement jsonElement : newEntriesJsonArray) {
            newEntriesJson.add(jsonElement.getAsJsonObject());
        }
        return newEntriesJson.toArray();
    }

    @Test
    public void testSendMailReporting() throws IOException {

        // Arrange
        HttpGet get = new HttpGet(BASE_URL + "/reporting/mail");

        // Act
        response = client.execute(get);

        // Assert
        int status = response.getStatusLine().getStatusCode();
        assertEquals(status, HttpStatus.SC_OK);
    }

    @Test(dataProvider = "newEntries")
    public void testAddTodoEntry(JsonObject entryJson) throws IOException {

        // Count existing entries that have the same values as the new Entry before adding it to todolist
        Gson gson = new Gson();
        Entry entry = gson.fromJson(entryJson, Entry.class);
        int n = TestUtil.numberOfEntries(todoList, entry);

        HttpPost post = new HttpPost(BASE_URL + "/todolist");

        String entryString = entryJson.toString();
        StringEntity entryEntity = new StringEntity(entryString);

        post.setEntity(entryEntity);
        post.setHeader("Content-type", "application/json");

        // Act
        response = client.execute(post);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, HttpStatus.SC_OK);
        TestUtil.updateTodolist();

        // Assert new entry is in todoList
        // 1.Count the number of entries that have the values of jsonEntry
        // 2.Assert that the number has increased by 1
        int m = TestUtil.numberOfEntries(todoList, entry);
        assertEquals(n + 1, m);

    }
}
