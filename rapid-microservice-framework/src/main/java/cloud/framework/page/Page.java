package cloud.framework.page;

/**
 * 分页参数对象
 * @author xmc
 */
public class Page<T> {

	/**
	 * 每页显示记录数
	 */
	private Integer pageSize;

	/**
	 * 当前页码
	 */
	private Integer pageNumber;

	/**
	 * 请求参数
	 */
	private T param;

	/**
	 * 总记录数
	 */
	private Integer totalSize;

	public Page() {
	}
	public Page(Integer pageSize, Integer pageNumber, Integer totalSize, T param) {
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.param = param;
		this.totalSize = totalSize;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public T getParam() {
		return param;
	}

	public void setParam(T param) {
		this.param = param;
	}

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}

	@Override
	public String toString() {
		return "Page{" +
				"pageSize=" + pageSize +
				", pageNumber=" + pageNumber +
				", param=" + param +
				", totalSize=" + totalSize +
				'}';
	}

}