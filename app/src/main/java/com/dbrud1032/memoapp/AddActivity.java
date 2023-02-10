package com.dbrud1032.memoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dbrud1032.memoapp.api.MemoApi;
import com.dbrud1032.memoapp.api.NetworkClient;
import com.dbrud1032.memoapp.api.UserApi;
import com.dbrud1032.memoapp.config.Config;
import com.dbrud1032.memoapp.model.Memo;
import com.dbrud1032.memoapp.model.MemoList;
import com.dbrud1032.memoapp.model.Res;
import com.dbrud1032.memoapp.model.User;
import com.dbrud1032.memoapp.model.UserRes;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddActivity extends AppCompatActivity {

    EditText editTitle;
    Button btnDate;
    Button btnTime;
    EditText editContent;
    Button btnSave;
    private ProgressDialog dialog;
    String time = "";
    String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        editTitle = findViewById(R.id.editTitle);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        editContent = findViewById(R.id.editContent);
        btnSave = findViewById(R.id.btnSave);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 오늘 날짜 가져오기
                Calendar current = Calendar.getInstance();

                new DatePickerDialog(
                        AddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                Log.i("MEMO_APP", "년도 :" + i + " 월 :" + i1 + " 일 :" + i2);
                                // i : 년도, i1 : 월 (0~11) , i2 : 일

                                int month = i1 + 1;
                                String strMonth;
                                if (month < 10) {
                                    strMonth = "0" + month;
                                } else {
                                    strMonth = "" + month;
                                }


                                String strDay;
                                if (i2 < 10) {
                                    strDay = "0" + i2;
                                } else {
                                    strDay = "" + i2;
                                }

                                date = i + "-" + strMonth + "-" + strDay;
                                btnDate.setText(date);

                            }
                        },
                        current.get(Calendar.YEAR),
                        current.get(Calendar.MONTH),
                        current.get(Calendar.DAY_OF_MONTH)
                ).show();  // 변수 만들어서 show 해도 되고, 여기서 이렇게 show 해도 된다.

            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar current = Calendar.getInstance();

//                TimePickerDialog dialog = new TimePickerDialog();
//                dialog.show();

                new TimePickerDialog(
                        AddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                // i : hour, i1 : minutes
                                String strHour;
                                if (i < 10) {
                                    strHour = "0" + i;
                                } else {
                                    strHour = "" + i;
                                }
                                String strMin;
                                if (i1 < 10) {
                                    strMin = "0" + i1;
                                } else {
                                    strMin = "" + i1;
                                }

                                time = strHour + ":" + strMin;
                                btnTime.setText(time);
                            }
                        },
                        current.get(Calendar.HOUR_OF_DAY),
                        current.get(Calendar.MINUTE),
                        true
                ).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = editTitle.getText().toString().trim();
                String content = editContent.getText().toString().trim();

                if (title.isEmpty() || content.isEmpty() || date.isEmpty() || time .isEmpty()){
                    Toast.makeText(AddActivity.this, "모든 항목은 필수 입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String datetime = date + " " + time;

                // 네트워크 호출하는 코드
                showProgress("메모 저장중...");

                Retrofit retrofit = NetworkClient.getRetrofitClient(AddActivity.this);

                MemoApi api = retrofit.create(MemoApi.class);

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String accessToken = sp.getString(Config.ACCESS_TOKEN, "");

                Memo memo = new Memo();
                memo.setTitle(title);
                memo.setContent(content);
                memo.setDatetime(datetime);

                Call<Res> call = api.addMemo("Bearer " + accessToken, memo);

                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        dismissProgress();

                        if (response.isSuccessful()){
                            finish();
                        } else {

                            Toast.makeText(AddActivity.this, "정상동작 하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {
                        dismissProgress();

                    }
                });

            }
        });
    }


    // 네트워크 로직 처리시에 화면에 보여주는 함수
    void showProgress(String message){
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    // 로직처리가 끝나면 화면에서 사라지는 함수
    void dismissProgress(){
        dialog.dismiss();
    }

}
