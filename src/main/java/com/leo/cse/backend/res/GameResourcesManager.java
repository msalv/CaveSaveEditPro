package com.leo.cse.backend.res;

import com.leo.cse.backend.CString;
import com.leo.cse.util.FileUtils;
import com.leo.cse.util.async.AsyncTaskCallback;
import com.leo.cse.backend.exe.GameResourcesLoadingState;
import com.leo.cse.backend.exe.OnGameResourcesLoadingStateChangeListener;
import com.leo.cse.backend.exe.GameResourcesLoadingPayload;
import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.res.loading.LoadCallback;
import com.leo.cse.backend.res.loading.LoadResourcesTask;
import com.leo.cse.frontend.Config;

import java.awt.EventQueue;
import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JOptionPane;

public class GameResourcesManager {
    private static volatile GameResourcesManager sInstance;

    private File currentFile;

    private GameResources resources;

    private final Queue<OnGameResourcesLoadingStateChangeListener> listeners = new ConcurrentLinkedQueue<>();

    private GameResourcesManager() {
    }

    public static GameResourcesManager getInstance() {
        GameResourcesManager localInstance = sInstance;

        if (localInstance == null) {
            synchronized (GameResourcesManager.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    sInstance = localInstance = new GameResourcesManager();
                }
            }
        }
        return localInstance;
    }

    public boolean hasResources() {
        return resources != null;
    }

    public GameResources getResources() {
        return resources;
    }

    public String getResourcesPath() {
        return (currentFile != null) ? currentFile.getAbsolutePath() : null;
    }

    public String getPlusGamePath() {
        try {
            return (currentFile != null)
                    ? FileUtils.getBaseFolder(currentFile).getParentFile().getParentFile().getAbsolutePath()
                    : null;
        } catch (Exception e) {
            return null;
        }
    }

    public void load(File file, LoadCallback callback) {
        currentFile = file;
        new LoadResourcesTask(file, new AsyncTaskCallback<GameResourcesLoadingPayload, GameResources>() {
            @Override
            public void onPreExecute() {
                EventQueue.invokeLater(() -> {
                    notifyListeners(GameResourcesLoadingState.BEGIN);
                });
            }

            @Override
            public void onPostExecute(GameResources gameResources) {
                if (gameResources != null) {
                    resources = gameResources;
                    if (callback != null) {
                        callback.onSuccess(gameResources);
                    }
                    AppLogger.info(String.format("Game resources loaded: %s", file));
                    Config.set(Config.KEY_LAST_MOD, file.getAbsolutePath());
                } else {
                    AppLogger.error("Game resources loading failed.");
                    JOptionPane.showMessageDialog(
                            null,
                            "An error occurred while loading game resources",
                            "Could not load executable!", JOptionPane.ERROR_MESSAGE
                    );
                }
                notifyListeners(GameResourcesLoadingState.DONE);
            }

            @Override
            public void onProgressUpdate(GameResourcesLoadingPayload values) {
                notifyListeners(GameResourcesLoadingState.IN_PROGRESS, values);
            }
        }).execute();
    }

    public void reload() {
        if (currentFile != null) {
            load(currentFile, null);
        }
    }

    public void clear() {
        resources = null;
        currentFile = null;
        notifyListeners(GameResourcesLoadingState.NONE);
    }

    public void addListener(OnGameResourcesLoadingStateChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OnGameResourcesLoadingStateChangeListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    private void notifyListeners(GameResourcesLoadingState event) {
        notifyListeners(event, null);
    }

    private void notifyListeners(GameResourcesLoadingState state, Object payload) {
        for (OnGameResourcesLoadingStateChangeListener listener : listeners) {
            listener.onGameResourcesLoadingStateChanged(state, payload);
        }
    }

    /**
     * Checks if the current "executable" is a stage.tbl file.
     *
     * @return <code>true</code> if in CS+ mode, <code>false</code> otherwise
     */
    public boolean isCurrentModePlus() {
        return resources instanceof PlusGameResources;
    }

    public String getEncoding() {
        return Config.get(Config.KEY_ENCODING, CString.DEFAULT_ENCODING);
    }

    /**
     * Checks if NPC files will be loaded.
     *
     * @return <code>true</code> if will be loaded, <code>false</code> otherwise.
     */
    public boolean shouldLoadNpc() {
        return Config.getBoolean(Config.KEY_LOAD_NPCS, true);
    }
}
