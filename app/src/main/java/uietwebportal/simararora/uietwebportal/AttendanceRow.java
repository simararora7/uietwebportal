package uietwebportal.simararora.uietwebportal;

import android.os.Parcel;
import android.os.Parcelable;

public class AttendanceRow implements Parcelable {
    private String code;
    private String class_;
    private String type;
    private String delivered;
    private String attended;
    private String percentage;

    public AttendanceRow(String code, String class_, String type,
                         String delivered, String attended, String percentage) {
        super();
        this.code = code;
        this.class_ = class_.replaceAll("&amp;", "&");
        this.type = type;
        this.delivered = delivered;
        this.attended = attended;
        this.percentage = percentage;
    }

    public AttendanceRow() {
        code = "";
        class_ = "";
        type = "";
        delivered = "";
        attended = "";
        percentage = "";
    }

    public AttendanceRow(Parcel source) {
        code = source.readString();
        class_ = source.readString();
        type = source.readString();
        delivered = source.readString();
        attended = source.readString();
        percentage = source.readString();
    }

    public String getCode() {
        return code;
    }

    public String getClass_() {
        return class_;
    }

    public String getType() {
        return type;
    }

    public String getDelivered() {
        return delivered;
    }

    public String getAttended() {
        return attended;
    }

    public String getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return "AttendanceTableRow [code=" + code + ", class_=" + class_
                + ", type=" + type + ", delivered=" + delivered + ", attended="
                + attended + ", percentage=" + percentage + "]";
    }


    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(class_);
        dest.writeString(type);
        dest.writeString(delivered);
        dest.writeString(attended);
        dest.writeString(percentage);
    }

    public static final Parcelable.Creator<AttendanceRow> CREATOR = new Creator<AttendanceRow>() {

        @Override
        public AttendanceRow createFromParcel(Parcel source) {
            if (source != null)
                return new AttendanceRow(source);
            else
                return new AttendanceRow();
        }

        @Override
        public AttendanceRow[] newArray(int size) {
            return new AttendanceRow[size];
        }
    };
}
