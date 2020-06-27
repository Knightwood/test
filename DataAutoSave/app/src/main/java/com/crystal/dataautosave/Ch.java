package com.crystal.dataautosave;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/27 13:02
 */
public class Ch implements Parcelable {
    public int studentId;
    public String studentName;
    public int age;

    protected Ch(Parcel in) {
        studentId = in.readInt();
        studentName = in.readString();
        age = in.readInt();
    }

    public static final Creator<Ch> CREATOR = new Creator<Ch>() {
        @Override
        public Ch createFromParcel(Parcel in) {
            return new Ch(in);
        }

        @Override
        public Ch[] newArray(int size) {
            return new Ch[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(studentId);
        parcel.writeString(studentName);
        parcel.writeInt(age);
    }
}
