package social.shofizone.com.networking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{


    private String username, email,userid;
    long phonenumber;
    public User(){

    }

    public User(String username, String email, String userid, long phonenumber) {
        this.username = username;
        this.email = email;
        this.userid = userid;
        this.phonenumber = phonenumber;
    }

    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        userid = in.readString();
        phonenumber = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(long phonenumber) {
        this.phonenumber = phonenumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", userid='" + userid + '\'' +
                ", phonenumber=" + phonenumber +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(userid);
        parcel.writeLong(phonenumber);
    }
}
