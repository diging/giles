package edu.asu.giles.service.ocr;

import java.util.concurrent.Future;

public interface IOCRService {

    public abstract Future<String> ocrImage(String imageFile);

}