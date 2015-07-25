package uietwebportal.simararora.uietwebportal;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class ResultActivity extends ActionBarActivity {
    private ProgressDialog progressDialog;
    private TextView name;
    private TextView rollNo;
    private TextView branch;
    private TextView semester;
    private TextView father;
    private TextView mother;
    private TextView cgpa;
    private ListView resultListView;
    private LinearLayout linearLayout;
    private boolean isTaskCompleted;
    private ResultTask resultTask;
    private Result result;
    private boolean suspend;
    private boolean taskRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_result);
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
        cgpa = (TextView) findViewById(R.id.tvCGPA);
        resultListView = (ListView) findViewById(R.id.lvResult);
        linearLayout = (LinearLayout) findViewById(R.id.llResult);
        Intent i = getIntent();
        if (savedInstanceState == null) {
            startTask(i);
        } else {
            savedInstanceState.setClassLoader(getClass().getClassLoader());
            result = savedInstanceState.getParcelable("result");
            if (result == null) {
                goBack();
            } else {
                if (result.getRows().isEmpty()) {
                    startTask(i);
                } else
                    populateViews();
            }
        }
    }

    private void startTask(Intent i) {
        resultTask = new ResultTask();
        resultTask.execute(i.getStringExtra("rollNo"), i.getStringExtra("semester"), i.getStringExtra("course"), i.getStringExtra("grading"), i.getStringExtra("regular"));
        new Thread(new MyThread()).start();
    }

    private void populateViews() {
        if (result == null)
            return;
        name.setText(result.getName());
        rollNo.setText(result.getRollNo());
        branch.setText(result.getBranch());
        semester.setText(result.getSemester());
        father.setText(result.getFathersName());
        mother.setText(result.getMothersName());
        cgpa.setText(result.getCgpa());
        resultListView.setAdapter(new ResultAdapter(result.getRows()));
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(result == null && taskRunning){
            suspend = true;
            cancelTask(false);
            goBack();
        }
        outState.putParcelable("result", result);
        super.onSaveInstanceState(outState);
    }

    public void cancelTask(boolean showMessage) {
        resultTask.cancel(true);
        progressDialog.dismiss();
        if(showMessage)
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
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ResultTask extends AsyncTask<String, Void, Result> {

        private static final String idBranch = "ctl00_middleContent_Label1";
        private static final String idSemester = "ctl00_middleContent_Label2";
        private static final String idName = "ctl00_middleContent_Label3";
        private static final String idRollNo = "ctl00_middleContent_Label4";
        private static final String idFather = "ctl00_middleContent_Label5";
        private static final String idMother = "ctl00_middleContent_Label6";
        private static final String idCGPA = "ctl00_middleContent_Label7";
        private static final String idEventTarget = "";
        private static final String idEventArgument = "";
        private static final String idViewStage = "/wEPDwUKLTMzMTUyNjUzMA9kFgJmD2QWAgIDD2QWBAIBDw8WAh4EVGV4dAUKVmlldyBNYXJrc2RkAgMPZBYCAgEPZBYEAhMPDxYCHgdWaXNpYmxlaGRkAhUPDxYCHwFnZBYSAgEPDxYCHwAFA0NTRWRkAgMPDxYCHwAFATFkZAIFDw8WAh8ABRZTSU1BUlBSRUVUIFNJTkdIIEFST1JBZGQCBw8PFgIfAAUIVUUxMjMwODlkZAIJDw8WAh8ABRVTSEFNSU5ERVIgU0lOR0ggQVJPUkFkZAILDw8WAh8ABQtLQU1BTCBBUk9SQWRkAg0PDxYCHwAFOFNHUEEgPSA4LjUyLCBDdW11bGF0aXZlIEVhcm5lZCBDcmVkaXRzID0gMjMsIENHUEEgPSA4LjUyZGQCDw88KwANAGQCEQ88KwANAQAPFgQeC18hRGF0YUJvdW5kZx4LXyFJdGVtQ291bnQCBmQWAmYPZBYOAgEPZBYMZg8PFgIfAAUGQVMtMTAxZGQCAQ8PFgIfAAUZRW5naW5lZXJpbmcgTWF0aGVtYXRpY3MtSWRkAgIPDxYCHwAFATRkZAIDDw8WAh8ABQJBK2RkAgQPDxYCHwAFBiZuYnNwO2RkAgUPDxYCHwAFBiZuYnNwO2RkAgIPZBYMZg8PFgIfAAUGQVMtMTAyZGQCAQ8PFgIfAAUHUGh5c2ljc2RkAgIPDxYCHwAFATRkZAIDDw8WAh8ABQFCZGQCBA8PFgIfAAUBMmRkAgUPDxYCHwAFAUJkZAIDD2QWDGYPDxYCHwAFBkFTLTEwM2RkAgEPDxYCHwAFF0Vudmlyb25tZW50YWwgRWR1Y2F0aW9uZGQCAg8PFgIfAAUBMGRkAgMPDxYCHwAFAk5QZGQCBA8PFgIfAAUGJm5ic3A7ZGQCBQ8PFgIfAAUGJm5ic3A7ZGQCBA9kFgxmDw8WAh8ABQZFQy0xMDFkZAIBDw8WAh8ABRFCYXNpYyBFbGVjdHJvbmljc2RkAgIPDxYCHwAFATRkZAIDDw8WAh8ABQFBZGQCBA8PFgIfAAUGJm5ic3A7ZGQCBQ8PFgIfAAUGJm5ic3A7ZGQCBQ9kFgxmDw8WAh8ABQZFRS0xMDFkZAIBDw8WAh8ABRxCYXNpYyBFbGVjdHJpY2FsIEVuZ2luZWVyaW5nZGQCAg8PFgIfAAUBNGRkAgMPDxYCHwAFAUFkZAIEDw8WAh8ABQEyZGQCBQ8PFgIfAAUBQWRkAgYPZBYMZg8PFgIfAAUGTUUtMTUzZGQCAQ8PFgIfAAUURW5naW5lZXJpbmcgR3JhcGhpY3NkZAICDw8WAh8ABQYmbmJzcDtkZAIDDw8WAh8ABQYmbmJzcDtkZAIEDw8WAh8ABQEzZGQCBQ8PFgIfAAUCQitkZAIHDw8WAh8BaGRkGAMFHl9fQ29udHJvbHNSZXF1aXJlUG9zdEJhY2tLZXlfXxYGBSBjdGwwMCRtaWRkbGVDb250ZW50JFJhZGlvQnV0dG9uRwUhY3RsMDAkbWlkZGxlQ29udGVudCRSYWRpb0J1dHRvbk5HBSFjdGwwMCRtaWRkbGVDb250ZW50JFJhZGlvQnV0dG9uTkcFIGN0bDAwJG1pZGRsZUNvbnRlbnQkUmFkaW9CdXR0b24xBSBjdGwwMCRtaWRkbGVDb250ZW50JFJhZGlvQnV0dG9uMgUgY3RsMDAkbWlkZGxlQ29udGVudCRSYWRpb0J1dHRvbjIFHWN0bDAwJG1pZGRsZUNvbnRlbnQkR3JpZFZpZXcyDzwrAAoBCAIBZAUdY3RsMDAkbWlkZGxlQ29udGVudCRHcmlkVmlldzEPZ2Q8yNBNqh6Jidv7B1oc2aslDHj45w==";
        private static final String idEventValidation = "/wEWFQKZnIPVDALKm//KDwKwzYbXDQKxzYbXDQKyzYbXDQKzzYbXDQK0zYbXDQK1zYbXDQK2zYbXDQKnzYbXDQKozYbXDQKwzcbUDQLgzbrJDQKyqe3pBgLdzbrJDQKs9fOJAQKJ27+qBwLtmLvJDAL2wcuTDwL2wb+3BgKSyNCHBvpWpqELiynjZG4Npc3bFn1mGccH";
        private boolean validRollNo = true;
        private boolean workingInternet = true;
        private boolean grading = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            suspend = false;
            taskRunning = true;
        }

        @Override
        protected Result doInBackground(String... params) {
            String rollNo = params[0];
            String semester = params[1];
            String course = params[2];
            String gradingNonGrading = params[3];
            String regularReappear = params[4];
            String downloadURL = "https://uwp.puchd.ac.in/common/viewmarks.aspx";
            if (gradingNonGrading.equals("RadioButtonNG")) {
                grading = false;
                return null;
            }
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
            Result result = null;
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
                        + URLEncoder.encode(rollNo, "UTF-8")
                        + "&ctl00$middleContent$DropDownList1="
                        + URLEncoder.encode(semester, "UTF-8")
                        + "&ctl00$middleContent$DropDownList2="
                        + URLEncoder.encode(course, "UTF-8")
                        + "&ctl00$middleContent$type="
                        + URLEncoder.encode(gradingNonGrading, "UTF-8")
                        + "&ctl00$middleContent$resulttype="
                        + URLEncoder.encode(regularReappear, "UTF-8")
                        + "&ctl00$middleContent$cmdsubmit="
                        + URLEncoder.encode("Show Marks", "UTF-8");
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
                result = processXML(inputStream);
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
            return result;
        }

        private Result processXML(InputStream is) {
            Result result = new Result();
            String str;
            DocumentBuilderFactory documentBuilderFactory;
            DocumentBuilder documentBuilder;
            Document xmlDocument = null;
            try {
                str = validateInputStream(is);
                str = str.replaceAll("&", "&amp;");
                is = new ByteArrayInputStream(
                        str.getBytes(Charset.forName("UTF-8")));
                documentBuilderFactory = DocumentBuilderFactory
                        .newInstance();
                documentBuilder = documentBuilderFactory
                        .newDocumentBuilder();
                xmlDocument = documentBuilder.parse(is);
            } catch (Exception e) {
                e.printStackTrace();
                workingInternet = false;
            }
            if (xmlDocument != null) {
                try {
                    Element rootElement = xmlDocument.getDocumentElement();
                    result.setBranch(xmlDocument.getElementById(idBranch)
                            .getTextContent());
                    result.setSemester(xmlDocument.getElementById(idSemester)
                            .getTextContent());
                    result.setName(xmlDocument.getElementById(idName).getTextContent());
                    result.setRollNo(xmlDocument.getElementById(idRollNo)
                            .getTextContent());
                    result.setFathersName(xmlDocument.getElementById(idFather)
                            .getTextContent());
                    result.setMothersName(xmlDocument.getElementById(idMother)
                            .getTextContent());
                    result.setCgpa(xmlDocument.getElementById(idCGPA).getTextContent());
                    NodeList tablesList = rootElement.getElementsByTagName("table");
                    Node resultNode = tablesList.item(4);
                    NodeList childList = resultNode.getChildNodes();
                    Node currentItem;
                    NodeList grandChildList;
                    ResultRow row;
                    ArrayList<ResultRow> list = new ArrayList<>();
                    for (int i = 2; i < childList.getLength() - 1; i++) {
                        currentItem = childList.item(i);
                        grandChildList = currentItem.getChildNodes();
                        row = new ResultRow(grandChildList.item(1).getTextContent(),
                                grandChildList.item(2).getTextContent(), grandChildList
                                .item(3).getTextContent(), grandChildList.item(
                                4).getTextContent(), grandChildList.item(5)
                                .getTextContent(), grandChildList.item(6)
                                .getTextContent());
                        list.add(row);
                    }
                    result.setRows(list);
                } catch (Exception e) {
                    validRollNo = false;
                    return null;
                }
            }
            return result;
        }

        private String validateInputStream(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                sb.append(line);
//                Log.d("Simar",i + " " + line);

                if (i == 67){
                    sb.append("</div>");}
                i++;
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(Result r) {
            super.onPostExecute(r);
            isTaskCompleted = true;
            taskRunning = false;
            suspend = true;
            if (!grading) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Support for Non Grading yet to be added", Toast.LENGTH_SHORT).show();
                goBack();
                return;
            }

            if (workingInternet && validRollNo) {
                result = r;
                populateViews();
                progressDialog.dismiss();
            } else if (!workingInternet) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Server Not Available", Toast.LENGTH_SHORT).show();
                goBack();
            } else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Either Roll No. does not exist or Result has not been processed yet", Toast.LENGTH_SHORT).show();
                goBack();
            }
        }
    }

    private class ResultAdapter extends BaseAdapter {
        private ArrayList<ResultRow> rows;

        public ResultAdapter(ArrayList<ResultRow> rows) {
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
            ResultRow currentRow = rows.get(position);
            TextView code, name, ecth, gpth, ecpr, gppr;
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.row_result, parent, false);
            code = (TextView) convertView.findViewById(R.id.tvCode);
            name = (TextView) convertView.findViewById(R.id.tvName);
            ecth = (TextView) convertView.findViewById(R.id.tvCreditsTh);
            gpth = (TextView) convertView.findViewById(R.id.tvGpTh);
            ecpr = (TextView) convertView.findViewById(R.id.tvCreditsPr);
            gppr = (TextView) convertView.findViewById(R.id.tvGpPr);
            code.setText(currentRow.getCode());
            name.setText(currentRow.getName());
            ecth.setText(currentRow.getEarnerCreditsTh());
            gpth.setText(currentRow.getGradePointsTh());
            ecpr.setText(currentRow.getEarnedCreditsPr());
            gppr.setText(currentRow.getGradePointsPr());
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
                    ResultActivity.this.runOnUiThread(new Runnable() {

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
