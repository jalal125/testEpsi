package com.simplecity.amp_library.model;

import android.content.Context;
import com.simplecity.amp_library.R;
import java.io.File;

public class ArtworkModel {

    private static final String TAG = "ArtworkModel";

    @ArtworkProvider.Type
    public int type;

    private File file;

    public ArtworkModel(@ArtworkProvider.Type int type, File file) {
        this.type = type;
        this.file = file;
    }

    public static String getTypeString(Context context, @ArtworkProvider.Type int type) {
        switch (type) {
            case ArtworkProvider.Type.MEDIA_STORE:
                return context.getString(R.string.artwork_type_media_store);
            case ArtworkProvider.Type.TAG:
                return context.getString(R.string.artwork_type_tag);
            case ArtworkProvider.Type.FOLDER:
                return "Folder";
            case ArtworkProvider.Type.REMOTE:
                return context.getString(R.string.artwork_type_internet);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArtworkModel that = (ArtworkModel) o;

        if (type != that.type) return false;
        return file != null ? file.equals(that.file) : that.file == null;
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }
}