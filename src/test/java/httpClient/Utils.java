package httpClient;

import com.google.common.net.MediaType;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;

public class Utils extends TestBase {

    public static int numberOfEntries(ArrayList<Entry> todolist, Entry en) {
        int n = 0;

        for (Entry current : todolist) {
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

    public static void updateTodolist() throws IOException {
        todoList.clear();
        HttpGet get = new HttpGet(TODOLIST_URL);
        response = client.execute(get);
        extractResToList(response);
    }

    public static void extractResToList(CloseableHttpResponse resp) throws IOException {
        String json = EntityUtils.toString(resp.getEntity());
        JsonArray jasonArray = JsonParser.parseString(json).getAsJsonArray();
        jasonArray.forEach(jsonElement -> {
            Entry entry = new Gson().fromJson(jsonElement.getAsJsonObject(), Entry.class);
            todoList.add(entry);
        });
    }

    public static void addNewEntries()  {
        ArrayList<JsonObject> newEntries = getNewEntries();
        newEntries.forEach(entryJson -> {
            client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(TODOLIST_URL);

            StringEntity entryEntity = null;
            try {
                entryEntity = new StringEntity(entryJson.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            post.setEntity(entryEntity);
            post.setHeader("Content-type", MediaType.JSON_UTF_8.toString());
            try {
                client.execute(post);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        client = HttpClientBuilder.create().build();
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
        InputStream in = Utils.class.getClassLoader().getResourceAsStream(s);
        assert in != null;
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        return JsonParser.parseReader(reader).getAsJsonArray();
    }

    public static void deleteAllEntries() throws IOException {
        HttpDelete delete = new HttpDelete(TODOLIST_URL);
        client.execute(delete);
    }
}
