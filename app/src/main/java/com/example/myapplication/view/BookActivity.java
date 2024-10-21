package com.example.myapplication.view;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.BookAdapter;
import com.example.myapplication.databinding.ActivityBookBinding;
import com.example.myapplication.model.BookSubModel;
import com.example.myapplication.repository.BookRepository;
import com.example.myapplication.repository.Responce;
import com.example.myapplication.util.NetworkUtils;
import com.example.myapplication.viewmodel.BookViewModel;
import com.example.myapplication.viewmodel.BookViewModelFactory;

import java.util.List;
import java.util.Objects;

public class BookActivity extends AppCompatActivity implements BookAdapter.OnClickListener {

    private ActivityBookBinding binding;
    private Context context;
    private List<BookSubModel> list;
    private int pos;
    private AlertDialog loaderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        initUi();
        initClickEvent();

    }

    //* init ui *//
    private void initUi() {
        callBookApi();
    }

    //* call book api *//
    private void callBookApi() {
        BookRepository repository = new BookRepository(context);
        BookViewModel viewModel = new ViewModelProvider(this,
                new BookViewModelFactory(repository))
                .get(BookViewModel.class);

        viewModel.getBookLiveData().observe(this, new Observer<Responce<List<BookSubModel>>>() {
            @Override
            public void onChanged(Responce<List<BookSubModel>> listResponce) {
                switch (listResponce.status) {
                    case INTERNET:
                        Toast.makeText(context, getString(R.string.no_internet_snackbar_text), Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        binding.bookProgressbar.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        setRecyclerView(listResponce.data);
                        break;
                    case FAIL:
                        Toast.makeText(context, getString(R.string.request_fail_err), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    //* set recycler view *//
    private void setRecyclerView(List<BookSubModel> data) {
        this.list = data;
        binding.bookRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        BookAdapter adapter = new BookAdapter(data, this, this::onDownload);
        binding.bookRecyclerView.setAdapter(adapter);
        binding.bookProgressbar.setVisibility(View.GONE);
    }

    //* init click event *//
    private void initClickEvent() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
    }

    public void downloadFile(String url) {
        String DownloadUrl = url;
        loaderDialog = NetworkUtils.createLoaderAlertDialog(this);
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request1.setDescription(list.get(pos).getbName());
        request1.setTitle(list.get(pos).getaName()+".pdf");
        request1.setVisibleInDownloadsUi(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request1.allowScanningByMediaScanner();
            request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        request1.setDestinationInExternalFilesDir(getApplicationContext(), "/File", list.get(pos).getaName());

        DownloadManager manager1 = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Objects.requireNonNull(manager1).enqueue(request1);
        if (DownloadManager.STATUS_SUCCESSFUL == 8) {
            Toast.makeText(context, "Download Success", Toast.LENGTH_SHORT).show();
            loaderDialog.dismiss();
        }
    }

    @Override
    public void onDownload(int position) {
        this.pos = position;
        downloadFile("https://event.sevenstepsschool.org" + list.get(position).getFile());
    }
}