package com.kabouzeid.gramophone.ui.fragments.mainactivity.library.pager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kabouzeid.gramophone.App;
import com.kabouzeid.gramophone.R;
import com.kabouzeid.gramophone.dialogs.ScanMediaFolderChooserDialog;
import com.kabouzeid.gramophone.ui.activities.MainActivity;
import com.kabouzeid.gramophone.ui.activities.PurchaseActivity;
import com.kabouzeid.gramophone.ui.activities.SettingsActivity;
import com.kabouzeid.gramophone.ui.fragments.mainactivity.folders.FoldersFragment;
import com.sofakingforever.stars.AnimatedStarsView;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

public class MoreFragment extends Fragment {
    private ConstraintLayout foldersButton,settingsButton,scanButton,twitterButton,instagramButton,shareButton,customButton;
    private AnimatedStarsView starsView;
    @Nullable
    MainActivity.MainActivityFragmentCallbacks currentFragment;

    public MoreFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MoreFragment newInstance(String param1, String param2) {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        foldersButton = view.findViewById(R.id.foldersButton);
        settingsButton = view.findViewById(R.id.settingsButton);
        scanButton = view.findViewById(R.id.scanButton);
        twitterButton = view.findViewById(R.id.twitterButton);
        instagramButton = view.findViewById(R.id.instagramButton);
        shareButton = view.findViewById(R.id.shareButton);
        customButton = view.findViewById(R.id.customButton);
        starsView = view.findViewById(R.id.stars);

        settingsButton.setOnClickListener(view1 -> new Handler().postDelayed(() -> startActivity(new Intent(getActivity(), SettingsActivity.class)), 200));

        scanButton.setOnClickListener(view13 -> new Handler().postDelayed(() -> {
            ScanMediaFolderChooserDialog dialog = ScanMediaFolderChooserDialog.create();
            dialog.show(getActivity().getSupportFragmentManager(), "SCAN_MEDIA_FOLDER_CHOOSER");
        }, 200));

        foldersButton.setOnClickListener(v -> {
            if (!App.isProVersion()) {
                Toast.makeText(getActivity(), R.string.folder_view_is_a_pro_feature, Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), PurchaseActivity.class));
            }else{
                setCurrentFragment(FoldersFragment.newInstance(getActivity()));
            }
        });

        customButton.setOnClickListener(view14 -> startActivity(new Intent(getActivity(), PurchaseActivity.class)));

        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.addCategory(Intent.CATEGORY_BROWSABLE);

        twitterButton.setOnClickListener(v -> {
            webIntent.setData(Uri.parse("https://twitter.com/TREBLMusic"));
            startActivity(webIntent);
        });

        instagramButton.setOnClickListener(v -> {
            webIntent.setData(Uri.parse("https://www.instagram.com/thevelocityvpn/"));
            startActivity(webIntent);
        });

        shareButton.setOnClickListener(v -> ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setChooserTitle("Share this app")
                .setText("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())
                .startChooser());

    }

    @Override
    public void onStart() {
        super.onStart();
        starsView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        starsView.onStop();
    }

    private void setCurrentFragment(@SuppressWarnings("NullableProblems") Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, null).commit();
        currentFragment = (MainActivity.MainActivityFragmentCallbacks) fragment;
    }
}