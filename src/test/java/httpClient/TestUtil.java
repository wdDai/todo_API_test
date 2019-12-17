package httpClient;

import com.google.common.net.MediaType;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;

public class TestUtil extends BaseTest {

    public static int numberOfEntries(ArrayList<Entry> todolist, Entry entry){
        int n = 0;
        for (int i=0; i < todolist.size(); i++){
            Entry actualEntry = todolist.get(i);

            if(entry.id != null){
                if (actualEntry.id == null || !entry.id.equals(actualEntry.id)){
                    continue;
                }
            }
            if(entry.title != null){
                if (actualEntry.title == null || !entry.title.equals(actualEntry.title)){
                    continue;
                }
            }
            if(entry.description != null){
                if (actualEntry.description == null || !entry.description.equals(actualEntry.description)){
                    continue;
                }
            }
            if(entry.done != null){
                if (actualEntry.done == null || !entry.done.equals(actualEntry.done)){
                    continue;
                }
            }
            if(entry.attachment != null){
                if (actualEntry.attachment == null){
                    continue;
                }
                else {
                    if(entry.attachment.cid != null) {
                        if (actualEntry.attachment.cid == null || !entry.attachment.cid.equals(actualEntry.attachment.cid)) {
                            continue;
                        }
                    }
                    if(entry.attachment.contentType != null){
                        if(actualEntry.attachment.contentType == null || !entry.attachment.contentType.equals(actualEntry.attachment.contentType)){
                            continue;
                        }
                    }
                    if(entry.attachment.data != null){
                        if(actualEntry.attachment.data == null || !entry.attachment.data.equals(actualEntry.attachment.data)){
                            continue;
                        }
                    }
                }
            }
            n++;
        }
        return n;
    }

    public static ArrayList<Entry> updateTodolist() throws IOException {
        todoList.clear();
        HttpGet get = new HttpGet(TODO_LIST_URL);
        response = client.execute(get);
        String json = EntityUtils.toString(response.getEntity());
        JsonArray todoArray = JsonParser.parseString(json).getAsJsonArray();
        todoArray.forEach(jsonElement -> {
            Gson gson = new Gson();
            Entry entry = gson.fromJson(jsonElement.getAsJsonObject(),Entry.class);
            todoList.add(entry);
        });
        return todoList;
    }

    public static void addNewEntries() throws FileNotFoundException {
        ArrayList<JsonObject> newEntries = newEntries();
        newEntries.forEach(entryJson ->{
            client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(TODO_LIST_URL);

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

    private static ArrayList<JsonObject> newEntries() {
        JsonArray newEntriesJsonArray;
        ArrayList<JsonObject> newEntriesJson = new ArrayList<>();
        InputStream inputStream = TestUtil.class.getClassLoader().getResourceAsStream("newEntries.json");
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        newEntriesJsonArray = JsonParser.parseReader(reader).getAsJsonArray();
        for (JsonElement jsonElement : newEntriesJsonArray) {
            newEntriesJson.add(jsonElement.getAsJsonObject());
        }
        return newEntriesJson;
    }

    public static void deleteAllEntries() throws IOException {
        HttpDelete delete = new HttpDelete(TODO_LIST_URL);
        client.execute(delete);
    }
}
