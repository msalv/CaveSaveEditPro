package com.leo.cse.backend.res.loading;

import com.leo.cse.backend.res.GameResources;

public interface LoadCallback {
    void onSuccess(GameResources gameResources);
}
