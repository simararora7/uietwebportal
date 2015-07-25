package uietwebportal.simararora.uietwebportal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Result implements Parcelable{

    private String branch;
    private String semester;
    private String name;
    private String rollNo;
    private String fathersName;
    private String mothersName;
    private String cgpa;
    private ArrayList<ResultRow> rows;

    public Result(Parcel source) {
        rollNo = source.readString();
        branch = source.readString();
        semester = source.readString();
        name = source.readString();
        fathersName = source.readString();
        mothersName = source.readString();
        cgpa = source.readString();
        Bundle b = source.readBundle();
        b.setClassLoader(getClass().getClassLoader());
        try {
            rows = b.getParcelableArrayList("rows");
        } catch (Exception e) {
            rows = new ArrayList<>();
        }
    }

    public Result(){
        rollNo = "";
        branch = "";
        semester = "";
        name = "";
        fathersName = "";
        mothersName = "";
        rows = new ArrayList<>();
        cgpa = "";
    }

    public String getBranch() {
        return branch;
    }

    public String getSemester() {
        return semester;
    }

    public String getName() {
        return name;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getFathersName() {
        return fathersName;
    }

    public String getMothersName() {
        return mothersName;
    }

    public String getCgpa() {
        return cgpa;
    }

    public ArrayList<ResultRow> getRows() {
        return rows;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public void setFathersName(String fathersName) {
        this.fathersName = fathersName;
    }

    public void setMothersName(String mothersName) {
        this.mothersName = mothersName;
    }

    public void setCgpa(String cgpa) {
        this.cgpa = cgpa;
    }

    public void setRows(ArrayList<ResultRow> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "Result{" +
                "branch='" + branch + '\'' +
                ", semester='" + semester + '\'' +
                ", name='" + name + '\'' +
                ", rollNo='" + rollNo + '\'' +
                ", fathersName='" + fathersName + '\'' +
                ", mothersName='" + mothersName + '\'' +
                ", cgpa='" + cgpa + '\'' +
                ", rows=" + rows +
                '}';
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(branch);
        dest.writeString(semester);
        dest.writeString(name);
        dest.writeString(rollNo);
        dest.writeString(fathersName);
        dest.writeString(mothersName);
        dest.writeString(cgpa);
        Bundle b = new Bundle();
        b.putParcelableArrayList("rows", rows);
        dest.writeBundle(b);
    }

    public static final Parcelable.Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel source) {
            if (source != null)
                return new Result(source);
            else
                return new Result();

        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };
}