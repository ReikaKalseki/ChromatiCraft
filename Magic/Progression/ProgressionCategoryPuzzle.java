package Reika.ChromatiCraft.Magic.Progression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import Reika.ChromatiCraft.Magic.Progression.FragmentCategorizationSystem.FragmentCategory;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

@Deprecated
public class ProgressionCategoryPuzzle {

	private final Random rand = new Random();

	private final int numberFragments;
	private final int difficultyLevel;
	private final int ringCount;

	private final Node center;
	private final Ring[] rings;
	private final Ring outerRing;

	ProgressionCategoryPuzzle(EntityPlayer ep, int n) {
		rand.setSeed(ep.getUniqueID().hashCode());
		rand.nextBoolean();
		rand.nextBoolean();

		numberFragments = n;
		difficultyLevel = MathHelper.clamp_int(ReikaRandomHelper.getRandomPlusMinus(n/3+this.getProgressionDifficulty(ep), 1, rand), 0, 9);
		ringCount = difficultyLevel/3;

		rings = new Ring[ringCount+1];
		int c = FragmentCategory.list.length;
		for (int i = rings.length-1; i >= 0; i--) {
			double r = i/(double)(rings.length-1);
			rings[i] = new Ring(i, r);
			if (i < rings.length-1) {
				double da = 360D/c;
				for (int k = 0; k < c; k++) {
					double ang = k*da+(da/2D)*((rings.length-1-i)%2);
					rings[i].addNode(new Node(ang, rings[i]));
				}
				c *= 1-0.0625*(rings.length-2);
			}
		}
		center = new Node(0, rings[0]);
		outerRing = rings[rings.length-1];
		rings[0].addNode(center);
		for (FragmentCategory fc : FragmentCategory.list) {
			outerRing.addNode(new Node(fc.getAngle(), outerRing));
		}
	}

	private int getProgressionDifficulty(EntityPlayer ep) {
		if (ProgressStage.DIMENSION.isPlayerAtStage(ep))
			return 6;
		else if (ProgressStage.LINK.isPlayerAtStage(ep) || ProgressStage.REPEATER.isPlayerAtStage(ep))
			return 5;
		else if (ProgressStage.TUNECAST.isPlayerAtStage(ep) || ProgressStage.ENERGYIDEA.isPlayerAtStage(ep))
			return 4;
		else if (ProgressStage.MULTIBLOCK.isPlayerAtStage(ep))
			return 3;
		else if (ProgressStage.RUNEUSE.isPlayerAtStage(ep) || ProgressStage.SHARDCHARGE.isPlayerAtStage(ep))
			return 2;
		else if (ProgressStage.ALLCOLORS.isPlayerAtStage(ep) || ProgressStage.MAKECHROMA.isPlayerAtStage(ep))
			return 1;
		return 0;
	}

	private void generateMesh() {
		/*
		for (Node n : outerRing.nodes) {
			n.connectTo(center);
		}
		for (int i = 0; i < rings.length-1; i++) {

		}
		 */
		for (int i = 1; i < rings.length-1; i++) {
			Ring inner = rings[i];
			Ring outer = rings[i+1];
			for (Node n : inner.nodes.values()) {
				double f = rand.nextDouble(); //determine connectivity with R/G and B/Y color fields?
				int c = 1;
				if (f < 0.15) {
					c = 3;
				}
				else if (f < 0.5) {
					c = 2;
				}
				Collection<Node> li = outer.getNearbyNodes(n, c);
				for (int k = 0; k < c; k++) {
					n.connectTo(ReikaJavaLibrary.getRandomCollectionEntry(rand, li));
				}
			}
		}
	}

	private static class Ring {

		private final int index;
		private final double radius;
		private final TreeMap<Double, Node> nodes = new TreeMap();

		private Ring(int i, double r) {
			index = i;
			radius = r;
		}

		private void addNode(Node node) {
			nodes.put(node.angle, node);
		}

		private Collection<Node> getNearbyNodes(Node n, int min) {
			Collection<Node> c = new ArrayList();
			Entry<Double, Node> n1 = nodes.floorEntry(n.angle);
			if (n1 == null)
				n1 = nodes.floorEntry(n.angle+360);
			Entry<Double, Node> n2 = nodes.ceilingEntry(n.angle);
			if (n2 == null)
				n2 = nodes.ceilingEntry(n.angle-360);
			if (n1 != null) {
				c.add(n1.getValue());
				n1 = nodes.lowerEntry(n1.getKey());
			}
			if (n2 != null) {
				c.add(n2.getValue());
				n2 = nodes.higherEntry(n2.getKey());
			}
			if (min > 2) {
				if (n1 != null) {
					c.add(n1.getValue());
				}
				if (n2 != null) {
					c.add(n2.getValue());
				}
			}
			return c;
		}

	}

	private static class Node {

		private final double angle;
		private final double posX;
		private final double posY;
		private final Ring ring;
		private final HashSet<Node> connections = new HashSet();

		private Node(double ang, Ring r) {
			angle = ang;
			ang = Math.toRadians(ang);
			posX = Math.cos(ang)*r.radius;
			posY = Math.sin(ang)*r.radius;
			ring = r;
		}

		private void connectTo(Node n) {
			connections.add(n);
			n.connections.add(this);
		}

	}

}
