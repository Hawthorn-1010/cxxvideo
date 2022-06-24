package com.hzy.cxxvideo.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.utils
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/15 9:32
 **/
public class FFmpegTest {
    private String ffmpegEXE;

    public FFmpegTest(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convert(String from, String to) throws IOException {

        List<String> command = new ArrayList<>();

        command.add(ffmpegEXE);
        command.add("-i");
        command.add(from);
        command.add(to);

        for (String c:
             command) {
            System.out.print(c + " ");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Process process = processBuilder.start();

        InputStream inputStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String s = "";
        String ss = "";
        while((s = bufferedReader.readLine()) != null) {
            ss += s;
        }

        System.out.println(ss);

        if(bufferedReader != null) {
            bufferedReader.close();;
        }

        if(inputStreamReader != null) {
            inputStreamReader.close();
        }

        if(inputStream != null) {
            inputStream.close();
        }


    }

    public static void main(String[] args) {
        try {
            FFmpegTest ffmpegTest = new FFmpegTest("D:\\Projects\\ffmpeg\\bin\\ffmpeg.exe");
            ffmpegTest.convert("D:\\Projects\\ffmpeg\\bin\\early.mp4","D:\\Projects\\ffmpeg\\bin\\snow.avi");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
