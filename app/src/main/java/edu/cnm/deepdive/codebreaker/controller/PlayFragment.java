package edu.cnm.deepdive.codebreaker.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.codebreaker.R;
import edu.cnm.deepdive.codebreaker.adapter.SimpleGuessAdapter;
import edu.cnm.deepdive.codebreaker.databinding.FragmentPlayBinding;
import edu.cnm.deepdive.codebreaker.viewmodel.GameViewModel;
import edu.cnm.deepdive.codebreaker.viewmodel.HomeViewModel;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public class  PlayFragment extends Fragment {

  private FragmentPlayBinding binding;
  private GameViewModel viewModel;


  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentPlayBinding.inflate(inflater, container, false);
    binding.submit.setOnClickListener(v -> {
      /*, TODO Submit guess to viewmodel */
      viewModel.submitGuess(binding.guess.getText().toString().trim().toUpperCase(Locale.ROOT));
      binding.guess.setText("");
    });
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view,
      @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(getActivity()).get(GameViewModel.class);
    viewModel.getGame().observe(getViewLifecycleOwner(), (game) -> {
      /* TODO Update game display prettier */
      if (game.isSolved()) {
        binding.guess.setEnabled(false);
        binding.submit.setEnabled(false);
      } else {
        binding.guess.setEnabled(true);
        binding.submit.setEnabled(true);
      }
      SimpleGuessAdapter adapter = new SimpleGuessAdapter(getContext(), game.getGuesses());
      binding.guessList.setAdapter(adapter);
    });
  }
}