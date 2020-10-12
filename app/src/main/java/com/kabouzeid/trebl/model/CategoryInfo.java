package com.kabouzeid.trebl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.kabouzeid.trebl.R;

public class CategoryInfo implements Parcelable {
    public Category category;
    public boolean visible;

    public CategoryInfo(Category category, boolean visible) {
        this.category = category;
        this.visible = visible;
    }

    private CategoryInfo(Parcel source) {
        category = (Category) source.readSerializable();
        visible = source.readInt() == 1;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(category);
        dest.writeInt(visible ? 1 : 0);
    }

    public static final Parcelable.Creator<CategoryInfo> CREATOR = new Parcelable.Creator<CategoryInfo>() {
        public CategoryInfo createFromParcel(Parcel source) {
            return new CategoryInfo(source);
        }

        public CategoryInfo[] newArray(int size) {
            return new CategoryInfo[size];
        }
    };

    public enum Category {
        //boin empty tabs icon
        SONGS(R.string.emptystring),
        ALBUMS(R.string.emptystring),
        ARTISTS(R.string.emptystring),
        GENRES(R.string.emptystring),
        PLAYLISTS(R.string.emptystring),
        MORE(R.string.emptystring);

        public final int stringRes;

        Category(int stringRes) {
            this.stringRes = stringRes;
        }
    }
}
