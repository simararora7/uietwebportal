package uietwebportal.simararora.uietwebportal;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;

public class ResultFragment extends Fragment implements View.OnClickListener{
    private Spinner spSemester;
    private Spinner spCourse;
    private RadioGroup rgGrading;
    private RadioGroup rgRegular;
    private AutoCompleteTextView etRollNo;
    private AutoCompleteTextViewDatabase autoCompleteTextViewDatabase;
    private ArrayList<String> rollNumbers;
    private ArrayAdapter<String> arrayAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        autoCompleteTextViewDatabase = new AutoCompleteTextViewDatabase(getActivity());
        autoCompleteTextViewDatabase.open();
        rollNumbers = autoCompleteTextViewDatabase.getRollNumbersFromResultTable();
        autoCompleteTextViewDatabase.close();
        etRollNo = (AutoCompleteTextView) view.findViewById(R.id.etRollNo);
                arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, rollNumbers);
        etRollNo.setAdapter(arrayAdapter);
        etRollNo.setThreshold(1);
        spSemester = (Spinner) view.findViewById(R.id.spinnerSemester);
        spCourse = (Spinner) view.findViewById(R.id.spinnerCourse);
        rgGrading = (RadioGroup) view.findViewById(R.id.rgGrading);
        rgRegular = (RadioGroup) view.findViewById(R.id.rgRegular);
        Button bShowResult = (Button) view.findViewById(R.id.bShowResult);
        bShowResult.setOnClickListener(this);
        String[] semesterArray = getResources().getStringArray(R.array.semester_array);
        String[] courseArray = getResources().getStringArray(R.array.course_array);
        spSemester.setAdapter(new ArrayAdapter <>(getActivity(), android.R.layout.simple_list_item_1, semesterArray));
        spCourse.setAdapter(new ArrayAdapter <>(getActivity(), android.R.layout.simple_list_item_1, courseArray));
        return view;
    }

    @Override
    public void onClick(View v) {
        MainActivity.closeKeyboard(getActivity(), etRollNo.getWindowToken());
        if (v.getId() != R.id.bShowResult)
            return;
        String rollNo = etRollNo.getText().toString();
        if (rollNo.isEmpty()) {
            etRollNo.setError("Roll No. can't be left empty");
            return;
        }
        autoCompleteTextViewDatabase.open();
        autoCompleteTextViewDatabase.addRollNumberToResultTable(rollNo);
        autoCompleteTextViewDatabase.close();
        rollNumbers.add(rollNo);
        arrayAdapter.add(rollNo);
        arrayAdapter.notifyDataSetChanged();
        String semester = (String) spSemester.getSelectedItem();
        String course = (String) spCourse.getSelectedItem();
        String grading = getGrading();
        String regular = getRegular();
        Intent i = new Intent(getActivity(), ResultActivity.class);
        i.putExtra("rollNo", rollNo);
        i.putExtra("semester", semester);
        i.putExtra("course", course);
        i.putExtra("grading", grading);
        i.putExtra("regular", regular);
        startActivity(i);
    }

    public String getGrading() {
        switch(rgGrading.getCheckedRadioButtonId()){
            case R.id.rbGrading:
                return "RadioButtonG";
            case R.id.rbNonGrading:
                return "RadioButtonNG";
            default:
                return "";
        }
    }

    public String getRegular() {
        switch(rgRegular.getCheckedRadioButtonId()){
            case R.id.rbRegular:
                return "RadioButton1";
            case R.id.rbReappear:
                return "RadioButton2";
            default:
                return "";
        }
    }
}
