/**
 * 文件名：Account.java
 * 时间：2015年5月9日上午10:23:19
 * 作者：修维康
 */
package com.sap.mim.bean;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Date;

/**
 * 类名：User 说明：账户对象
 */
public class Account implements Externalizable {

	private static final long serialVersionUID = -1086962731178609581L;

	private int     id;            // 用户id
	private String  account;       // 用户账号
	private String  userName;      // 用户名称
	private String  password;      // 用户密码
	private Date    birthday;      // 用户出生日期
	private int     gender;        // 用户性别:0代表女生 1代表男生 2未知
	private boolean isOnline;      // 当前是否在线
	private String  location;      // 用户位置
	private byte[]  photo;		   // 头像
	private int     age;           // 用户年龄
	private String  userBriefIntro;// 用户个性签名

	private ArrayList<Account> friendList;// 用户好友列表

	public Account() {
	}

	public String getUserBriefIntro() {
		return userBriefIntro;
	}

	public void setUserBriefIntro(String userBriefIntro) {
		this.userBriefIntro = userBriefIntro;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public ArrayList<Account> getFriendList() {
		return friendList;
	}

	public void setFriendList(ArrayList<Account> friendList) {
		this.friendList = friendList;
	}


	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public boolean isOnline() {
		return isOnline;
	}
 
	public void setIsOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

	}
}
