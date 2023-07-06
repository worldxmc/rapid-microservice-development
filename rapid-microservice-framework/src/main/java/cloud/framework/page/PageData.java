package cloud.framework.page;

/**
 * 分页结果对象
 * @author xmc
 */
public class PageData<T> {

	/**
	 * 总记录数
	 */
	private Integer totalSize;

	/**
	 * 数据内容
	 */
	private T data;

	public PageData() {
	}
	public PageData(Integer totalSize, T data) {
		this.totalSize = totalSize;
		this.data = data;
	}

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "PageData{" +
				", totalSize=" + totalSize +
				", data=" + data +
				'}';
	}

}