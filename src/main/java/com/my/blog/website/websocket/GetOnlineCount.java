package com.my.blog.website.websocket;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统计在线人数
 */
@ServerEndpoint(value = "/onlineCount/{userId}")
@Component
public class GetOnlineCount {

	//json
	private JSONObject json = null;

	// 储存用户
	private static ConcurrentHashMap<String, Session> users = new ConcurrentHashMap();

	//用户token
	private String userId;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("userId") String userId) {
		this.userId = userId;
		users.put(userId, session); //存入用户
		sendOnlineCount(); // 发送用户数
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		users.remove(userId); // 移除此用户
		sendOnlineCount(); // 发送用户数
	}

	/**
	 * 发生错误时调用
	 * 
	 */
	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	/**
	 * 群发自定义消息
	 */
	public void sendMessage(String message)  {
		try {
			for (String key : users.keySet()) {
				Session session = users.get(key);
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
		json.put("onlineCount", users.size());
		sendMessage(json.toJSONString());
	}

}

