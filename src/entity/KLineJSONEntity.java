package entity;

/**
 * K 线图返回数据的 JSON 实体类
 * @author Liulili
 *
 */
public class KLineJSONEntity {
	
	private String time;   //生成图片的时间
	private String type;   //图片对应产品的类型
	private String title;
	private String imgpath;//返回图片路径

	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgpath() {
		return imgpath;
	}

	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
}
