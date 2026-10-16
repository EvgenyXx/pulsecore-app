package ru.pulsecore.app.tournament.domain.enums;

import lombok.Getter;

@Getter
public enum GameStage {
    TOP_MESH_1("top_mesh_1"),
    TOP_MESH_2("top_mesh_2"),
    TOP_MESH_3("top_mesh_3"),
    LOWER_MESH_1("lower_mesh_1"),
    LOWER_MESH_2("lower_mesh_2"),
    LOWER_MESH_3("lower_mesh_3"),
    FINAL("final");

    private final String code;

    GameStage(String code) {
        this.code = code;
    }

    public static GameStage fromCode(String code) {
        for (GameStage s : values()) {
            if (s.code.equals(code)) return s;
        }
        return null;
    }
}