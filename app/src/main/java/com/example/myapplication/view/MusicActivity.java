package com.example.myapplication.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.EventAdapter;
import com.example.myapplication.adapter.MusicAdapter;
import com.example.myapplication.databinding.ActivityMusicBinding;
import com.example.myapplication.model.MusicSubModel;
import com.example.myapplication.model.SongModel;
import com.example.myapplication.repository.MusicRepository;
import com.example.myapplication.repository.Responce;
import com.example.myapplication.util.DownloadTask;
import com.example.myapplication.util.NetworkUtils;
import com.example.myapplication.util.OnClick;
import com.example.myapplication.util.StaticData;
import com.example.myapplication.viewmodel.MusicViewModel;
import com.example.myapplication.viewmodel.MusicViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MusicActivity extends AppCompatActivity {

    private ActivityMusicBinding binding;
    private Context context;
    private MediaPlayer mediaPlayer;
    private ImageView imgView;
    private int length;
    private List<MusicSubModel> musicSubModels;
    private int pos;
    private AlertDialog loaderDialog;
//    private List<SongModel> songModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMusicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        initUi();
        initClickEvent();

    }

    //* init ui *//
    private void initUi() {
//        songModels = new ArrayList<>();
//        songModels.add(new SongModel("music_1",R.raw.music_1));
//        songModels.add(new SongModel("music_1",R.raw.music_2));
//        songModels.add(new SongModel("music_1",R.raw.music_3));
//        songModels.add(new SongModel("music_1",R.raw.music_4));
        callMusicApi();
    }

    //* call event api *//
    private void callMusicApi() {
        MusicRepository repository = new MusicRepository(context);
        MusicViewModel viewModel = new ViewModelProvider(this,
                new MusicViewModelFactory(repository))
                .get(MusicViewModel.class);

        viewModel.getMusicLiveData().observe(this, new Observer<Responce<List<MusicSubModel>>>() {
            @Override
            public void onChanged(Responce<List<MusicSubModel>> listResponce) {
                switch (listResponce.status) {
                    case INTERNET:
                        Toast.makeText(context, getString(R.string.no_internet_snackbar_text), Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        binding.musicProgressbar.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        musicSubModels = listResponce.data;
                        setRecyclerView(listResponce.data);
                        break;
                    case FAIL:
                        Toast.makeText(context, getString(R.string.request_fail_err), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    //* set up recycler view *//
    private void setRecyclerView(List<MusicSubModel> data) {
        binding.musicRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        MusicAdapter adapter = new MusicAdapter(data, this, new MusicAdapter.OnClick() {
            @Override
            public void onDownload(int position) {
                    downloadFile("https://event.sevenstepsschool.org" + musicSubModels.get(position).getFile());


            }

            @Override
            public void saveClick(ImageView imageView, ImageView imageView1, int position) {
                imgView = imageView1;
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build());
                imageView1.setOnClickListener(view -> {
                    Handler handler = new Handler(Looper.getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            //Media player stop
                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
//                                mediaPlayer.release();
                                mediaPlayer = null;
                                imgView.setImageResource(R.drawable.play_icon);
                            } else {

                                //Media Player Play
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setAudioAttributes(
                                        new AudioAttributes.Builder()
                                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                                .build());
                                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse("https://event.sevenstepsschool.org" + data.get(position).getFile()));
                                imgView.setImageResource(R.drawable.pause_icon);
                                mediaPlayer.start();
                            }
                        }
                    };
                    handler.post(runnable);
                });

                imageView.setOnClickListener(view -> {
                    pos = position;
//                    NetworkUtils.setCustomIntent(MusicActivity.this,"https://event.sevenstepsschool.org" + data.get(position).getFile());
//                    new DownloadTask(context).execute("https://event.sevenstepsschool.org" + data.get(position).getFile());
//                    downloadFile(String.valueOf(songModels.get(position).getFile()));
                    downloadFile("https://event.sevenstepsschool.org" + data.get(position).getFile());
                });
            }
        });
        binding.musicRecyclerView.setAdapter(adapter);
        binding.musicProgressbar.setVisibility(View.GONE);
    }

    public void downloadFile(String url) {
        String DownloadUrl = url;
        loaderDialog = NetworkUtils.createLoaderAlertDialog(this);
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(url));
        request1.setTitle(musicSubModels.get(pos).getmName() + ".mp3");
        request1.setVisibleInDownloadsUi(true);
        request1.setDescription(musicSubModels.get(pos).getmName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            request1.allowScanningByMediaScanner();
            request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }
        request1.setDestinationInExternalFilesDir(getApplicationContext(), "/File", musicSubModels.get(pos).getmName());
//        request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, musicSubModels.get(pos).getmName());
        request1.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        DownloadManager manager1 = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager1.enqueue(request1);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        Cursor cursor = manager1.query(query);
        cursor.moveToFirst();
        if (DownloadManager.STATUS_SUCCESSFUL == 8) {
            Toast.makeText(context, "Download Success", Toast.LENGTH_SHORT).show();
            loaderDialog.dismiss();
        }
    }



    //* Activity Lifecycle *//
    @Override
    protected void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            length = mediaPlayer.getCurrentPosition();
            imgView.setImageResource(R.drawable.play_icon);
        }
        super.onPause();
    }

    @Override
    protected void onRestart() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.seekTo(length);
            imgView.setImageResource(R.drawable.pause_icon);
        }
        super.onRestart();
    }

    @Override
    protected void onStop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            imgView.setImageResource(R.drawable.play_icon);
        }
        super.onStop();
    }

    //* init click event *//
    private void initClickEvent() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
    }
}