package com.hzy.cxxvideo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.utils
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/15 9:32
 **/
public class MergeVideoAudio {
    private static final String ffmpegEXE = "D:\\Projects\\ffmpeg\\bin\\ffmpeg.exe";


    public static void convert(String fileSpace,String tempPath, String videoPath, String audioPath, double videoSeconds) throws IOException {

        // 要被这个正斜杠，反斜杠，转义气死
        String tempVideoPath = tempPath + "/temp.mp4";
//        videoPath = videoPath.replaceAll("/","\\\\");
//        tempVideoPath = tempVideoPath.replaceAll("/","\\\\");
//        audioPath = (fileSpace + audioPath).replaceAll("/","\\\\");
        audioPath = fileSpace + audioPath;

        // 消音处理
        List<String> command = new ArrayList<>();

        command.add(ffmpegEXE);
        command.add("-y");
        command.add("-i");
        command.add(videoPath);
        command.add("-vcodec");
        command.add("copy");
        command.add("-an");
        command.add(tempVideoPath);

        for (String c:
             command) {
            System.out.print(c + " ");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Process process = processBuilder.start();

        InputStream inputStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


        while(bufferedReader.readLine() != null) {
        }


        if(bufferedReader != null) {
            bufferedReader.close();;
        }

        if(inputStreamReader != null) {
            inputStreamReader.close();
        }

        if(inputStream != null) {
            inputStream.close();
        }

        // 视频音频合并处理
        List<String> command2 = new ArrayList<>();

        command2.add(ffmpegEXE);
        command2.add("-i");
        command2.add(tempVideoPath);
        command2.add("-i");
        command2.add(audioPath);
        command2.add("-t");
        command2.add(String.valueOf(videoSeconds));
        command2.add("-y");
        command2.add(videoPath);

        System.out.println(" ");

        for (String c:
                command2) {
            System.out.print(c + " ");
        }

        ProcessBuilder processBuilder2 = new ProcessBuilder(command2);

        Process process2 = processBuilder2.start();

        InputStream inputStream2 = process2.getErrorStream();
        InputStreamReader inputStreamReader2 = new InputStreamReader(inputStream2);
        BufferedReader bufferedReader2 = new BufferedReader(inputStreamReader2);


        while(bufferedReader2.readLine() != null) {
        }


        if(bufferedReader2 != null) {
            bufferedReader.close();;
        }

        if(inputStreamReader2 != null) {
            inputStreamReader.close();
        }

        if(inputStream2 != null) {
            inputStream.close();
        }


    }


}
