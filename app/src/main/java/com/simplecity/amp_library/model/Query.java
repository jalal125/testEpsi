package com.simplecity.amp_library.model;

import android.net.Uri;
import java.util.Arrays;

public class Query {

    private final Uri uri;
    private final String[] projection;
    private final String selection;
    private final String[] args;
    private final String sort;

    Query(Builder builder) {
        this.uri = builder.uri;
        this.projection = builder.projection;
        this.selection = builder.selection;
        this.args = builder.args;
        this.sort = builder.sort;
    }

    /** Returns the content URI to query. */
    public Uri getUri() {
        return uri;
    }

    /** Returns the columns to retrieve. */
    public String[] getProjection() {
        return projection;
    }

    /** Returns the selection clause. */
    public String getSelection() {
        return selection;
    }

    /** Returns the selection arguments. */
    public String[] getArgs() {
        return args;
    }

    /** Returns the sort order. */
    public String getSort() {
        return sort;
    }

    public static final class Builder {
        private Uri uri;
        private String[] projection;
        private String selection;
        private String[] args;
        private String sort;

        public Builder() {}

        public Builder uri(Uri val) {
            this.uri = val;
            return this;
        }

        public Builder projection(String[] val) {
            this.projection = val;
            return this;
        }

        public Builder selection(String val) {
            this.selection = val;
            return this;
        }

        public Builder args(String[] val) {
            this.args = val;
            return this;
        }

        public Builder sort(String val) {
            this.sort = val;
            return this;
        }

        public Query build() {
            return new Query(this);
        }
    }

    @Override
    public String toString() {
        return "Query{" +
               "\n uri=" + uri +
               "\n projection=" + Arrays.toString(projection) +
               "\n selection='" + selection + '\'' +
               "\n args=" + Arrays.toString(args) +
               "\n sort='" + sort + '\'' +
               '}';
    }
}
