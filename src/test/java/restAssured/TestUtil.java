package restAssured;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.restassured.response.Response;

import java.io.*;
import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;


public class TestUtil extends BaseTest {

    static void updateTodoList() {
        todoList.clear();
        Response response = get(TODO_LIST_URL).then().extract().response();
        JsonArray todoListArray = JsonParser.parseString(response.asString()).getAsJsonArray();
        todoListArray.forEach(jsonElement -> {
            Gson gson = new Gson();
            Entry entry = gson.fromJson(jsonElement.getAsJsonObject(), Entry.class);
            todoList.add(entry);
        });
    }


    static int getNumberOfEntries(ArrayList<Entry> todoList, Entry entry) {
        int n = 0;
        for (Entry actualEntry : todoList) {
            if (entry.id != null) {
                if (actualEntry.id == null || !entry.id.equals(actualEntry.id)) {
                    continue;
                }
            }
            if (entry.title != null) {
                if (actualEntry.title == null || !entry.title.equals(actualEntry.title)) {
                    continue;
                }
            }
            if (entry.description != null) {
                if (actualEntry.description == null || !entry.description.equals(actualEntry.description)) {
                    continue;
                }
            }
            if (entry.done != null) {
                if (actualEntry.done == null || !entry.done.equals(actualEntry.done)) {
                    continue;
                }
            }
            if (entry.attachment != null) {
                if (actualEntry.attachment == null) {
                    continue;
                } else {
                    if (entry.attachment.cid != null) {
                        if (actualEntry.attachment.cid == null || !entry.attachment.cid.equals(actualEntry.attachment.cid)) {
                            continue;
                        }
                    }
                    if (entry.attachment.contentType != null) {
                        if (actualEntry.attachment.contentType == null || !entry.attachment.contentType.equals(actualEntry.attachment.contentType)) {
                            continue;
                        }
                    }
                    if (entry.attachment.data != null) {
                        if (actualEntry.attachment.data == null || !entry.attachment.data.equals(actualEntry.attachment.data)) {
                            continue;
                        }
                    }
                }
            }
            n++;
        }
        return n;
    }

    static void addNewEntries() throws FileNotFoundException {
        ArrayList<JsonObject> newEntries = newEntries();
        newEntries.forEach(entry -> {
            addNewEntry(entry);
        });
    }

    private static ArrayList<JsonObject> newEntries() throws FileNotFoundException {
        JsonArray newEntriesJsonArray;
        ArrayList<JsonObject> newEntriesJson = new ArrayList<>();
        InputStream inputStream = TestUtil.class.getClassLoader().getResourceAsStream("newEntries.json");
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        newEntriesJsonArray = JsonParser.parseReader(reader).getAsJsonArray();
        newEntriesJsonArray.forEach(jsonElement -> {
            JsonObject entry = jsonElement.getAsJsonObject();
            addNewEntry(entry);
        });

        return newEntriesJson;
    }

    private static void addNewEntry(JsonObject entry) {
        given().contentType(JSON).body(entry.toString()).post(TODO_LIST_URL);
    }

    static void deleteAllEntries() {
        delete(TODO_LIST_URL);
    }
}