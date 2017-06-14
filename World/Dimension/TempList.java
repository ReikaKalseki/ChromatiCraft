package Reika.ChromatiCraft.World.Dimension;

import java.util.ArrayList;
import java.util.Collection;


public class TempList<E> extends ArrayList {

	@Override
	public boolean remove(Object obj) {
		return super.remove(obj);
	}

	@Override
	public E remove(int index) {
		return (E)super.remove(index);
	}

	@Override
	public boolean removeAll(Collection c) {
		return super.removeAll(c);
	}

}
