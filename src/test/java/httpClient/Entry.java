package httpClient;

public class Entry {
    String id;
    String title;
    Boolean done;
    String description;
    attachment attachment;

    class attachment {
        String cid;
        String contentType;
        String data;
    }

    @Override
    public boolean equals(Object input) {
        if (!(input instanceof Entry)) return false;

        Entry entry = (Entry) input;
        return ((entry.id == null && id == null) || entry.id.equals(id)) &&
                ((entry.title == null && title == null) || entry.title.equals(title)) &&
                ((entry.description == null && description == null) || entry.description.equals(description)) &&
                ((entry.done == null && done == null) || entry.done.equals(done)) &&
                (((entry.attachment == null && attachment == null) ||
                        (entry.attachment != null && attachment != null &&
                                entry.attachment.cid.equals(attachment.cid) &&
                                entry.attachment.contentType.equals(attachment.contentType) &&
                                entry.attachment.data.equals(attachment.data))));
    }
}
