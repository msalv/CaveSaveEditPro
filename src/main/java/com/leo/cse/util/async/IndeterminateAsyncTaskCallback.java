package com.leo.cse.util.async;

public abstract class IndeterminateAsyncTaskCallback<Result> implements AsyncTaskCallback<Void, Result> {
    @Override
    public final void onProgressUpdate(Void progress) {
    }
}
