package com.kabouzeid.trebl.ui.fragments.mainactivity.library.pager;

import android.os.Bundle;
import androidx.loader.app.LoaderManager;

import com.kabouzeid.trebl.ui.fragments.AbsMusicServiceFragment;
import com.kabouzeid.trebl.ui.fragments.mainactivity.library.LibraryFragment;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AbsLibraryPagerFragment extends AbsMusicServiceFragment {

    /* http://stackoverflow.com/a/2888433 */
    @Override
    public LoaderManager getLoaderManager() {
        return getParentFragment().getLoaderManager();
    }

    public LibraryFragment getLibraryFragment() {
        return (LibraryFragment) getParentFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
