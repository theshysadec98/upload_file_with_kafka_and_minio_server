package com.example.uploadfile.service.impl;

import com.example.uploadfile.service.ImageDownloaderService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;

@Service
public class ImageDownloaderServiceImpl implements ImageDownloaderService {

    @Override
    public File downloadImage(String url, String destination) throws IOException {
        try(BufferedInputStream img = new BufferedInputStream(new URL(url).openStream())){
            File targetFile = new File(destination);
            targetFile.getParentFile().mkdirs();
            try(OutputStream outStream = new FileOutputStream(targetFile)){
                outStream.write(img.readAllBytes());
                outStream.flush();
                return targetFile;
            }
        }
    }
}
