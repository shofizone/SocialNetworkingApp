package social.shofizone.com.networking.model;

public class UserSettings {

    private User mUser;
    private UserAccountSettings mSettings;

    public UserSettings(User user, UserAccountSettings settings) {
        mUser = user;
        mSettings = settings;
    }

    public UserSettings() {

    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public UserAccountSettings getSettings() {
        return mSettings;
    }

    public void setSettings(UserAccountSettings settings) {
        mSettings = settings;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "mUser=" + mUser +
                ", mSettings=" + mSettings +
                '}';
    }
}
