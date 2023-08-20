package com.example.vertonowsky.admin;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class AdminService {


    /**
     * Get all paths of videos that can be used as topic media
     *
     * @return List containing all video paths
     */
    public List<String> getVideoList(String localVideoesPath) {
        File folder = new File(localVideoesPath);
        if (!folder.exists()) return new LinkedList<>();

        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) return null;

        return  Arrays.stream(listOfFiles).map(File::getName).toList();
    }

}
