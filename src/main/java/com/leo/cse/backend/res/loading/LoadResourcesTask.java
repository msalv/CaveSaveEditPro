package com.leo.cse.backend.res.loading;

import com.leo.cse.backend.BytesReaderWriter;
import com.leo.cse.backend.CString;
import com.leo.cse.backend.res.loading.impl.MRMapResourcesLoader;
import com.leo.cse.backend.res.loading.impl.NXResourcesLoader;
import com.leo.cse.util.async.AsyncTask;
import com.leo.cse.util.async.AsyncTaskCallback;
import com.leo.cse.backend.exe.GameResourcesLoadingPayload;
import com.leo.cse.backend.res.GameResources;
import com.leo.cse.backend.res.loading.impl.ExeResourcesLoader;
import com.leo.cse.backend.res.loading.impl.GameResourcesLoader;
import com.leo.cse.backend.res.loading.impl.PlusResourcesLoader;
import com.leo.cse.frontend.Config;

import java.io.File;
import java.nio.file.Files;

public class LoadResourcesTask extends AsyncTask<GameResourcesLoadingPayload, GameResources> {
    private final File file;
    private final AsyncTaskCallback<GameResourcesLoadingPayload, GameResources> callback;

    public LoadResourcesTask(File file, AsyncTaskCallback<GameResourcesLoadingPayload, GameResources> callback) {
        this.file = file;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPreExecute();
    }

    @Override
    protected GameResources doInBackground() throws Exception {
        final byte[] data = Files.readAllBytes(file.toPath());
        final boolean shouldLoadNpcs = Config.getBoolean(Config.KEY_LOAD_NPCS, true);
        final String encoding = Config.get(Config.KEY_ENCODING, CString.DEFAULT_ENCODING);

        final GameResourcesLoader loader;
        if (isExeFile(data)) {
            loader = new ExeResourcesLoader(file, data, encoding, shouldLoadNpcs);
        } else if (isMoustacheFile(data)) {
            loader = new MRMapResourcesLoader(file, data, encoding, shouldLoadNpcs);
        } else if (isNXEngineFile(data)) {
            loader = new NXResourcesLoader(file, data, encoding, shouldLoadNpcs);
        } else {
            loader = new PlusResourcesLoader(file, data, encoding, shouldLoadNpcs);
        }
        loader.setCallback(this::publishProgress);

        return loader.load();
    }

    private boolean isExeFile(byte[] data) {
        return data[0] == 0x4d && data[1] == 0x5a;
    }

    private boolean isMoustacheFile(byte[] data) {
        final int count = BytesReaderWriter.readInt(data, 0);
        return data.length == count * 0x74 + Integer.BYTES;
    }

    private boolean isNXEngineFile(byte[] data) {
        final int count = data[0];
        return data.length == count * 0x49 + 1;
    }

    @Override
    protected void onProgressUpdate(GameResourcesLoadingPayload progress) {
        callback.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(GameResources gameResources) {
        callback.onPostExecute(gameResources);
    }

    @Override
    protected void onCancelled() {
        callback.onPostExecute(null);
    }
}
