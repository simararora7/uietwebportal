package uietwebportal.simararora.uietwebportal;

import android.os.Parcel;
import android.os.Parcelable;

public class ResultRow implements Parcelable{
    private String code;
    private String name;
    private String earnerCreditsTh;
    private String gradePointsTh;
    private String earnedCreditsPr;
    private String gradePointsPr;

    public ResultRow(String code, String name, String earnerCreditsTh,
                     String gradePointsTh, String earnedCreditsPr, String gradePointsPr) {
        super();
        this.code = code;
        this.name = name.replaceAll("&amp;", "&");
        this.earnerCreditsTh = validate(earnerCreditsTh);
        this.gradePointsTh = validate(gradePointsTh);
        this.earnedCreditsPr = validate(earnedCreditsPr);
        this.gradePointsPr = validate(gradePointsPr);
    }

    public ResultRow(Parcel source) {
        code = source.readString();
        name = source.readString();
        earnerCreditsTh = source.readString();
        gradePointsTh = source.readString();
        earnedCreditsPr = source.readString();
        gradePointsPr = source.readString();
    }

    public ResultRow(){
        code = "";
        name = "";
        earnedCreditsPr = "";
        earnerCreditsTh = "";
        gradePointsPr = "";
        gradePointsTh = "";
    }

    private String validate(String s) {
        if (s.equals("&nbsp;"))
            return "";
        return s;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getEarnerCreditsTh() {
        return earnerCreditsTh;
    }

    public String getGradePointsTh() {
        return gradePointsTh;
    }

    public String getEarnedCreditsPr() {
        return earnedCreditsPr;
    }

    public String getGradePointsPr() {
        return gradePointsPr;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ResultRow [code=" + code + ", name=" + name
                + ", earnerCreditsTh=" + earnerCreditsTh + ", gradePointsTh="
                + gradePointsTh + ", earnedCreditsPr=" + earnedCreditsPr
                + ", gradePointsPr=" + gradePointsPr + "]";
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(earnedCreditsPr);
        dest.writeString(gradePointsPr);
        dest.writeString(earnerCreditsTh);
        dest.writeString(gradePointsTh);
    }

    public static final Parcelable.Creator<ResultRow> CREATOR = new Creator<ResultRow>() {

        @Override
        public ResultRow createFromParcel(Parcel source) {
            if (source != null)
                return new ResultRow(source);
            else
                return new ResultRow();
        }

        @Override
        public ResultRow[] newArray(int size) {
            return new ResultRow[size];
        }
    };
}
