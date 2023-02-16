package com.leo.cse.frontend.editor.selectors;

import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.util.ArrayUtils;
import com.leo.cse.util.Dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SongSelectionDialog {
    private final ProfileManager profileManager;

    public SongSelectionDialog(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    public void select() {
        final List<SongListItem> songs = getSongs();

        if (songs.isEmpty()) {
            return;
        }

        final SongListItem current = getCurrentSong();

        final SongListItem selected = Dialogs.showSelectionDialog("Select a song",
                songs.toArray(new SongListItem[0]),
                current);

        if (selected == null || selected.equals(current)) {
            return;
        }

        profileManager.setField(ProfileFields.FIELD_SONG, selected.songId);
    }

    private SongListItem getCurrentSong() {
        final int currentSongId = profileManager.getIntField(ProfileFields.FIELD_SONG);
        final String[] songs = profileManager.getCurrentMCI().getSongNames();

        final String name = ArrayUtils.getOrDefault(songs, currentSongId, "");
        return new SongListItem(currentSongId, name);
    }

    private List<SongListItem> getSongs() {
        final List<SongListItem> maps = new ArrayList<>();
        final String[] songs = profileManager.getCurrentMCI().getSongNames();

        for (int i = 0; i < songs.length; i++) {
            maps.add(new SongListItem(i, songs[i]));
        }

        return maps;
    }

    private static class SongListItem implements Comparable<SongListItem> {
        final int songId;
        final String name;

        private SongListItem(int songId, String name) {
            this.songId = songId;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SongListItem item = (SongListItem) o;

            if (songId != item.songId) return false;
            return Objects.equals(name, item.name);
        }

        @Override
        public int hashCode() {
            int result = songId;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return String.format("%d - %s", songId, name);
        }

        @Override
        public int compareTo(SongListItem o) {
            return name.compareTo(o.name);
        }
    }
}
