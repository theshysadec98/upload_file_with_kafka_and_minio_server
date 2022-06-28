package com.example.uploadfile.service;

import java.io.File;
import java.io.IOException;

public interface ImageDownloaderService {
    File downloadImage(String url, String destination) throws IOException;
}
