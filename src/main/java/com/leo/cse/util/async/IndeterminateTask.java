package com.leo.cse.util.async;

/**
 * AsyncTask without progress updates
 */
@SuppressWarnings("EmptyMethod")
abstract public class IndeterminateTask<Result> extends AsyncTask<Void, Result> {
    @Override
    protected final void onProgressUpdate(Void progress) {
    }
}
