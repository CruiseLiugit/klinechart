package entity;

/*
 {
 "Open" : "4001.0",  开盘价格
 "Code" : "XAGUSD",
 "id" : "279537",   
 "High" : "4002.0",  最高价
 "Low" : "4001.0",   最低价
 "Close" : "4001.0", 收盘价
 "OpenTime" : "2014-01-20 21:35:00"  成交时间
 }
 */
public class KLine {

	private String id;
	private String code;
	private String open;
	private String high;
	private String opentime;
	private String low;
	private String close;

	public String getOpen() {
		return this.open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getHigh() {
		return this.high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLow() {
		return this.low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getClose() {
		return this.close;
	}

	public void setClose(String close) {
		this.close = close;
	}

	public String getOpentime() {
		return opentime;
	}

	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}

	public String toString(){  
	      return "<id:"+this.id+",code:"+this.code+",opentime:"+this.opentime+",open:"+this.open+",high:"+this.high+",low:"+this.low+",close:"+this.close+">";  
	}  
	      
    
	@Override
	public boolean equals(Object obj) {
		//比较两个对象中所有值是否一样
		if (obj == null) {  
            return false;  
        }  
        if (this == obj) {  
            return true;  
        }  
		
		KLine line = (KLine)obj;
		//时间点一样的，去掉
		if (this.getOpentime().trim().equals(line.getOpentime().trim())) {  
            return true;  
        }  
        return false; 		
	}

}
