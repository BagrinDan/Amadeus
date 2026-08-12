package org.example.amadeus.service;

import org.example.amadeus.service.interfaces.EmbeddedLlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;


public class EmbeddedLlmServiceImpl implements EmbeddedLlmService {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedLlmServiceImpl.class);
    private Process demonProcess;
    private final int port;

    public EmbeddedLlmServiceImpl(int port){
        this.port = port;
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopDemon));
    }

    public void startDemon(String modelPath) {
        log.info("[DEBUG] Starting demon...");

        try{
            if(isDemonRunning()) {
                log.warn("[DEBUG] Demon is already started. Stoping previous process...");
                stopDemon();
            }

            File binaryFile = prepareExecutable();

            log.info("[DEBUG] Building demon...");
            ProcessBuilder pb = new ProcessBuilder(
                    binaryFile.getAbsolutePath(),
                    "-m", modelPath,
                    "--port", String.valueOf(port),
                    "-c", "2048",
                    "-ngl", "99" // задействует Vulkan GPU
            );

            pb.inheritIO();

            this.demonProcess = pb.start();

            Runtime.getRuntime().addShutdownHook(new Thread(this::stopDemon));

            log.warn("[DEBUG] Demon process started");
        } catch (Exception e){
            log.error("[ERROR]: Error in starting demon: {}", String.valueOf(e));
        }
    }

    public void stopDemon() {
        log.warn("[DEBUG] Stoping demon...");
        if (demonProcess != null && demonProcess.isAlive()) {
            demonProcess.destroyForcibly();
        }
    }

    public boolean isDemonRunning() {
        log.info("[DEBUG] Checking if demon is running..");
        return demonProcess != null && demonProcess.isAlive();
    }

    public File prepareExecutable() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");
        String osFolder = isWindows ? "win" : "linux";
        String binaryName = isWindows ? "llama-server.exe" : "llama-server";

        File tempDir = new File(System.getProperty("java.io.tmpdir"), "amadeus-bin");
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            log.error("Failed to create directory: {}", tempDir.getAbsolutePath());
        }

        String resourcePath = "/bin/" + osFolder;

        try {
            copyResourceFolder(resourcePath, tempDir);
        } catch (Exception e) {
            throw new IOException("Failed to extract binaries from " + resourcePath, e);
        }

        File targetBinary = new File(tempDir, binaryName);

        if (!targetBinary.exists()) {
            throw new FileNotFoundException("Executable " + binaryName + " not found in " + tempDir.getAbsolutePath());
        }

        if (!isWindows) {

            targetBinary.setExecutable(true);
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.setReadable(true, false);
                    file.setExecutable(true, false);
                }
            }
        }

        return targetBinary;
    }

    private void copyResourceFolder(String resourceFolderPath, File targetFolder) throws Exception {
        URL url = getClass().getResource(resourceFolderPath);
        if (url == null) {
            throw new FileNotFoundException("Resource path not found: " + resourceFolderPath);
        }

        URI uri = url.toURI();

        if ("jar".equals(uri.getScheme())) {
            // Если запуск происходит из собраного JAR файла
            try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                Path remotePath = fileSystem.getPath(resourceFolderPath);
                copyPathToDirectory(remotePath, targetFolder);
            }
        } else {
            // Если запуск из IDE (обычные файлы на диске)
            Path localPath = Paths.get(uri);
            copyPathToDirectory(localPath, targetFolder);
        }
    }

    private void copyPathToDirectory(Path sourcePath, File targetFolder) throws IOException {
        try (Stream<Path> stream = Files.walk(sourcePath, 1)) {
            stream.filter(path -> !path.equals(sourcePath)).forEach(path -> {
                try {
                    String fileName = path.getFileName().toString();
                    File targetFile = new File(targetFolder, fileName);

                    // Скопировать/перезаписать файл
                    Files.copy(path, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.error("Failed to copy resource file: {}", path, e);
                }
            });
        }
    }
}
