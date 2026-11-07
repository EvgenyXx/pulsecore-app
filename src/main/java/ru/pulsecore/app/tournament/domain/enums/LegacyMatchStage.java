package ru.pulsecore.app.tournament.domain.enums;

public enum LegacyMatchStage {


    GROUP {
        @Override
        public boolean matches(String stage) {
            if (stage == null) return false;
            String s = normalize(stage);
            return LegacyStageConstants.GROUP_EN.equals(s) || LegacyStageConstants.GROUP_RU.equals(s);
        }
    },


    FINAL {
        @Override
        public boolean matches(String stage) {
            if (stage == null) return false;
            String s = normalize(stage);
            return LegacyStageConstants.FINAL_EN.equals(s) || LegacyStageConstants.FINAL_RU.equals(s);
        }
    },

    SEMI_FINAL {
        @Override
        public boolean matches(String stage) {
            if (stage == null) return false;
            String s = normalize(stage);
            return LegacyStageConstants.SEMI_EN.equals(s) || LegacyStageConstants.SEMI_RU.equals(s);
        }
    },

    THIRD_PLACE {
        @Override
        public boolean matches(String stage) {
            if (stage == null) return false;
            String s = normalize(stage);
            return LegacyStageConstants.THIRD_EN.equals(s) || LegacyStageConstants.THIRD_RU.equals(s);
        }
    };

    public abstract boolean matches(String stage);

    protected String normalize(String stage) {
        return stage.toLowerCase()
                .replace("\u00A0", "")
                .replaceAll("\\s+", "")
                .trim();
    }
}