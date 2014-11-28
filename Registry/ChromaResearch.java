package Reika.ChromatiCraft.Registry;

import Reika.DragonAPI.Instantiable.Data.ObjectWeb;

public class ChromaResearch {

	private final ObjectWeb<ResearchStep> data = new ObjectWeb();

	public static final ChromaResearch instance = new ChromaResearch();

	private ChromaResearch() {
		this.addLink("Test", ResearchLevel.ENTRY, null);
	}

	private void addLink(String s, ResearchLevel rl, ResearchStep... parents) {
		ResearchStep rs = new ResearchStep(s, rl);
		data.addNode(rs);
		for (int i = 0; i < parents.length; i++)
			data.addDirectionalConnection(parents[i], rs);
	}

	public static final class ResearchStep implements Comparable<ResearchStep> { //need some way to tie with handbook and general crafting, preferably automatically

		public final String name;
		public final ResearchLevel stage;

		private ResearchStep(String s, ResearchLevel rl) {
			name = s;
			stage = rl;
		}

		@Override
		public int compareTo(ResearchStep o) {
			return this.isParent(o) ? Integer.MAX_VALUE : o.isParent(this) ? Integer.MIN_VALUE : 0;
		}

		private boolean isParent(ResearchStep o) {
			return ChromaResearch.instance.data.getChildren(o).contains(this);
		}

	}

	public static enum ResearchLevel {
		ENTRY(),
		RAWEXPLORE(),
		STARTCRAFT(),
		BASICCRAFT(),
		ENERGYEXPLORE(),
		RUNECRAFT(),
		CHARGESELF(), //?
		MULTICRAFT(),
		NETWORKING(),
		PYLONCRAFT(),
		ENDGAME();
	}

}
