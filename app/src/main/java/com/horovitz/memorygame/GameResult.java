package com.horovitz.memorygame;

    public class GameResult {
        private int id;
        private String gameType;
        private int score;
        private int time;

        public GameResult(int id, String gameType, int score, int time) {
            this.id = id;
            this.gameType = gameType;
            this.score = score;
            this.time = time;
        }

        public int getId() {
            return id;
        }
        public int getTime() {
            return time;
        }
        public String getGameType() {
            return gameType;
        }

        public int getScore() {
            return score;
        }
    }


