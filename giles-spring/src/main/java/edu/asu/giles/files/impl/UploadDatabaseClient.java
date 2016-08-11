package edu.asu.giles.files.impl;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

import edu.asu.giles.core.IUpload;
import edu.asu.giles.core.impl.Upload;
import edu.asu.giles.db4o.DatabaseManager;
import edu.asu.giles.files.IUploadDatabaseClient;

@Service
public class UploadDatabaseClient extends DatabaseClient implements
        IUploadDatabaseClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ObjectContainer client;

    @Autowired
    @Qualifier("uploadDatabaseManager")
    private DatabaseManager userDatabase;

    @PostConstruct
    public void init() {
        client = userDatabase.getClient();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IUploadDatabaseClient#store(edu.asu.giles.core
     * .impl.Upload)
     */
    @Override
    public IUpload store(IUpload upload) {
        client.store(upload);
        client.commit();
        return upload;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IUploadDatabaseClient#getUpload(java.lang.String
     * )
     */
    @Override
    public IUpload getUpload(String id) {
        IUpload upload = new Upload(id);
        return queryByExample(upload);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IUploadDatabaseClient#getUploadsForUser(java
     * .lang.String)
     */
    @Override
    public List<IUpload> getUploadsForUser(String username) {
        IUpload upload = new Upload();
        upload.setUsername(username);
        return getFilesByExample(upload);
    }

    @Override
    public List<IUpload> getUploadsForUser(String username, int page,
            int pageSize, String sortBy, int sortDirection) {
        Query query = client.query();
        query.constrain(Upload.class);
        query.descend("username").constrain(username);

        try {
            Field sortField = Upload.class.getDeclaredField(sortBy);
            sortField.setAccessible(true);

            query.sortBy(new Comparator() {

                @Override
                public int compare(Object o1, Object o2) {
                    Object o1FieldContent;
                    Object o2FieldContent;
                    try {
                        if (sortDirection == IUploadDatabaseClient.ASCENDING) {
                            o1FieldContent = sortField.get(o1);
                            o2FieldContent = sortField.get(o2);
                        } else {
                            o2FieldContent = sortField.get(o1);
                            o1FieldContent = sortField.get(o2);
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        logger.error("Error accessing field.", e);
                        return 0;
                    }

                    if (sortBy.endsWith("Date")) {
                        ZonedDateTime date1 = ZonedDateTime
                                .parse(o1FieldContent.toString());
                        ZonedDateTime date2 = ZonedDateTime
                                .parse(o2FieldContent.toString());
                        return date1.compareTo(date2);
                    }
                    if (o1FieldContent instanceof Integer) {
                        return ((Integer) o1FieldContent)
                                .compareTo((Integer) o2FieldContent);
                    }
                    return o1FieldContent.toString().compareTo(
                            o2FieldContent.toString());
                }
            });
        } catch (NoSuchFieldException | SecurityException e) {
            logger.error("Couldn't sort list.", e);
            return null;
        }

        List<IUpload> allResults = query.execute(); // getUploadsForUser(username);
        int startIndex = (page - 1) * pageSize;
        int endIndex = startIndex + pageSize - 1;
        if (endIndex > allResults.size() - 1) {
            endIndex = allResults.size() - 1;
        }
        return allResults.subList(startIndex, endIndex);
    }

    private IUpload queryByExample(IUpload upload) {
        ObjectSet<Upload> uploads = client.queryByExample(upload);
        if (uploads != null && uploads.size() > 0) {
            return uploads.get(0);
        }
        return null;
    }

    private List<IUpload> getFilesByExample(IUpload upload) {
        ObjectSet<Upload> uploads = client.queryByExample(upload);
        List<IUpload> results = new ArrayList<IUpload>();
        for (IUpload u : uploads) {
            results.add(u);
        }
        return results;
    }

    @Override
    protected String getIdPrefix() {
        return "UP";
    }

    @Override
    protected Object getById(String id) {
        return getUpload(id);
    }

}
