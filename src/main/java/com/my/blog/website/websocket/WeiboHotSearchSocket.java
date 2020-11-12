package com.my.blog.website.websocket;

import com.alibaba.fastjson.JSONObject;
import com.my.blog.website.modal.searchHot.SearchHotEntity;
import com.my.blog.website.websocket.config.CustomThreadFactoryBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器系统信息
 *
 * @author SongM
 * @date 2018/10/16
 */
@ServerEndpoint(value = "/weiboHotSearch")
@Component
public class WeiboHotSearchSocket {

	private static WeiboTask weiboTask;
	static {
		String THREAD_NAME = "WeiboSearchHot-Scrapy-Thread";
//		ThreadFactory customThreadFactory = new CustomThreadFactoryBuilder()
//				.setNamePrefix(THREAD_NAME).setDaemon(false)
//				.setPriority(Thread.MAX_PRIORITY).build();
//		ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
//				new LinkedBlockingQueue<>(), customThreadFactory, new ThreadPoolExecutor.AbortPolicy());
//		executorService.execute(weiboTask);
		if (weiboTask == null) {
			weiboTask = new WeiboTask();
			Thread thread = new Thread(weiboTask);
			thread.setName(THREAD_NAME);
			thread.start();
		}
	}

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session) {
		weiboTask.newUser(session);
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
	}

	/**
	 * 发生错误时调用
	 * 
	 */
	@OnError
	public void onError(Throwable e) {
	}
}

/**
 * 获取信息信息线程
 */
class WeiboTask implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(WeiboTask.class);
	private static final String DOMAIN_WEIBO = "http://s.weibo.com";
	private static final long sleepTime = 1000 * 10;

	private JSONObject json = new JSONObject();

	/**
	 * 存在的session
	 */
	private volatile Set<Session> sessions = new HashSet<>();

	public void newUser(Session session) {
		this.sessions.add(session);
	}

	@Override
	public void run() {
		while (true) {
			try {
				int size = sessions.size();
				if (size > 0) {
					json.clear();
					List<SearchHotEntity> dataList = parseWeiboHot();
					json.put("dataList", dataList);
					Iterator<Session> iterator = sessions.iterator();
					for (int i = 0; i < size; i++) {
						Session session = iterator.next();
						if (session.isOpen()) {
							session.getBasicRemote().sendText(json.toJSONString());
						} else {
							sessions.remove(session);
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("微博热搜接口错误：" + e.getMessage());
			} finally {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public List<SearchHotEntity> parseWeiboHot() {
		try {
			long currentTime = System.currentTimeMillis();
			// 通过jsoup将对应url转为document
			Document doc = Jsoup.parse(new URL("http://s.weibo.com/top/summary?cate=realtimehot"), (int) sleepTime);
			// 获取script标签对应的Element list
			Elements tbody = doc.select("tbody");
			List<Node> nodes = tbody.first().childNodes();
			return parseSearchTop(nodes, currentTime);
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private List<SearchHotEntity> parseSearchTop(List<Node> nodes, long currentTime) {
		List<SearchHotEntity> weiboHotEntities = new ArrayList<>();

		// 热搜
		for (int i = 0, size = nodes.size(); i < size; i++) {
			Node node = nodes.get(i);
			if (node.toString().trim().length() > 10) {
				Element element = (Element) node;
				weiboHotEntities.add(parseDetail(element, currentTime));
			}
		}

		return weiboHotEntities;
	}

	private SearchHotEntity parseDetail(Element element, long currentTime) {
		SearchHotEntity searchHotEntity = new SearchHotEntity();

		Elements td01 = element.getElementsByClass("td-01");

		String sortText = td01.text();
		// 排序
		if (sortText.length() > 0) {
			// 获取热度排名，如果没有的话，则是推荐设为0
			searchHotEntity.setSort(Integer.valueOf(sortText));
		} else {
			searchHotEntity.setSort(0);
		}

		// 名称和链接和热度值
		Elements td02 = element.getElementsByClass("td-02");
		if (isListNotEmpty(td02)) {
			Elements a = td02.get(0).getElementsByTag("a");
			Elements span = td02.get(0).getElementsByTag("span");
			if (isListNotEmpty(a)) {
				Element el = a.get(0);
				String href = el.attributes().get("href");
				if ("javascript:void(0);".equals(href)) {
					href = el.attributes().get("href_to");
				}
				searchHotEntity.setLinkUrl(DOMAIN_WEIBO + href);
				searchHotEntity.setTitle(el.text());
			}
			if (isListNotEmpty(span)) {
				searchHotEntity.setNum(Long.valueOf(span.text()));
			} else {
				searchHotEntity.setNum(0L);
			}
		}

		// 标签
		Elements td03 = element.getElementsByClass("td-03");
		if (isListNotEmpty(td03)) {
			searchHotEntity.setTab(td03.text());
		}
		searchHotEntity.setChannel("微博");
		searchHotEntity.setDate(getDateStr(currentTime));
		return searchHotEntity;
	}

	private static boolean isListNotEmpty(List list) {
		return list != null && list.size() > 0;
	}

	private static String getDateStr(long currentTime) {
		Date time = new Date(currentTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(time);
	}
}

