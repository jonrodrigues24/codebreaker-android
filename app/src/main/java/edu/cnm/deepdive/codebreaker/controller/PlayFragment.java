package edu.cnm.deepdive.codebreaker.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.viewmodel.GameViewModel;
import edu.cnm.deepdive.codebreaker.viewmodel.HomeViewModel;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public class  PlayFragment extends Fragment implements InputFilter {

  private static final String ILLEGAL_CHARACTERS_FORMAT = "[^%s]+";
  private FragmentPlayBinding binding;
  private GameViewModel viewModel;
  private int codelength;
  private String pool;
  private String illegalCharacters;


  @Override
  public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentPlayBinding.inflate(inflater, container, false);
    binding.submit.setOnClickListener(v -> {
      /*, TODO Submit guess to viewmodel */
      viewModel.submitGuess(binding.guess.getText().toString().trim().toUpperCase(Locale.ROOT));
      binding.guess.setText("");
    });
    binding.guess.setFilters(new InputFilter[]{this});
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view,
      @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(getActivity()).get(GameViewModel.class);
    viewModel.getGame().observe(getViewLifecycleOwner(), this::update);
    }

  private void update(Game game) {
    /* TODO Update game display prettier */
    codelength = game.getLength();
    pool = game.getPool();
    illegalCharacters = String.format(ILLEGAL_CHARACTERS_FORMAT, pool);
    if (game.isSolved()) {
      binding.guess.setEnabled(false);
      binding.submit.setEnabled(false);
    } else {
      binding.guess.setEnabled(true);
      enforceSubmitConditions(binding.guess.length());
    }
    SimpleGuessAdapter adapter = new SimpleGuessAdapter(getContext(), game.getGuesses());
    binding.guessList.setAdapter(adapter);
  }

  @Override
  public void onCreateOptionsMenu(@NonNull @NotNull Menu menu,
      @NonNull @NotNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.play_options, menu);
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
    boolean handled = true;
    //noinspection SwitchStatementWithTooFewBranches
    switch (item.getItemId()) {
      case R.id.new_game_option:
        viewModel.startGame();
        break;
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }



  private void enforceSubmitConditions(int newLength) {
    binding.submit.setEnabled(newLength == codelength);
  }

  @Override
  public CharSequence filter(CharSequence source, int sourceStart, int sourceEnd,
      Spanned dest, int destStart, int destEnd) {
    String modifiedSource = source
        .subSequence(sourceStart, sourceEnd)
        .toString()
        .toUpperCase()
        .replaceAll(illegalCharacters, "");
    StringBuilder builder = new StringBuilder(dest);
    builder.replace(destStart, destEnd, modifiedSource);
    if (builder.length() > codelength) {
      modifiedSource = modifiedSource.substring(0, modifiedSource.length() - (builder.length() - codelength));
    }
    int newLength = dest.length() - (destEnd - destStart) + modifiedSource.length();
    enforceSubmitConditions(newLength);
    return modifiedSource;
  }
}