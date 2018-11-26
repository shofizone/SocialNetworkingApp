package social.shofizone.com.networking.model;

public class UserAccountSettings {


    public String description, displayname, profilephoto, username, biodata ,user_id;
    public long followers, following, posts;

    public UserAccountSettings() {

    }

    public UserAccountSettings(String description, String displayname, String profilephoto, String username, String biodata, String user_id, long followers, long following, long posts) {
        this.description = description;
        this.displayname = displayname;
        this.profilephoto = profilephoto;
        this.username = username;
        this.biodata = biodata;
        this.user_id = user_id;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBiodata() {
        return biodata;
    }

    public void setBiodata(String biodata) {
        this.biodata = biodata;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }


    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "description='" + description + '\'' +
                ", displayname='" + displayname + '\'' +
                ", profilephoto='" + profilephoto + '\'' +
                ", username='" + username + '\'' +
                ", biodata='" + biodata + '\'' +
                ", user_id=" + user_id +'\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                '}';
    }
}

