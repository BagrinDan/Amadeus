package org.example.amadeus.service;

import javafx.scene.image.Image;
import org.example.amadeus.enums.Emotions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Random;

public class SpriteService {

    private static final Logger log = LoggerFactory.getLogger(SpriteService.class);
    private final Random random = new Random();

    public record CharacterFrames(Image idle, Image talk1, Image talk2) {}

    public CharacterFrames getFramesForEmotion(Emotions emotion) {
        String emotionFolder = emotion.name();
        String baseName = emotionFolder.toLowerCase();

        // 1. Сначала пробуем загрузить из структуры с вариациями (_1 / _2)
        int variation = random.nextInt(2) + 1; // 1 или 2
        CharacterFrames frames = loadFromVariationFolder(emotionFolder, variation);

        if (frames != null) {
            return frames;
        }

        // Если выпала вариация 2, но ее нет — пробуем вариацию 1
        if (variation == 2) {
            frames = loadFromVariationFolder(emotionFolder, 1);
            if (frames != null) {
                return frames;
            }
        }

        // 2. Если подпапок _1/_2 нет, загружаем напрямую из папки эмоции
        // Пути вида: /sprites/EAGER/eager.png
        frames = loadDirectlyFromEmotionFolder(emotionFolder, baseName);
        if (frames != null) {
            return frames;
        }

        // 3. Fallback на DEFAULT, если ничего не нашли
        log.warn("[SpriteService] Sprite not found for emotion: {}. Fallback to DEFAULT", emotionFolder);
        CharacterFrames defaultFrames = loadFromVariationFolder("DEFAULT", 1);
        return defaultFrames != null ? defaultFrames : loadDirectlyFromEmotionFolder("DEFAULT", "default");
    }


    private CharacterFrames loadFromVariationFolder(String emotionFolder, int variation) {
        String subFolder = emotionFolder + "_" + variation;
        String fileName = emotionFolder.toLowerCase() + (variation == 2 ? "2" : "");

        String pathIdle  = String.format("/sprites/%s/%s/%s.png", emotionFolder, subFolder, fileName);
        String pathTalk1 = String.format("/sprites/%s/%s/%s_talk.png", emotionFolder, subFolder, fileName);
        String pathTalk2 = String.format("/sprites/%s/%s/%s_talk2.png", emotionFolder, subFolder, fileName);

        return buildFramesIfExist(pathIdle, pathTalk1, pathTalk2);
    }

    private CharacterFrames loadDirectlyFromEmotionFolder(String emotionFolder, String baseName) {
        String pathIdle  = String.format("/sprites/%s/%s.png", emotionFolder, baseName);
        String pathTalk1 = String.format("/sprites/%s/%s_talk.png", emotionFolder, baseName);
        String pathTalk2 = String.format("/sprites/%s/%s_talk2.png", emotionFolder, baseName);

        return buildFramesIfExist(pathIdle, pathTalk1, pathTalk2);
    }

    private CharacterFrames buildFramesIfExist(String pathIdle, String pathTalk1, String pathTalk2) {
        Image idle = loadImage(pathIdle);
        if (idle == null) {
            return null; // Главный файл не найден
        }

        Image talk1 = loadImage(pathTalk1);
        Image talk2 = loadImage(pathTalk2);

        // Если кадров рта нет, подставляем idle как фоллбек
        return new CharacterFrames(
                idle,
                talk1 != null ? talk1 : idle,
                talk2 != null ? talk2 : idle
        );
    }

    private Image loadImage(String path) {
        InputStream stream = getClass().getResourceAsStream(path);
        return stream != null ? new Image(stream) : null;
    }
}
