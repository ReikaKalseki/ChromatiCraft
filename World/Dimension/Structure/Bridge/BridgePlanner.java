/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Bridge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class BridgePlanner {

	private static class Node {

		private final int ID;
		private final HashMap<Node, Link> bridges = new HashMap();

		private Node(int id) {
			ID = id;
		}

		private Node linkTo(Node n) {
			Link l = new Link(this, n);
			n.bridges.put(this, l);
			bridges.put(n, l);
			return this;
		}

		@Override
		public String toString() {
			return Character.toString(ReikaJavaLibrary.getIDChar(ID));
		}

	}

	private static class Link {

		private final HashSet<UUID> requireSet = new HashSet();
		private final HashSet<UUID> disallowSet = new HashSet();

		private final Node end1;
		private final Node end2;

		private Link(Node n1, Node n2) {
			end1 = n1;
			end2 = n2;
		}

	}

}
