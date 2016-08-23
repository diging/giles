package edu.asu.giles.files.impl;

import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import edu.asu.giles.core.IFile;
import edu.asu.giles.core.impl.File;

public class FilesDatabaseClientTest {
    
    private final String FILE1_ID = "FILE1";
    private final String FILE2_ID = "FILE2";
    private final String UPLOAD_ID = "UP1";
    
    @Mock private ObjectContainer container;
    
    @Mock private ObjectSet<Object> objectSetFile1;
    @Mock private ObjectSet<Object> objectSetFiles;
    @Mock private ObjectSet<Object> objectSetEmpty;
    
    @Mock private Iterator<Object> mockedIterator;
    @Mock private Iterator<Object> mockedIteratorFiles;
    @Mock private Iterator<Object> mockedIteratorEmpty;
    
    @InjectMocks private FilesDatabaseClient filesDBClient;
    
    @Before
    public void init() {
        filesDBClient = new FilesDatabaseClient();
        MockitoAnnotations.initMocks(this);
        
        IFile file1 = new File();
        file1.setId(FILE1_ID);
        file1.setUploadId(UPLOAD_ID);
        
        IFile file2 = new File();
        file2.setId(FILE2_ID);
        file2.setUploadId(UPLOAD_ID);
        
        Mockito.when(objectSetFile1.size()).thenReturn(1);
        Mockito.when(objectSetFile1.get(0)).thenReturn(file1);
        Mockito.when(objectSetFile1.iterator()).thenReturn(mockedIterator);
        Mockito.when(mockedIterator.hasNext()).thenReturn(true, false);
        Mockito.when(mockedIterator.next()).thenReturn(file1);
        
        Mockito.when(objectSetFiles.size()).thenReturn(2);
        Mockito.when(objectSetFiles.get(0)).thenReturn(file1);
        Mockito.when(objectSetFiles.iterator()).thenReturn(mockedIteratorFiles);
        Mockito.when(mockedIteratorFiles.hasNext()).thenReturn(true, true, false);
        Mockito.when(mockedIteratorFiles.next()).thenReturn(file1, file2);
        
        Mockito.when(objectSetEmpty.size()).thenReturn(0);
        Mockito.when(objectSetEmpty.iterator()).thenReturn(mockedIteratorEmpty);
        Mockito.when(mockedIteratorEmpty.hasNext()).thenReturn(false);
    }
    
    @Test
    public void test_saveFile() {
        IFile file = new File();
        filesDBClient.saveFile(file);
        
        Mockito.verify(container).store(file);
        Mockito.verify(container).commit();
    }
    
    @Test
    public void test_getFileById_exists() {
        Mockito.when(container.queryByExample(Mockito.argThat(new FileIdArgumentMatcher()))).thenReturn(objectSetFile1);
        IFile file = filesDBClient.getFileById(FILE1_ID);
        Assert.assertNotNull(file);
        Assert.assertEquals(FILE1_ID, file.getId());
    }
    
    @Test
    public void test_getFileById_notExists() {
        Mockito.when(container.queryByExample(Mockito.argThat(new FileIdArgumentMatcher()))).thenReturn(objectSetEmpty);
        IFile file = filesDBClient.getFileById(FILE1_ID);
        Assert.assertNull(file);
    }
    
    @Test
    public void test_getFileByUploadId_exists() {
        Mockito.when(container.queryByExample(Mockito.argThat(new UploadIdArgumentMatcher()))).thenReturn(objectSetFiles);
        List<IFile> files = filesDBClient.getFileByUploadId(UPLOAD_ID);
        Assert.assertNotNull(files);
        Assert.assertEquals(2, files.size());
        Assert.assertEquals(UPLOAD_ID, files.get(0).getUploadId());
        Assert.assertEquals(UPLOAD_ID, files.get(1).getUploadId());
    }
    
    class FileIdArgumentMatcher extends ArgumentMatcher<IFile> {

        @Override
        public boolean matches(Object argument) {
            if (((IFile)argument).getId().equals(FILE1_ID)) {
                return true;
            }
            return false;
        }
    }
    
    class UploadIdArgumentMatcher extends ArgumentMatcher<IFile> {

        @Override
        public boolean matches(Object argument) {
            if (((IFile)argument).getUploadId().equals(UPLOAD_ID)) {
                return true;
            }
            return false;
        }
        
    }
}
