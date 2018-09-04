package com.my.blog.website.websocket;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import cn.binarywang.tools.generator.ChineseNameGenerator;
import com.my.blog.website.dao.ChatVoMapper;
import com.my.blog.website.modal.Vo.ChatVo;
import com.my.blog.website.utils.DateKit;
import com.my.blog.website.websocket.utils.ApplicationContextRegister;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@ServerEndpoint(value = "/chatWebSocket/{userId}")
@Component
public class ChatWebSocket {

	//json
	private JSONObject json = null;

	//注入ChatVoMapper
	private ChatVoMapper chatDao = null;

	// 储存用户
	private static ConcurrentHashMap<String, User> users = new ConcurrentHashMap();

	//用户token
	private String userId;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("userId") String userId) {
		this.userId = userId;
		if(users.get(userId) == null) {
			//随机生成用户昵称，并储存
			String username = ChineseNameGenerator.getInstance().generate();
			User user = new User();
			user.setUsername(username);
			user.setSession(session);
			users.put(userId, user);
		}
		sendHistoryMessage(session); // 取出所有历史消息并发送
		sendNotice("\"" + users.get(userId).getUsername() + "\"加入聊天"); // 发送消息
		sendOnlineCount(); // 发送用户数
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		String username = users.get(userId).getUsername(); // 取出用户昵称
		users.remove(userId); // 移除此用户
		sendOnlineCount(); // 发送用户数
		sendNotice("\"" + username + "\"退出聊天"); // 发送通知
	}

	/**
	 * 收到客户端消息后调用的方法
	 * @param message 客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message) {
		if(json == null) {
			json = new JSONObject();
		} else {
			json.clear();
		}
		ChatVo chat = new ChatVo(userId, DateKit.dateFormat(new Date()), users.get(userId).getUsername(), message);
		json.put("chat", chat);
		//发送消息到页面
		sendMessage(json.toJSONString());
		//将消息存入数据库
		if(chatDao == null) {
			chatDao = ApplicationContextRegister.getApplicationContext().getBean(ChatVoMapper.class);
		}
		chatDao.insert(chat);
	}

	/**
	 * 发生错误时调用
	 * 
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		error.printStackTrace();
	}

	/**
	 * 群发自定义消息
	 */
	public void sendMessage(String message)  {
		try {
			for (String key : users.keySet()) {
				User user = users.get(key);
				Session session = user.getSession();
				session.getBasicRemote().sendText(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送在线人数
	 */
	public void sendOnlineCount() {
		if(json == null) {
			json = new JSONObject();
		} else {
			json.clear();
		}
		json.put("OnlineCount", users.size());
		sendMessage(json.toJSONString());
	}

	/**
	 * 发送通知
	 * @param msg
	 */
	public void sendNotice(String msg) {
		if(json == null) {
			json = new JSONObject();
		} else {
			json.clear();
		}
		json.put("notice", msg);
		sendMessage(json.toJSONString());
	}

	/**
	 * 发送历史消息
	 * @param session
	 */
	private void sendHistoryMessage(Session session) {
		try {
			if(chatDao == null) {
				chatDao = ApplicationContextRegister.getApplicationContext().getBean(ChatVoMapper.class);
			}
			List<ChatVo> chatList = chatDao.findAll();
			for (ChatVo chat : chatList) {
				if(json == null) {
					json = new JSONObject();
				} else {
					json.clear();
				}
				json.put("chat", chat);
				session.getBasicRemote().sendText(json.toJSONString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

/**
 * 聊天用户
 */
class User {
	private String username;
	private Session session;
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
}
