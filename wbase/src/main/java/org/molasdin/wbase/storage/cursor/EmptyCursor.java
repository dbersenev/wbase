package org.molasdin.wbase.storage.cursor;

import java.util.Collections;
import java.util.List;

/**
 * Created by dbersenev on 14.03.2016.
 */
public class EmptyCursor {

    private final static BiDirectionalBatchCursor<?> twbc = new BiDirectionalBatchCursor<Object>() {
        @Override
        public long currentPage() {
            return 0;
        }

        @Override
        public void setCurrentPage(long page) {

        }

        @Override
        public List<Object> data() {
            return Collections.emptyList();
        }

        @Override
        public boolean isAfter() {
            return false;
        }

        @Override
        public void next() {

        }

        @Override
        public long pageSize() {
            return 0;
        }

        @Override
        public long size() {
            return 0;
        }

        @Override
        public void close() {

        }
    };

    @SuppressWarnings("unchecked")
    public static <R> BiDirectionalBatchCursor<R> emptyTwoWaysBatchCursor(){
        return (BiDirectionalBatchCursor<R>) twbc;
    }
}
