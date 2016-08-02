package edu.asu.giles.service;

import edu.asu.giles.core.IFile;

public interface IMetadataUrlService {

    public abstract String getUploadCallback();

    public abstract String getFileLink(IFile file);

}