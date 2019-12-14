package httpClient;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static org.testng.Assert.*;

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

    @DataProvider
    private Object[] limitNumber() {
        return new Integer[]{1, 3, 5};
    }

    @Test
    public void testSendMailReporting() throws IOException {

        // Arrange
        HttpGet get = new HttpGet(REPORTING_URL);

        // Act
        response = client.execute(get);

        // Assert
        int status = response.getStatusLine().getStatusCode();
        assertEquals(status, HttpStatus.SC_OK);
    }

    @Test(dataProvider = "newEntries")
    public void testAddEntry(JsonObject entryJson) throws IOException {

        // Count existing entries that have the same values as the new Entry before adding it to todolist
        Gson gson = new Gson();
        Entry entry = gson.fromJson(entryJson, Entry.class);
        int n = TestUtil.numberOfEntries(todoList, entry);

        HttpPost post = new HttpPost(TODO_LIST_URL);

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

    @Test
    public void testDeleteAllEntries() throws IOException {
        HttpDelete delete = new HttpDelete(TODO_LIST_URL);
        // Act
        response = client.execute(delete);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, HttpStatus.SC_OK);
        TestUtil.updateTodolist();
        // Verify all entries have been deleted
        assertEquals(todoList.size(), 0);
    }

    @Test
    public void testListEntries() throws IOException {
        HttpGet get = new HttpGet(TODO_LIST_URL);

        // Act
        response = client.execute(get);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, HttpStatus.SC_OK);

        todoList.clear();
        String json = EntityUtils.toString(response.getEntity());
        JsonArray todoArray = JsonParser.parseString(json).getAsJsonArray();
        todoArray.forEach(jsonElement -> {
            Gson gson = new Gson();
            Entry entry = gson.fromJson(jsonElement, Entry.class);
            todoList.add(entry);
        });
    }

    @Test
    public void testGetNumberOfEntries() throws IOException {
        HttpGet get = new HttpGet(TODO_LIST_URL + "/count");

        // Act
        response = client.execute(get);

        // Assert
        int actualStatus = response.getStatusLine().getStatusCode();
        assertEquals(actualStatus, HttpStatus.SC_OK);
        String jsonBody = EntityUtils.toString(response.getEntity());
        int numberOfEntries = Integer.parseInt(jsonBody);
        TestUtil.updateTodolist();
        assertEquals(numberOfEntries, todoList.size());
    }

    @Test(dataProvider = "limitNumber")
    public void testListWithLimit(int limitNumber) throws IOException {
        HttpGet get = new HttpGet(TODO_LIST_URL + "/" + limitNumber);

        // Act
        response = client.execute(get);
        int actualStatus = response.getStatusLine().getStatusCode();
        assertEquals(actualStatus, HttpStatus.SC_OK);

        // Assert result
        if (actualStatus == HttpStatus.SC_OK) {
            todoList.clear();
            String json = EntityUtils.toString(response.getEntity());
            JsonArray jasonArray = JsonParser.parseString(json).getAsJsonArray();
            for (int i = 0; i < jasonArray.size(); i++) {
                Gson gson = new Gson();
                Entry entry = gson.fromJson(jasonArray.get(i), Entry.class);
                todoList.add(entry);
            }
            assertTrue(limitNumber >= todoList.size());
        }
    }

    @Test
    public void testDeleteEntriesByTitle() throws IOException {
        TestUtil.updateTodolist();
        Entry entry = todoList.get(0);
        HttpDelete delete = new HttpDelete( TODO_URL + "?title=" + entry.title);

        // Act
        response = client.execute(delete);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, HttpStatus.SC_OK);
        TestUtil.updateTodolist();
        assertFalse(todoList.stream().anyMatch(entryI -> entryI.title.equals(entry.title)));
    }

    @Test
    public void testDeleteEntryById() throws IOException {
        // Arrange
        Entry entry = todoList.get(0);
        HttpDelete delete = new HttpDelete(TODO_URL + "/" + entry.id);

        // Act
        response = client.execute(delete);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, HttpStatus.SC_OK);
        TestUtil.updateTodolist();
        assertFalse(todoList.stream().anyMatch(entryI -> entryI.id.equals(entry.id)));
    }

    @Test
    public void testFindEntryById() throws IOException {
        Entry entryFound;
        Entry entry = todoList.get(0);

        // Act
        HttpGet get = new HttpGet(TODO_URL + "/" + entry.id);
        response = client.execute(get);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, HttpStatus.SC_OK);
        String jsonString = EntityUtils.toString(response.getEntity());
        JsonObject entryJson = JsonParser.parseString(jsonString).getAsJsonObject();
        Gson gson = new Gson();
        entryFound = gson.fromJson(entryJson, Entry.class);
        assertTrue(entry.equals(entryFound));
    }

    @Test
    public void testSetEntryStatus() throws IOException {
        // Arrange
        Entry entry = todoList.get(0);
        boolean done = !entry.done;
        HttpPut put = new HttpPut(TODO_URL + "/" + entry.id + "?done=" + done);

        // Act
        response = client.execute(put);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, HttpStatus.SC_OK);
        TestUtil.updateTodolist();
        entry = todoList.get(0);
        assertEquals(done, (boolean) entry.done);
    }
}
