import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FFmpegTest {
    @Test
    public void test() throws Exception{
        String ffmpegPath="D:\\java_tools\\ffmpeg\\ffmpeg.exe";
        String videoPath="D:\\java_tools\\ffmpeg\\video\\437061562872627209\\video";
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
                System.out.println(matcher.group(1));
                return;
            }
        }
        System.out.println("获取视频时长失败");
        process.waitFor();
        throw new IOException("获取视频时长失败");
    }
}
