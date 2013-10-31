package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Sep 9, 2009 9:50:41 AM @ [PaginationList]
 */
public class Pagination {

	public List recordList = new ArrayList(); // all records
	public long recordCount = 0; // record count
	public long pageCount = 0; // page count
	public int pageSize = 5; // page size
	public int currentPage = 0; // current page

	public Pagination() {
	}

}
