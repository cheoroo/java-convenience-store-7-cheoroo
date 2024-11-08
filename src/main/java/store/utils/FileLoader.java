package store.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

public class FileLoader {

    public static void loadFile(String filePath, Consumer<String> lineProcessor) {
        try (BufferedReader reader = createBufferedReader(filePath)) {
            reader.readLine();
            processLines(reader, lineProcessor);
        } catch (Exception e) {
            throw new RuntimeException(filePath + " 파일을 로드하는 중 오류 발생", e);
        }
    }

    private static BufferedReader createBufferedReader(String filePath) {
        InputStream inputStream = Objects.requireNonNull(FileLoader.class.getResourceAsStream(filePath),
                "파일 경로가 유효하지 않습니다: " + filePath);
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    private static void processLines(BufferedReader reader, Consumer<String> lineProcessor) throws Exception {
        String line;
        while ((line = reader.readLine()) != null) {
            lineProcessor.accept(line);
        }
    }
}
