package uietwebportal.simararora.uietwebportal;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AttendanceActivity extends ActionBarActivity {
    private static ProgressDialog progressDialog;
    private TextView name;
    private TextView rollNo;
    private TextView branch;
    private TextView semester;
    private TextView father;
    private TextView mother;
    private ListView listview;
    private LinearLayout linearLayout;
    private boolean isTaskCompleted;
    private AttendanceTask attendanceTask;
    private Attendance attendance;
    private boolean suspend;
    private boolean taskRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        name = (TextView) findViewById(R.id.tvName);
        rollNo = (TextView) findViewById(R.id.tvRollNo);
        branch = (TextView) findViewById(R.id.tvBranch);
        semester = (TextView) findViewById(R.id.tvSemester);
        father = (TextView) findViewById(R.id.tvMFather);
        mother = (TextView) findViewById(R.id.tvMother);
        listview = (ListView) findViewById(R.id.lvAttendance);
        linearLayout = (LinearLayout) findViewById(R.id.llAttendance);
        if (savedInstanceState == null) {
            startTask();
        } else {
            savedInstanceState.setClassLoader(getClass().getClassLoader());
            attendance = savedInstanceState.getParcelable("attendance");
            if (attendance == null) {
                goBack();
            } else {
                if (attendance.getRows().isEmpty()) {
                    startTask();
                } else
                    populateViews();
            }
        }
    }

    private void startTask() {
        attendanceTask = new AttendanceTask();
        attendanceTask.execute(getIntent().getStringExtra("rollNo"));
        new Thread(new MyThread()).start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (attendance == null && taskRunning) {

            suspend = true;
            cancelTask(false);
            goBack();
        }
        outState.putParcelable("attendance", attendance);
        super.onSaveInstanceState(outState);
    }

    private void populateViews() {
        if (attendance == null)
            return;
        name.setText(attendance.getName());
        rollNo.setText(attendance.getRollNo());
        branch.setText(attendance.getBranch());
        semester.setText(attendance.getSemester());
        father.setText(attendance.getFathersName());
        mother.setText(attendance.getMothersName());
        listview.setAdapter(new AttendanceAdapter(attendance.getRows()));
        linearLayout.setVisibility(View.VISIBLE);
    }

    public void cancelTask(boolean showMwssage) {
        attendanceTask.cancel(true);
        progressDialog.dismiss();
        if (showMwssage)
            Toast.makeText(this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
        goBack();
    }

    private void goBack() {
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AttendanceTask extends AsyncTask<String, Void, Attendance> {

        private static final String idBranch = "ctl00_middleContent_Label1";
        private static final String idSemester = "ctl00_middleContent_Label2";
        private static final String idName = "ctl00_middleContent_Label3";
        private static final String idRollNo = "ctl00_middleContent_Label4";
        private static final String idFather = "ctl00_middleContent_Label5";
        private static final String idMother = "ctl00_middleContent_Label6";
        private static final String idEventTarget = "";
        private static final String idEventArgument = "";
        private static final String idViewStage = "/wEPDwULLTE2MjY1ODQ2MTEPZBYCZg9kFgICAw9kFgQCAQ8PFgIeBFRleHQFD1ZpZXcgQXR0ZW5kYW5jZWRkAgMPZBYCAgkPZBYCAg8PPCsADQBkGAEFHWN0bDAwJG1pZGRsZUNvbnRlbnQkR3JpZFZpZXcxD2dk07+jpDjdJzbL0b2rU7F+Pv2pJtA=";
        private static final String idEventValidation = "/wEWAwKQsuibDgLKm//KDwKSyNCHBsyNpAWIu2F/smBxOHVtztiDPiC/";
        private boolean validRollNo = true;
        private boolean workingInternet = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            suspend = false;
            taskRunning = true;

        }

        @Override
        protected Attendance doInBackground(String... params) {
            String downloadURL = "https://uwp.puchd.ac.in/common/viewattendance.aspx";
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }
            }

            };

            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc
                        .getSocketFactory());
                HttpsURLConnection
                        .setDefaultHostnameVerifier(new NullHostNameVerifier());
            } catch (Exception e) {
                e.printStackTrace();
            }

            HttpURLConnection connection = null;
            InputStream inputStream = null;
            Attendance attendance = null;

            try {
                URL url = new URL(downloadURL);
                String urlParameters = "__EVENTTARGET="
                        + URLEncoder.encode(idEventTarget, "UTF-8")
                        + "&__EVENTARGUMENT="
                        + URLEncoder.encode(idEventArgument, "UTF-8")
                        + "&__VIEWSTATE="
                        + URLEncoder
                        .encode(idViewStage,
                                "UTF-8")
                        + "&__EVENTVALIDATION="
                        + URLEncoder
                        .encode(idEventValidation,
                                "UTF-8") +

                        "&ctl00$middleContent$TextBox1="
                        + URLEncoder.encode(params[0], "UTF-8")
                        + "&ctl00$middleContent$cmdsubmit="
                        + URLEncoder.encode("Show Attendance", "UTF-8");
                connection = (HttpURLConnection) url
                        .openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length",
                        "" + Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                inputStream = connection.getInputStream();
                attendance = processXML(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
                workingInternet = false;
            } finally {
                if (connection != null)
                    connection.disconnect();
                if (inputStream != null)
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return attendance;
        }

        private Attendance processXML(InputStream is) {
            String str = getStringFromInputStream(is);
            str = str.replaceAll("&", "&amp;");
            is = new ByteArrayInputStream(
                    str.getBytes(Charset.forName("UTF-8")));
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder;
            Document xmlDocument = null;
            try {
                documentBuilder = documentBuilderFactory
                        .newDocumentBuilder();
                xmlDocument = documentBuilder.parse(is);
            } catch (Exception e) {
                e.printStackTrace();
                workingInternet = false;
            }
            Attendance attendance = new Attendance();
            try {
                Element rootElement;
                if (xmlDocument != null) {
                    rootElement = xmlDocument.getDocumentElement();
                    attendance.setBranch(xmlDocument.getElementById(idBranch)
                            .getTextContent());
                    attendance.setSemester(xmlDocument.getElementById(idSemester)
                            .getTextContent());
                    attendance.setName(xmlDocument.getElementById(idName)
                            .getTextContent());
                    attendance.setRollNo(xmlDocument.getElementById(idRollNo)
                            .getTextContent());
                    attendance.setFathersName(xmlDocument.getElementById(idFather)
                            .getTextContent());
                    attendance.setMothersName(xmlDocument.getElementById(idMother)
                            .getTextContent());
                    ArrayList<AttendanceRow> list = new ArrayList<>();
                    NodeList tableList = rootElement.getElementsByTagName("table");
                    Node attendanceTable = tableList.item(3);
                    NodeList childNodeList = attendanceTable.getChildNodes();
                    Node currentItem;
                    NodeList grandChildList;
                    AttendanceRow row;
                    for (int i = 2; i < childNodeList.getLength() - 2; i++) {
                        currentItem = childNodeList.item(i);
                        grandChildList = currentItem.getChildNodes();
                        row = new AttendanceRow(grandChildList.item(1)
                                .getTextContent(), grandChildList.item(2)
                                .getTextContent(), grandChildList.item(3)
                                .getTextContent(), grandChildList.item(4)
                                .getTextContent(), grandChildList.item(5)
                                .getTextContent(), grandChildList.item(6)
                                .getTextContent());
                        list.add(row);
                    }
                    attendance.setRows(list);
                }
            } catch (Exception e) {
                validRollNo = false;
                return null;
            }
            return attendance;

        }

        private String getStringFromInputStream(InputStream is) {
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();

        }

        @Override
        protected void onPostExecute(Attendance a) {
            super.onPostExecute(a);
            suspend = true;
            taskRunning = false;
            isTaskCompleted = true;
            if (workingInternet && validRollNo) {
                attendance = a;
                populateViews();
                progressDialog.dismiss();
            } else if (!workingInternet) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Server Not Available", Toast.LENGTH_SHORT).show();
                goBack();
            } else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Roll No. does not exist", Toast.LENGTH_SHORT).show();
                goBack();
            }


        }
    }

    private class AttendanceAdapter extends BaseAdapter {
        private ArrayList<AttendanceRow> rows;

        public AttendanceAdapter(ArrayList<AttendanceRow> rows) {
            this.rows = rows;
        }

        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public Object getItem(int position) {
            return rows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AttendanceRow currentRow = rows.get(position);
            TextView code, class_, type, delivered, attended, percentage;
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.row_attendance, parent, false);
            code = (TextView) convertView.findViewById(R.id.tvCode);
            class_ = (TextView) convertView.findViewById(R.id.tvClass);
            type = (TextView) convertView.findViewById(R.id.tvType);
            delivered = (TextView) convertView.findViewById(R.id.tvDelivered);
            attended = (TextView) convertView.findViewById(R.id.tvAttended);
            percentage = (TextView) convertView.findViewById(R.id.tvPercentage);
            code.setText(currentRow.getCode());
            class_.setText(currentRow.getClass_());
            type.setText(currentRow.getType());
            delivered.setText(currentRow.getDelivered());
            attended.setText(currentRow.getAttended());
            percentage.setText(currentRow.getPercentage());
            return convertView;
        }
    }

    private class MyThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (!suspend) {
                    AttendanceActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (!isTaskCompleted) {
                                cancelTask(true);
                            }
                        }
                    });
                }
            }
        }
    }
}
