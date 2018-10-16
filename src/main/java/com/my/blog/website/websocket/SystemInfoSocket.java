package com.my.blog.website.websocket;

import com.alibaba.fastjson.JSONObject;
import com.my.blog.website.utils.SystemInfo;
import org.hyperic.sigar.Sigar;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * 服务器系统信息
 *
 * @author SongM
 * @date 2018/10/16
 */
@ServerEndpoint(value = "/systemInfo")
@Component
public class SystemInfoSocket {

//	private static final Logger LOGGER = LoggerFactory.getLogger(SystemInfoSocket.class);

	private MeThread meThread;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session) {
		meThread = new MeThread(session);
		Thread thread = new Thread(meThread);
		thread.setName("system-info-thread");
		thread.start();
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		if (meThread != null) {
			meThread.toEnd();
		}
	}

	/**
	 * 发生错误时调用
	 * 
	 */
	@OnError
	public void onError(Throwable e) {
		if (meThread != null) {
			meThread.toEnd();
		}
		// LOGGER.error(e.getMessage(), e);
	}

}

/**
 * 获取信息信息线程
 */
class MeThread implements Runnable {

	private JSONObject json = new JSONObject();
	private volatile boolean status = true;
	private Session session;

	public synchronized void toEnd() {
		this.status = false;
	}

	public MeThread(Session session) {
		this.session = session;
	}

	@Override
	public void run() {
		while (status) {
			json.clear();
			json.put("serverinfo", SystemInfo.SystemProperty());
			Sigar sigar = new Sigar();
			json.put("memory", SystemInfo.memory(sigar));
			json.put("cpuInfos", SystemInfo.cpuInfos(sigar));
			json.put("diskInfos", SystemInfo.diskInfos(sigar));
			try {
				if (session.isOpen()) {
					session.getBasicRemote().sendText(json.toJSONString());
				}
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}
}

