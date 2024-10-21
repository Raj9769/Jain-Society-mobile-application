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
import com.example.myapplication.databinding.ActivityEventBinding;
import com.example.myapplication.model.EventSubModel;
import com.example.myapplication.repository.EventRepository;
import com.example.myapplication.repository.Responce;
import com.example.myapplication.viewmodel.EventViewModel;
import com.example.myapplication.viewmodel.EventViewModelFactory;

import java.io.Serializable;
import java.util.List;

public class EventActivity extends AppCompatActivity implements EventAdapter.OnClickListener {

    private ActivityEventBinding binding;
    private Context context;
    List<EventSubModel> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        initUi();
        initClickEvent();
    }

    //* init ui *//
    private void initUi() {
        callEventApi();
    }

    //* call event api *//
    private void callEventApi() {
        EventRepository repository = new EventRepository(context);
        EventViewModel viewModel = new ViewModelProvider(this,
                new EventViewModelFactory(repository))
                .get(EventViewModel.class);

        viewModel.getEventLiveData().observe(this, new Observer<Responce<List<EventSubModel>>>() {
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