package com.my.blog.website.websocket;

import com.alibaba.fastjson.JSONObject;
import com.my.blog.website.utils.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统计在线人数
 *
 * @author SongM
 * @date 2017/05/25
 */
@ServerEndpoint(value = "/onlineCount/{userId}")
@Component
public class GetOnlineCount {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetOnlineCount.class);

	/**
	 * json
	 */
	private JSONObject json = null;

	/**
	 * 储存用户
	 */
	private static ConcurrentHashMap<String, Session> users = new ConcurrentHashMap();

	/**
	 * 用户token
	 */
	private String userId;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("userId") String userId) {
		if (userId == null || "undefined".equals(userId)) {
			try {
				json = new JSONObject();
				json.put("onlineCount", -1);
				json.put("token", UUID.UU64());
				session.getBasicRemote().sendText(json.toJSONString());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				return;
			}
		}
		this.userId = userId;
		// 存入用户
		users.putIfAbsent(userId, session);
		// 发送用户数
		sendOnlineCount();
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		// 移除此用户
		users.remove(userId);
		// 发送用户数
		sendOnlineCount();
	}

	/**
	 * 发生错误时调用
	 * 
	 */
	@OnError
	public void onError(Throwable e) {
		// LOGGER.error(e.getMessage(), e);
	}

	/**
	 * 群发自定义消息
	 */
	public void sendMessage(String message)  {
		try {
			for (String key : users.keySet()) {
				Session session = users.get(key);
				if (session.isOpen()) {
					session.getBasicRemote().sendText(message);
				}
			}
		} catch (IOException e) {
			//LOGGER.error(e.getMessage(), e);
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

