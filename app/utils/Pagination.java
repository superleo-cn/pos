package utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * @ Sep 9, 2009 9:50:41 AM @ [PaginationList]
 */
public class Pagination {

	@Expose
	public List recordList = new ArrayList(); // all records

	@Expose
	public long recordCount = 0; // record count

	@Expose
	public long pageCount = 0; // page count

	@Expose
	public int pageSize = 5; // page size

	@Expose
	public int currentPage = 0; // current page
	
	@Expose
	public Boolean all = false;
	
	@Expose
	public Boolean zh = false;

	@Expose
	public long iTotalDisplayRecords = 0;
	
	@Expose
	public long iTotalRecords = 0;

	public Pagination() {
	}

}
