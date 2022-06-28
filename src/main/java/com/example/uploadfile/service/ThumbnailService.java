package com.example.uploadfile.service;

import java.io.File;
import java.io.IOException;

public interface ThumbnailService {
    File downScaleImage(String source, String destination) throws IOException;
}
