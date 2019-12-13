package httpClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

public class TestUtil extends BaseClass{

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
        HttpGet get = new HttpGet(BASE_URL + "/todolist");
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
}
