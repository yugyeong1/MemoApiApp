package com.dbrud1032.memoapp.adapter;

// 1. RecyclerView.Adapter 를 상속받는다.

// 2. 상속받은 클래스가 abstract 이므로, unimplemented method 오버라이드!
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dbrud1032.memoapp.EditActivity;
import com.dbrud1032.memoapp.MainActivity;
import com.dbrud1032.memoapp.R;
import com.dbrud1032.memoapp.model.Memo;

import java.util.ArrayList;

// 6. RecyclerView.Adapter 의 데이터 타입을 적어주어야 한다.
//    우리가 만든 ViewHolder 로 적는다.


// 7. 빨간색 에러가 뜨면, 우리가 만든 ViewHolder 로
// onCreateViewHolder, onBindViewHolder 변경해준다.

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

// 5. 어댑터 클래스의 멤버변수와 생성자를 만들어 준다.
    Context context;
    ArrayList<Memo> memoList;

    public MemoAdapter(Context context, ArrayList<Memo> memoList) {
        this.context = context;
        this.memoList = memoList;
    }

    // 8. 오버라이드 함수 3개를 전부 구현 해주면 끝!

    @NonNull
    @Override
    public MemoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // xml 파일을 연결하는 작업
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_row, parent, false);
        return new MemoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoAdapter.ViewHolder holder, int position) {
        Memo memo= memoList.get(position);

        holder.txtTitle.setText(memo.getTitle());
        //  "2023-06-12T00:00:00"
        //  "2023-06-12 00:00"
        String date = memo.getDatetime().replace("T", " ")
                .substring(0, 15+1);

        holder.txtDate.setText(date);
        holder.txtContent.setText(memo.getContent());

    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle;
        TextView txtDate;
        TextView txtContent;
        ImageView imgDelete;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtContent = itemView.findViewById(R.id.txtContent);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    Memo memo = memoList.get(index);

                    Intent intent = new Intent(context, EditActivity.class);

                    intent.putExtra("memo", memo);

//                    intent.putExtra("id",contact.id);
//                    intent.putExtra("name", contact.name);
//                    intent.putExtra("phone", contact.phone);

                    context.startActivity(intent);

                }
            });

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("삭제");
                    builder.setMessage("정말 삭제하시겠습니까?");
                    builder.setNegativeButton("NO", null);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int index = getAdapterPosition();

                            ((MainActivity)context).deleteMemo(index);

                        }
                    });
                    builder.show();
                }
            });

        }
    }
}
