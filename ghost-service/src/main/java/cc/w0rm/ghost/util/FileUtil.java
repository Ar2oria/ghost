package cc.w0rm.ghost.util;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class FileUtil {
    private static final List<String> TEXT_LIST = new ArrayList<>();

    @PostConstruct
    public void init() {
        String s = readFile("templates/text.txt");
        String[] split = s.split("\n");
        TEXT_LIST.addAll(Arrays.asList(split));
    }

    @NotNull
    public static String readFile(String filePath) {
        String data = Strings.EMPTY;
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        try {
            byte[] bdata = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
            data = new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("IOException", e);
        }
        return data;
    }

    public static String getShakespeare(){
        int i = RandomUtil.randomInt(0, TEXT_LIST.size());
        return TEXT_LIST.get(i);
    }
}
