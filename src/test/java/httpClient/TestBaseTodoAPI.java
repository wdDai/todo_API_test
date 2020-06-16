package httpClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static httpClient.Utils.*;
import static org.apache.http.HttpStatus.SC_OK;
import static org.testng.Assert.*;

public class TestBaseTodoAPI extends TestBase {
    @DataProvider
    private Object[] newEntries() {
        return getNewEntries().toArray();
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
        assertEquals(status, SC_OK);
    }

    @Test(dataProvider = "newEntries")
    public void testAddEntry(JsonObject entryJson) throws IOException {
        // Arrange
        // Count existing entries that have the same values as the new Entry before adding it to todolist
        Entry entry = new Gson().fromJson(entryJson, Entry.class);
        int n = numberOfEntries(todoList, entry);

        HttpPost post = new HttpPost(TODOLIST_URL);

        StringEntity entity = new StringEntity(entryJson.toString());

        post.setEntity(entity);
        post.setHeader("Content-type", "application/json");

        // Act
        response = client.execute(post);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, SC_OK);
        updateTodolist();

        // Assert new entry is in todoList
        // 1.Count the number of entries that have the values of jsonEntry
        // 2.Assert that the number has increased by 1
        int m = numberOfEntries(todoList, entry);
        assertEquals(n + 1, m);
    }

    @Test
    public void testDeleteAllEntries() throws IOException {
        //Arrange
        HttpDelete delete = new HttpDelete(TODOLIST_URL);
        // Act
        response = client.execute(delete);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, SC_OK);
        updateTodolist();
        assertEquals(todoList.size(), 0);
    }

    @Test
    public void testListEntries() throws IOException {
        // Arrange
        HttpGet get = new HttpGet(TODOLIST_URL);

        // Act
        response = client.execute(get);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, SC_OK);

        // Clean up
        updateTodolist();
    }

    @Test
    public void testGetNumberOfEntries() throws IOException {
        // Arrange
        HttpGet get = new HttpGet(TODOLIST_URL + "/count");

        // Act
        response = client.execute(get);

        // Assert
        int actualStatus = response.getStatusLine().getStatusCode();
        assertEquals(actualStatus, SC_OK);
        String jsonBody = EntityUtils.toString(response.getEntity());
        int numberOfEntries = Integer.parseInt(jsonBody);
        updateTodolist();
        assertEquals(numberOfEntries, todoList.size());
    }

    @Test(dataProvider = "limitNumber")
    public void testListWithLimit(int limitNumber) throws IOException {
        // Arrange
        HttpGet get = new HttpGet(TODOLIST_URL + "/" + limitNumber);

        // Act
        response = client.execute(get);
        int actualStatus = response.getStatusLine().getStatusCode();
        assertEquals(actualStatus, SC_OK);

        // Assert result
        if (actualStatus == SC_OK) {
            todoList.clear();
            extractResToList(response);
            assertTrue(limitNumber >= todoList.size());
        }
    }

    @Test
    public void testDeleteEntriesByTitle() throws IOException {
        // Arrange
        updateTodolist();
        Entry entry = todoList.get(0);
        HttpDelete delete = new HttpDelete(TODO_URL + "?title=" + entry.title);

        // Act
        response = client.execute(delete);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, SC_OK);
        updateTodolist();
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
        assertEquals(actualStatus, SC_OK);
        updateTodolist();
        assertFalse(todoList.stream().anyMatch(entryI -> entryI.id.equals(entry.id)));
    }

    @Test
    public void testFindEntryById() throws IOException {
        // Arrange
        Entry entryFound;
        Entry entry = todoList.get(0);

        // Act
        HttpGet get = new HttpGet(TODO_URL + "/" + entry.id);
        response = client.execute(get);
        int actualStatus = response.getStatusLine().getStatusCode();

        // Assert
        assertEquals(actualStatus, SC_OK);
        String jsonString = EntityUtils.toString(response.getEntity());
        JsonObject entryJson = JsonParser.parseString(jsonString).getAsJsonObject();
        entryFound = new Gson().fromJson(entryJson, Entry.class);
        assertEquals(entryFound, entry);
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
        assertEquals(actualStatus, SC_OK);
        updateTodolist();
        entry = todoList.get(0);
        assertEquals(done, (boolean) entry.done);
    }
}
