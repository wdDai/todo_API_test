package restAssured;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import io.restassured.response.Response;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;


public class Utils extends BaseTest {

    static void updateTodoList() {
        todoList.clear();
        Response response = get(TODOLIST_URL).then().extract().response();
        extractResToList(response);
    }

    public static void extractResToList(Response resp) {
        JsonArray todoListArray = JsonParser.parseString(resp.asString()).getAsJsonArray();
        todoListArray.forEach(jsonElement -> {
            Entry entry = new Gson().fromJson(jsonElement.getAsJsonObject(), Entry.class);
            todoList.add(entry);
        });
    }

    static int numberOfEntries(ArrayList<Entry> todoList, Entry en) {
        int n = 0;
        for (Entry current : todoList) {
            if (en.id != null) {
                if (current.id == null || !en.id.equals(current.id)) {
                    continue;
                }
            }
            if (en.title != null) {
                if (current.title == null || !en.title.equals(current.title)) {
                    continue;
                }
            }
            if (en.description != null) {
                if (current.description == null || !en.description.equals(current.description)) {
                    continue;
                }
            }
            if (en.done != null) {
                if (current.done == null || !en.done.equals(current.done)) {
                    continue;
                }
            }
            if (en.attachment != null) {
                if (current.attachment == null) {
                    continue;
                } else {
                    if (en.attachment.cid != null) {
                        if (current.attachment.cid == null || !en.attachment.cid.equals(current.attachment.cid)) {
                            continue;
                        }
                    }
                    if (en.attachment.contentType != null) {
                        if (current.attachment.contentType == null || !en.attachment.contentType.equals(current.attachment.contentType)) {
                            continue;
                        }
                    }
                    if (en.attachment.data != null) {
                        if (current.attachment.data == null || !en.attachment.data.equals(current.attachment.data)) {
                            continue;
                        }
                    }
                }
            }
            n++;
        }
        return n;
    }

    static void addNewEntries() {
        ArrayList<JsonObject> newEntries = getNewEntries();
        newEntries.forEach(Utils::addNewEntry);
    }

    public static ArrayList<JsonObject> getNewEntries() {
        JsonArray newEntries;
        ArrayList<JsonObject> newEntriesJson = new ArrayList<>();
        newEntries = readJsonArray("newEntries.json");
        for (JsonElement jsonElement : newEntries) {
            newEntriesJson.add(jsonElement.getAsJsonObject());
        }
        return newEntriesJson;
    }

    private static JsonArray readJsonArray(String s) {
        InputStream in = httpClient.Utils.class.getClassLoader().getResourceAsStream(s);
        assert in != null;
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        return JsonParser.parseReader(reader).getAsJsonArray();
    }

    private static void addNewEntry(JsonObject entry) {
        given().contentType(JSON).body(entry.toString()).post(TODOLIST_URL);
    }

    static void deleteAllEntries() {
        delete(TODOLIST_URL);
    }
}