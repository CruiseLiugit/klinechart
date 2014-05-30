package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ListTools {

	/**
	 * 把 List 中的 重复对象，经过 Set 过滤一下
	 * @param list
	 * @return
	 */
	public static List removeDuplicateWithOrder(List list) {
		Set set = new HashSet();
		List newList = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				
				
				newList.add(element);
		}
		return newList;
	}

}
