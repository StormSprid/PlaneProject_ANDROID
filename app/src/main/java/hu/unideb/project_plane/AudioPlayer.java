package hu.unideb.project_plane;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;
    private String currentPlaying = ""; // Текущий проигрываемый файл

    /**
     * Воспроизводит аудио по имени файла (из res/raw), прерывая предыдущее.
     *
     * @param context  Контекст приложения
     * @param fileName Имя файла (без расширения)
     */
    public void playAudio(Context context, String fileName) {
        if (fileName.equals(currentPlaying)) {
            return; // Уже играет этот звук, не перезапускаем
        }

        stopAudio(); // Останавливаем предыдущее аудио

        int resId = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());

        if (resId == 0) {
            throw new IllegalArgumentException("Файл " + fileName + " не найден в res/raw/");
        }

        currentPlaying = fileName;

        // Запускаем аудио в фоновом потоке
        new PlayAudioTask(context, resId).execute();
    }

    /**
     * Фоновая задача для запуска аудио
     */
    private class PlayAudioTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private int resId;

        public PlayAudioTask(Context context, int resId) {
            this.context = context;
            this.resId = resId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mediaPlayer = MediaPlayer.create(context, resId);
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
                currentPlaying = "";
            });
            mediaPlayer.start();
            return null;
        }
    }

    /**
     * Останавливает текущее аудио, если оно играет.
     */
    public void stopAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            currentPlaying = "";
        }
    }
}
