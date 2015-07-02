package com.example.mahe.bereans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahe on 20/6/15.
 */
public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private TextView chatName;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private LinearLayout singleMessageContainer;
    ChatDbHelper mDbHelper;
    SQLiteDatabase db;
    long id = -1;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public void addToDb(String message, String name) {
        mDbHelper = new ChatDbHelper(getContext());
        db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ChatContract.NameEntry.COLUMN_NAME_NAME, name);
        values.put(ChatContract.NameEntry.COLUMN_NAME_MESSAGE, message);
        long newRowId;
        newRowId = db.insert(
                ChatContract.NameEntry.TABLE_NAME,
                null,
                values);
//        Toast.makeText(getContext(), "New Entry has been added - " + Long.toString(newRowId), Toast.LENGTH_LONG).show();
    }

    public void addChatList() {
        mDbHelper = new ChatDbHelper(getContext());
        db = mDbHelper.getReadableDatabase();

        String[] projection = {
                ChatContract.NameEntry._ID,
                ChatContract.NameEntry.COLUMN_NAME_NAME,
                ChatContract.NameEntry.COLUMN_NAME_MESSAGE,
        };

        String sortOrder =
                ChatContract.NameEntry._ID + " DESC";

        String selection = ChatContract.NameEntry._ID + " < ? ";

        Cursor cursor;

        if(id == -1) {
            cursor = db.query(
                    ChatContract.NameEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder,
                    "10"
            );
        }
        else {
            String[] selectionArgs = {Long.toString(id)};
            cursor = db.query(
                    ChatContract.NameEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder,
                    "10"
            );
        }

        cursor.moveToFirst();
        while( !cursor.isAfterLast() ) {
            String chat_id = cursor.getString(cursor.getColumnIndexOrThrow(ChatContract.NameEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ChatContract.NameEntry.COLUMN_NAME_NAME));
            String message = cursor.getString(cursor.getColumnIndexOrThrow(ChatContract.NameEntry.COLUMN_NAME_MESSAGE));
            if(name == null)
                name = "Unknown";
            if(name.equals("Me"))
                chatMessageList.add(0, new ChatMessage(false, message, "Me"));
            else
                chatMessageList.add(0, new ChatMessage(true, message, name));

            id = Long.parseLong(chat_id);
            cursor.moveToNext();
        }

        notifyDataSetChanged();

//        Toast.makeText(getContext(), Long.toString(id), Toast.LENGTH_LONG).show();

    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chat_singlemessage, parent, false);
        }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        chatText.setText(chatMessageObj.name + "\n" + chatMessageObj.message);
        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_b : R.drawable.bubble_a);
        singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);

        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public void loadMoredata() {
        addChatList();
//        Toast.makeText(getContext(), "Loading more", Toast.LENGTH_LONG).show();
    }
}
