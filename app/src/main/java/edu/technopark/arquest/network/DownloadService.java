package edu.technopark.arquest.network;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import edu.technopark.arquest.MainActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.technopark.arquest.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class DownloadService extends IntentService {
    private int questId;
    public static final String fileNameStubs = "ujkdfvkj.zip";
    private static final String notificationTitle = "Скачивание";
    private static final String notificationText = "Скачивание квеста";
    private static final String downloading = "Загрузка квеста";
    private static final String downloaded = "Квест загружен";


    public DownloadService() {
        super("Download Service");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasExtra(getResources().getString(R.string.quest_id))) {
                questId = intent.getIntExtra(getResources().getString(R.string.quest_id), 0);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;

    @Override
    protected void onHandleIntent(Intent intent) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_file_download_black_24dp)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
        initDownload();
    }

    private void initDownload(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sample-videos.com/")//stubs for testing
                .build();

        LoaderService retrofitInterface = retrofit.create(LoaderService.class);

        Call<ResponseBody> request = retrofitInterface.downloadFile();
        try {
            downloadFile(request.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(ResponseBody body) throws IOException {
        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileNameStubs);
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {
            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);
            download.setId(questId);

            if (currentTime > 1000 * timeCount) {
                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        onDownloadComplete();
        output.flush();
        output.close();
        bis.close();

    }

    private void sendNotification(Download download) {
        sendIntent(download);
        notificationBuilder.setProgress(100,download.getProgress(),false);
        notificationBuilder.setContentText(downloading + download.getCurrentFileSize() +"/"+totalFileSize +" MB");
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendIntent(Download download) {
        Intent intent = new Intent(MainActivity.MESSAGE_PROGRESS);
        intent.putExtra(getResources().getString(R.string.download),download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete() {
        Download download = new Download();
        download.setId(questId);
        download.setProgress(100);
        sendIntent(download);
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0,0,false);
        notificationBuilder.setContentText(downloaded);
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}
