package com.lm.Metadomain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lm.Metadomain.Cell;

/**
 * @Description:单元集合相关的类

 * @author:lm

 * @time:2013-11-6 下午03:53:33

 */
public class CellSet implements Iterable<Cell> {
/***************************属性域***********************************************************************/
	/**单元集合**/
	private List<Cell> cells;

/***************************方法域***********************************************************************/

	/**
	 * @Description construction of CellSet
	 * @exception:
	 */
	public CellSet() {
		cells = new ArrayList<Cell>();
	}

	/**
	 * @Description get size of CellSet
	 * @return
	 */
	public int size() {
		return cells.size();
	}

	/**
	 * @Description add a cell to CellSet
	 * @param c
	 */
	public void addCell(Cell c) {
		cells.add(c);
	}

	/**
	 * @Description get the cell in position index of CellSet
	 * @param index
	 * @return
	 */
	public Cell get(int index) {
		return cells.get(index);
	}

	/**
	 * @Description judge that all cell has been scheduled?
	 * @return a boolean type
	 */
	public boolean isScheduleAll() {
		for (Cell c : cells) {
			if (c.isTransComplete() == false)
				return false;
		}
		return true;
	}

	/**
	 * @Description reset the cell states
	 */
	public void reset() {
		for (Cell c : cells) {
			c.reset();
		}
	}

	/* (non-Javadoc) Override the iterator for cells
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Cell> iterator() {
		return cells.iterator();
	}

}
