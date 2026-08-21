package org.example.amadeus.dto.response;


import org.example.amadeus.utils.CharacterFrames;


public record InteractionResult(
        String text,
        CharacterFrames frames
) {}
