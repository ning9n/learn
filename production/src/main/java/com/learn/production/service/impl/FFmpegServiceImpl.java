package com.learn.production.service.impl;

import com.learn.production.service.FFmpegService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class FFmpegServiceImpl implements FFmpegService {
    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    /**
     * 获取视频时长
     *
     * @param videoPath 视频路径
     * @return 时长
     */
    @Override
    public String getVideoDuration(String videoPath) throws IOException, InterruptedException {
        //调用ffmpeg
        ProcessBuilder builder = new ProcessBuilder(
                ffmpegPath, "-i", videoPath
        );
        builder.redirectErrorStream(true);
        Process process = builder.start();
        //使用 BufferedReader 读取 FFmpeg 的输出日志
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        //正则匹配时长信息：
        Pattern pattern = Pattern.compile("Duration: (\\d{2}:\\d{2}:\\d{2}\\.\\d{2})");
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                // 关闭进程
                process.destroy();
                return matcher.group(1);
            }
        }
        log.info("获取视频时长失败");
        process.waitFor();
        throw new IOException("获取视频时长失败");
    }

    /**
     * 获取视频封面
     * @param videoPath 视频路径
     * @param thumbnailPath 封面路径
     * @throws IOException 获取失败
     */
    @Override
    public void extractVideoThumbnail(String videoPath, String thumbnailPath) throws IOException {
        // 使用FFmpeg提取第一秒的帧作为封面
        /*
        ffmpeg"：FFmpeg 的可执行文件名。
        "-i"：指定输入文件。
        videoPath：输入视频文件的路径。
        "-ss"：指定截取的时间点，这里是 00:00:01（即第1秒）。
        "-vframes"：指定提取的帧数，这里是 1 帧。
        "-q:v"：设置输出图片的质量，值为 2 表示较高的质量。
        thumbnailPath：输出封面图片的保存路径。
         */
        ProcessBuilder builder = new ProcessBuilder(
                ffmpegPath, "-i", videoPath, "-ss", "00:00:01", "-vframes", "1",
                "-q:v", "2", thumbnailPath // 设置JPEG质量
        );
        builder.redirectErrorStream(true);
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg进程退出状态码非零: " + exitCode + "\n输出日志: " + output.toString());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("FFmpeg进程被中断", e);
        }
        if (!new java.io.File(thumbnailPath).exists()) {
            throw new IOException("封面文件未生成: " + thumbnailPath);
        }

    }


}
