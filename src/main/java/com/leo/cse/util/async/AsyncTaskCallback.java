package com.leo.cse.util.async;

public interface AsyncTaskCallback<Progress, Result> {
    void onPreExecute();
    void onPostExecute(Result result);
    void onProgressUpdate(Progress values);
}
