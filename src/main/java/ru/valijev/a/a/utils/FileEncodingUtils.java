package ru.valijev.a.a.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@UtilityClass
public class FileEncodingUtils {

    public String getFileByteContent(String str) {
        File f = new File(str);
        str = f.getName();

        ClassLoader classLoader = FileEncodingUtils.class.getClassLoader();
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource(str)).getFile());

        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.getEncoder().encodeToString(fileContent);
    }

}
