package CombinePDF;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryFiles {
    public static List<String> listFiles(String path) {
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            return walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
