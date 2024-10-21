package com.example.myapplication.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.EventAdapter;
import com.example.myapplication.databinding.ActivityUpcomingBinding;
import com.example.myapplication.model.EventSubModel;
import com.example.myapplication.repository.Responce;
import com.example.myapplication.repository.UpcomingRepository;
import com.example.myapplication.viewmodel.UpComingViewModel;
import com.example.myapplication.viewmodel.UpComingViewModelFactory;

import java.io.Serializable;
import java.util.List;

public class UpcomingActivity extends AppCompatActivity implements EventAdapter.OnClickListener {

    private ActivityUpcomingBinding binding;
    private Context context;
    List<EventSubModel> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpcomingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        initUi();
        initClickEvent();

    }

    //* init ui *//
    private void initUi() {
        callUpcomingEventApi();
    }

    //* call event api *//
    private void callUpcomingEventApi() {
        UpcomingRepository repository = new UpcomingRepository(context);
        UpComingViewModel viewModel = new ViewModelProvider(this,
                new UpComingViewModelFactory(repository))
                .get(UpComingViewModel.class);

        viewModel.getUpComingLiveData().observe(this, new Observer<Responce<List<EventSubModel>>>() {
            @Override
            public void onChanged(Responce<List<EventSubModel>> listResponce) {
                switch (listResponce.status) {
                    case INTERNET:
                        Toast.makeText(context, getString(R.string.no_internet_snackbar_text), Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        binding.eventProgressbar.setVisibility(View.VISIBLE);
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

    //* set up recycler view *//
    private void setRecyclerView(List<EventSubModel> data) {
        this.data = data;
        binding.eventRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        EventAdapter adapter = new EventAdapter(data, this, this::onclick);
        binding.eventRecyclerView.setAdapter(adapter);
        binding.eventProgressbar.setVisibility(View.GONE);
    }

    //* init click event *//
    private void initClickEvent() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onclick(int position) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra("list", (Serializable) data);
        intent.putExtra("position", position);
        startActivity(intent);
    }
}