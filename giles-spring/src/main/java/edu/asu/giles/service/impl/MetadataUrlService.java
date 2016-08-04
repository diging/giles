package edu.asu.giles.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.giles.core.IFile;
import edu.asu.giles.service.IMetadataUrlService;

/**
 * Class that generate callback urls to Jars (or potentially any other
 * metadata repository.
 * The following properties can be used:
 * <ul>
 *  <li>{giles} - Giles url</li>
 *  <li>{fileId} - Id of a provided file</li>
 * </ul>
 * @author jdamerow
 *
 */
@PropertySource("classpath:/config.properties")
@Service
public class MetadataUrlService implements IMetadataUrlService {
    
    @Value("${jars_url}")
    private String jarsUrl;
    
    @Value("${jars_file_url}")
    private String jarsFileUrl;
    
    @Value("${giles_url}")
    private String gilesUrl;
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IMetadataUrlService#getUploadCallback()
     */
    @Override
    public String getUploadCallback() {
        return jarsUrl;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IMetadataUrlService#getFileLink(edu.asu.giles.core.IFile)
     */
    @Override
    public String getFileLink(IFile file) {
        return jarsUrl + jarsFileUrl.replace("{giles}", gilesUrl).replace("{fileId}", file.getId());
    }
}
