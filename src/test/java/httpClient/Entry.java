package httpClient;

public class Entry {
    String id;
    String title;
    Boolean done;
    String description;
    attachment attachment;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Entry)) return false;

        Entry in = (Entry) obj;
        if (!(in.id == null && id == null) && !in.id.equals(id))
            return false;
        else if ((!(in.title == null && title == null) && !in.title.equals(title)))
            return false;
        else if (!(in.description == null && description == null) && !in.description.equals(description))
            return false;
        else if (!(in.done == null && done == null) && !in.done.equals(done))
            return false;
        else return in.attachment == null && attachment == null ||
                    in.attachment != null && attachment != null &&
                            in.attachment.cid.equals(attachment.cid) &&
                            in.attachment.contentType.equals(attachment.contentType) &&
                            in.attachment.data.equals(attachment.data);

    }

    class attachment {
        String cid;
        String contentType;
        String data;
    }
}
