package com.kabouzeid.trebl.helper.menu;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.kabouzeid.trebl.App;
import com.kabouzeid.trebl.R;
import com.kabouzeid.trebl.dialogs.AddToPlaylistDialog;
import com.kabouzeid.trebl.dialogs.DeletePlaylistDialog;
import com.kabouzeid.trebl.dialogs.RenamePlaylistDialog;
import com.kabouzeid.trebl.helper.MusicPlayerRemote;
import com.kabouzeid.trebl.loader.PlaylistSongLoader;
import com.kabouzeid.trebl.misc.WeakContextAsyncTask;
import com.kabouzeid.trebl.model.AbsCustomPlaylist;
import com.kabouzeid.trebl.model.Playlist;
import com.kabouzeid.trebl.model.Song;
import com.kabouzeid.trebl.util.PlaylistsUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class PlaylistMenuHelper {
    public static boolean handleMenuClick(@NonNull AppCompatActivity activity, @NonNull final Playlist playlist, @NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                MusicPlayerRemote.openQueue(new ArrayList<>(getPlaylistSongs(activity, playlist)), 0, true);
                return true;
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(new ArrayList<>(getPlaylistSongs(activity, playlist)));
                return true;
            case R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(new ArrayList<>(getPlaylistSongs(activity, playlist)));
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistDialog.create(new ArrayList<>(getPlaylistSongs(activity, playlist))).show(activity.getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.action_rename_playlist:
                RenamePlaylistDialog.create(playlist.id).show(activity.getSupportFragmentManager(), "RENAME_PLAYLIST");
                return true;
            case R.id.action_delete_playlist:
                DeletePlaylistDialog.create(playlist).show(activity.getSupportFragmentManager(), "DELETE_PLAYLIST");
                return true;
            case R.id.action_save_playlist:
                new SavePlaylistAsyncTask(activity).execute(playlist);
                return true;
        }
        return false;
    }

    @NonNull
    private static List<? extends Song> getPlaylistSongs(@NonNull Activity activity, Playlist playlist) {
        return playlist instanceof AbsCustomPlaylist ?
                ((AbsCustomPlaylist) playlist).getSongs(activity) :
                PlaylistSongLoader.getPlaylistSongList(activity, playlist.id);
    }


    private static class SavePlaylistAsyncTask extends WeakContextAsyncTask<Playlist, String, String> {
        public SavePlaylistAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackground(Playlist... params) {
            try {
                return String.format(App.getInstance().getApplicationContext().getString(R.string.saved_playlist_to), PlaylistsUtil.savePlaylist(App.getInstance().getApplicationContext(), params[0]));
            } catch (IOException e) {
                e.printStackTrace();
                return String.format(App.getInstance().getApplicationContext().getString(R.string.failed_to_save_playlist), e);
            }
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, string, Toast.LENGTH_LONG).show();
            }
        }
    }
}
