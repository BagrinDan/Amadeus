package org.example.amadeus.service.interfaces;

import org.example.amadeus.enums.Emotions;
import org.example.amadeus.utils.CharacterFrames;

public interface SpriteService {
    CharacterFrames getFramesForEmotion(Emotions emotion);
}
