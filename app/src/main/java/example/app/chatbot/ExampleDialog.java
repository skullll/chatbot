package example.app.chatbot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ExampleDialog extends AppCompatDialogFragment {

    private EditText edithr;
    private EditText editmin;
    private EditText editTitle;
    private ExampleDialogListener listener ;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);

        builder.setView(view)
                .setTitle("Set Reminder")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String EDT = editTitle.getText().toString();
                String Shr = edithr.getText().toString();
                String Smin = editmin.getText().toString();
                int hr = new Integer(Shr).intValue();
                int min = new Integer(Smin).intValue();
                listener.applyTexts(EDT,hr,min);

            }
        });

        editTitle = view.findViewById(R.id.edittitle);
        edithr = view.findViewById(R.id.edit_hr);
        editmin = view.findViewById(R.id.edit_min);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        }catch (ClassCastException e){
            throw new  ClassCastException(context.toString() +
                    "must implement exampledialoglisener");
        }
    }

    public  interface  ExampleDialogListener{
        void  applyTexts(String EDT,int hr, int min);
    }
}
