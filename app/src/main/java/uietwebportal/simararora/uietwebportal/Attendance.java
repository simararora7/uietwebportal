package uietwebportal.simararora.uietwebportal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Attendance implements Parcelable {
    private String rollNo;
    private String Branch;
    private String semester;
    private String name;
    private String fathersName;
    private String mothersName;
    private ArrayList<AttendanceRow> rows;

    public Attendance(Parcel source) {
        rollNo = source.readString();
        Branch = source.readString();
        semester = source.readString();
        name = source.readString();
        fathersName = source.readString();
        mothersName = source.readString();
        Bundle b = source.readBundle();
        b.setClassLoader(getClass().getClassLoader());
        try {
            rows = b.getParcelableArrayList("rows");
        } catch (Exception e) {
            rows = new ArrayList<>();
        }
    }

    public Attendance() {
        rollNo = "";
        Branch = "";
        semester = "";
        name = "";
        fathersName = "";
        mothersName = "";
        rows = new ArrayList<>();
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getBranch() {
        return Branch;
    }

    public String getSemester() {
        return semester;
    }

    public String getName() {
        return name;
    }

    public String getFathersName() {
        return fathersName;
    }

    public String getMothersName() {
        return mothersName;
    }

    public ArrayList<AttendanceRow> getRows() {
        return rows;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public void setBranch(String branch) {
        Branch = branch;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFathersName(String fathersName) {
        this.fathersName = fathersName;
    }

    public void setMothersName(String mothersName) {
        this.mothersName = mothersName;
    }

    public void setRows(ArrayList<AttendanceRow> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "Attendance [rollNo=" + rollNo + ", Branch=" + Branch
                + ", semester=" + semester + ", name=" + name
                + ", fathersName=" + fathersName + ", mothersName="
                + mothersName + ", rows=" + rows + "]";
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rollNo);
        dest.writeString(Branch);
        dest.writeString(semester);
        dest.writeString(name);
        dest.writeString(fathersName);
        dest.writeString(mothersName);
        Bundle b = new Bundle();
        b.putParcelableArrayList("rows", rows);
        dest.writeBundle(b);
    }


    public static final Parcelable.Creator<Attendance> CREATOR = new Creator<Attendance>() {
        @Override
        public Attendance createFromParcel(Parcel source) {
            if (source != null)
                return new Attendance(source);
            else
                return new Attendance();

        }

        @Override
        public Attendance[] newArray(int size) {
            return new Attendance[size];
        }
    };
}
