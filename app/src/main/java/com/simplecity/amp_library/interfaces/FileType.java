package com.simplecity.amp_library.interfaces;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({FileType.PARENT, FileType.FOLDER, FileType.FILE})
@Retention(RetentionPolicy.SOURCE)
public @interface FileType {
    int PARENT = 0;
    int FOLDER = 1;
    int FILE = 2;
}
