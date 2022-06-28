package com.example.uploadfile.service.impl;

import com.example.uploadfile.service.ThumbnailService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class ThumbnailServiceImpl implements ThumbnailService {
    @Override
    public File downScaleImage(String source, String destination) throws IOException {
        File targetFile = new File(destination);
        targetFile.getParentFile().mkdirs();
        BufferedImage img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        img.createGraphics().drawImage(ImageIO.read(new File(source)).getScaledInstance(300, 300, Image.SCALE_SMOOTH),0,0,null);
        ImageIO.write(img, "jpg", targetFile);
        return targetFile;
    }
}
