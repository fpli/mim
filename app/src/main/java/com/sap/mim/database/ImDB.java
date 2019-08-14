package com.sap.mim.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.sap.mim.bean.Account;
import com.sap.mim.bean.ChatMessage;
import com.sap.mim.bean.MessageTabEntity;
import com.sap.mim.bean.MessageType;
import com.sap.mim.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ImDB {

	public static final String DB_NAME = "mim_local";
	public static final int VERSION = 1;
	private static ImDB imDB;

	private Account account = null;
	private SQLiteDatabase db;

	private ImDB(Context context) {
		ImOpenHelper imOpenHelper = new ImOpenHelper(context, DB_NAME,null, VERSION);
		db = imOpenHelper.getWritableDatabase();
	}

	public static ImDB getInstance(Context context) {
		if (imDB == null){
			synchronized (ImDB.class){
				if (imDB == null){
					imDB = new ImDB(context);
				}
			}
		}
		return imDB;
	}

	public void saveFriend(Account friend) {
		ContentValues values = new ContentValues();
		values.put("userid",    account.getId());
		values.put("friendid",  friend.getId());
		values.put("name",      friend.getUserName());
		values.put("birthday",  friend.getBirthday().toString());
		values.put("photo",     friend.getPhoto());
		db.insert("friend", null, values);
	}

	public List<Account> getAllFriend() {
		List<Account> friends = new ArrayList<>();
		int id = account.getId();
		Cursor cursor = db.rawQuery("select * from friend where userid = " + id, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				Account friend = new Account();
				friend.setId(cursor.getInt(cursor.getColumnIndex("friendid")));
				friend.setUserName(cursor.getString(cursor.getColumnIndex("name")));
				friend.setPhoto(cursor.getBlob(cursor.getColumnIndex("photo")));
				friends.add(friend);
			}
		}

		if (cursor != null){
			cursor.close();
		}
		return friends;
	}

	public void saveMessage(MessageTabEntity message) {
		ContentValues values = new ContentValues();
		values.put("userid",   account.getId());
		values.put("senderid", message.getSenderId());
		values.put("name",     message.getName());
		values.put("content",  message.getContent());
		values.put("sendtime", message.getSendTime());
		values.put("unread",   message.getUnReadCount());
		values.put("type",     message.getMessageType());
		db.insert("message", null, values);
	}

	public List<MessageTabEntity> getAllMessage() {
		List<MessageTabEntity> messages = new ArrayList<>();
		Cursor cursor = db.rawQuery("select * from message where userid = " + account.getId(), null);
		if (cursor != null){
			while (cursor.moveToNext()) {
				MessageTabEntity message = new MessageTabEntity();
				message.setSenderId(cursor.getInt(cursor.getColumnIndex("senderid")));
				message.setName(cursor.getString(cursor.getColumnIndex("name")));
				String time = cursor.getString(cursor.getColumnIndex("sendtime"));
				message.setSendTime(time);
				message.setContent(cursor.getString(cursor.getColumnIndex("content")));
				message.setMessageType(cursor.getInt(cursor.getColumnIndex("type")));
				message.setUnReadCount(cursor.getInt(cursor.getColumnIndex("unread")));
				messages.add(message);
			}
		}
		if (cursor != null){
			cursor.close();
		}
		return messages;
	}

	public void deleteMessage(MessageTabEntity message) {
		String sql = "delete from message where userid = " + account.getId()
				+ " and senderid =" + message.getSenderId() + " and type = "
				+ message.getMessageType();
		db.execSQL(sql);
	}

	public void updateMessages(MessageTabEntity message) {
		String sql = "update message set unread = " + message.getUnReadCount()
				+ ", content = \"" + message.getContent() + "\",sendtime = \""
				+ message.getSendTime() + "\" where userid = " + account.getId()
				+ " and senderid = " + message.getSenderId() + " and type = "
				+ message.getMessageType();
		db.execSQL(sql);
	}

	public void saveChatMessage(ChatMessage message) {
		ContentValues values = new ContentValues();
		values.put("userid", account.getId());
		if (account.getId() == message.getSenderId()) {
			values.put("friendid", message.getReceiverId());
			values.put("type",     Constants.CHAT_ITEM_TYPE_RIGHT);
		} else {
			values.put("friendid", message.getSenderId());
			values.put("type",     Constants.CHAT_ITEM_TYPE_LEFT);
		}
		values.put("content", message.getContent());
		values.put("sendtime", message.getSendTime());
		db.insert("chat_message", null, values);
	}

	public List<ChatMessage> getChatMessage(int friendId) {
		Cursor cursor = db.rawQuery(
				"select * from chat_message where userid = " + account.getId()
						+ " and friendid = " + friendId, null);
		List<ChatMessage> allMessages = new ArrayList<>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				ChatMessage chat = new ChatMessage();
				chat.setContent(cursor.getString(cursor.getColumnIndex("content")).getBytes());
				chat.setMessageType(MessageType.getMessageTypeById(cursor.getInt(cursor.getColumnIndex("type"))));
				chat.setSendTime(cursor.getString(cursor.getColumnIndex("sendtime")));
				allMessages.add(chat);
			}
		}

		if (cursor != null){
			cursor.close();
		}

		return  allMessages;
	}
}
