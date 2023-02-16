package com.leo.cse.backend.profile;

public interface ProfileStateChangeListener {
	void onProfileStateChanged(ProfileStateEvent event, Object payload);
}
