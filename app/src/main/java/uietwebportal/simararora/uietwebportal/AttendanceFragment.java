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

import java.util.ArrayList;

public class AttendanceFragment extends Fragment implements View.OnClickListener {
    private ArrayList<String> rollNumbers;
    private AutoCompleteTextViewDatabase autoCompleteTextViewDatabase;
    private AutoCompleteTextView etRollNo;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        autoCompleteTextViewDatabase = new AutoCompleteTextViewDatabase(getActivity());
        autoCompleteTextViewDatabase.open();
        rollNumbers = autoCompleteTextViewDatabase.getRollNumbersFromAttendanceTable();
        autoCompleteTextViewDatabase.close();
        etRollNo = (AutoCompleteTextView) view.findViewById(R.id.etRollNo);
        etRollNo.setThreshold(1);
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, rollNumbers);
        etRollNo.setAdapter(arrayAdapter);
        Button bShowAttendance = (Button) view.findViewById(R.id.bShowAttendance);
        bShowAttendance.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        MainActivity.closeKeyboard(getActivity(), etRollNo.getWindowToken());
        if (v.getId() != R.id.bShowAttendance)
            return;
        String rollNo = etRollNo.getText().toString();
        if (rollNo.isEmpty()) {
            etRollNo.setError("Roll No. can't be left empty");
            return;
        }
        autoCompleteTextViewDatabase.open();
        autoCompleteTextViewDatabase.addRollNumberToAttendanceTable(rollNo);
        autoCompleteTextViewDatabase.close();
        rollNumbers.add(rollNo);
        arrayAdapter.add(rollNo);
        arrayAdapter.notifyDataSetChanged();
        Intent i = new Intent(getActivity().getApplicationContext(), AttendanceActivity.class);
        i.putExtra("rollNo", rollNo);
        startActivity(i);
    }
}